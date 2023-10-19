package com.Cang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.Cang.exception.EmptyUserHolderException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.Cang.dto.Result;
import com.Cang.entity.VoucherOrder;
import com.Cang.mapper.SeckillVoucherMapper;
import com.Cang.mapper.VoucherOrderMapper;
import com.Cang.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Cang.utils.RedisIdWorker;
import com.Cang.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static com.Cang.constants.RedisConstants.CACHE_VOUCHER_ORDER_KEY;
import static com.Cang.constants.SystemConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-15
 */
@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    private final SeckillVoucherMapper secKillvoucherMapper;

    private final VoucherOrderMapper voucherOrderMapper;
    private final RedisIdWorker redisIdWorker;
    private final StringRedisTemplate redisTemplate;

    private final RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> VOUCHER_SCRIPT;

    private IVoucherOrderService proxy;


    /*
      lua脚本的初始化
     */
    static {
        VOUCHER_SCRIPT = new DefaultRedisScript<>();
//        VOUCHER_SCRIPT.setLocation(new ClassPathResource("order.lua"));
        VOUCHER_SCRIPT.setLocation(new ClassPathResource("orderStream.lua"));
        VOUCHER_SCRIPT.setResultType(Long.class);
    }

    public VoucherOrderServiceImpl(SeckillVoucherMapper secKillvoucherMapper, VoucherOrderMapper voucherOrderMapper, RedisIdWorker redisIdWorker, StringRedisTemplate redisTemplate, RedissonClient redissonClient) {
        this.secKillvoucherMapper = secKillvoucherMapper;
        this.voucherOrderMapper = voucherOrderMapper;
        this.redisIdWorker = redisIdWorker;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @PostConstruct
    private void init() {
        log.info("线程启动");
        Thread thread = new Thread(new VoucherOrderHandler());
        thread.start();
        log.info("线程启动");
    }

    /**
     * 从消息队列里面取出信息并处理订单
     */
    private class VoucherOrderHandler implements Runnable {
        String queueName = "stream.orders";

        @Override
        public void run() {

            while (true) {
//                log.info("读取信息");
                try {
//                    从消息队列中读取消息
                    List<MapRecord<String, Object, Object>> message = getMessage();
//                    消息为空，继续读取
                    if (message == null || message.isEmpty()) {
                        continue;
                    }
//                    读取成功，创建订单
                    handleMessage(message);
                } catch (Exception e) {
                    handlePendingList();
                }
            }

        }

        private void handlePendingList() {
            while (true) {
                try {
//                    从消息队列中读取消息
                    List<MapRecord<String, Object, Object>> message = getMessage();
//                    异常消息为空，退出
                    if (message == null || message.isEmpty()) {
                        return;
                    }
                    //                    读取成功，创建订单
                    handleMessage(message);
                } catch (Exception e) {
//                    log.info("unKnow Exception");
                    e.printStackTrace();
                }
            }
        }

        void handleMessage(List<MapRecord<String, Object, Object>> message) {
            MapRecord<String, Object, Object> map = message.get(0);
            Map<Object, Object> value = map.getValue();
            VoucherOrder order = BeanUtil.fillBeanWithMap(value, new VoucherOrder(), true);
            orderHandler(order);
//                    ack确认
//                    log.info("@@@@ {}", map.getId());
            redisTemplate.opsForStream().acknowledge(queueName, "g1", map.getId());
        }

        List<MapRecord<String, Object, Object>> getMessage() {
//            TODO rabbitmq实现
//            return redisTemplate.opsForStream().read(Consumer.from("g1", "c1"),//设置订阅组以及订阅者
//                    StreamOffset.create(queueName, ReadOffset.lastConsumed())//消息队列
//            );
            return Collections.emptyList();
        }

    }


//    /**
//     * 从消息队列里面取出信息并处理订单
//     */
//    private class voucherOrderHandler implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    VoucherOrder order = queue.take();
//                    orderHandler(order);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }

    public void orderHandler(VoucherOrder order) {
//        判断此用户是否已经买过此优惠券，若已买过则不予售卖
        LambdaUpdateWrapper<VoucherOrder> voucherOrderWrapper = new LambdaUpdateWrapper<>();
        Long voucherId = order.getVoucherId();
        Long userId = order.getUserId();
        voucherOrderWrapper.eq(VoucherOrder::getVoucherId, voucherId).eq(VoucherOrder::getUserId, userId);
        int count = voucherOrderMapper.getCount(voucherId, userId);
        if (count > 0) {
//            log.info("count>0");
            return;
        }

        RLock lock = redissonClient.getLock(CACHE_VOUCHER_ORDER_KEY + userId);
        try {
            boolean isLock = lock.tryLock();
            if (!isLock) {
                return;
            }
            return;
        } finally {
            proxy.createVoucherOrder(order);
            lock.unlock();
        }
    }

    /**
     * 生成订单id并往消息队列里面发送订单信息
     */
    @Override
    public Result SecKillVoucher(Long voucherId) {
        Long id = null;
        try {
            id = UserHolder.getUser().getId();
        } catch (EmptyUserHolderException e) {
            e.printStackTrace();
        }
        Long orderId = redisIdWorker.nextId("secKillVoucher");
        Long result = redisTemplate.execute(VOUCHER_SCRIPT, Collections.emptyList(), id.toString(), voucherId.toString(), orderId.toString());
        if (result != 0) {
//            log.info("@@@@@@ {}", result);
            return Result.fail(VOUCHER_ERROR);
        }
        proxy = (IVoucherOrderService) AopContext.currentProxy();
        return Result.ok(orderId);
    }

//    @Override
//    public Result SecKillVoucher(Long voucherId) {
//        Long id = UserHolder.getUser().getId();
//        Long result = redisTemplate.execute(VOUCHER_SCRIPT,
//                Collections.emptyList(), id.toString(), voucherId.toString());
//        if (result != 0)
//            return Result.fail(VOUCHER_ERROR);
//        Long orderId = redisIdWorker.nextId("secKillVoucher");
//        VoucherOrder order = new VoucherOrder();
//        order.setVoucherId(voucherId);
//        order.setUserId(id);
//        order.setId(orderId);
//        queue.add(order);
//        proxy = (IVoucherOrderService) AopContext.currentProxy();
//        return Result.ok(orderId);
//    }


    //    @Override
//    public Result SecKillVoucher(Long voucherId) {
//        LambdaQueryWrapper<SeckillVoucher> secKillWrapper = new LambdaQueryWrapper<>();
//        secKillWrapper.eq(SeckillVoucher::getVoucherId, voucherId).gt(SeckillVoucher::getStock, 0);
//        SeckillVoucher secKillVoucher = voucherMapper.selectOne(secKillWrapper);
//        LocalDateTime now = LocalDateTime.now();
//        if (now.isAfter(secKillVoucher.getEndTime())) {
//            return Result.fail(VOUCHER_ERROR);
//        }
//        if (now.isBefore(secKillVoucher.getBeginTime())) {
//            return Result.fail(VOUCHER_ERROR);
//        }
//        if (secKillVoucher.getStock() < 1) {
//            return Result.fail(VOUCHER_ERROR);
//        }
//
//        Long id = UserHolder.getUser().getId();
////        SimpleRedisLock simpleRedisLock = null;
//        RLock lock = redissonClient.getLock(CACHE_VOUCHER_ORDER_KEY+id);
//
//        IVoucherOrderService proxy = null;
//        try {
////            simpleRedisLock = new SimpleRedisLock(redisTemplate, String.valueOf(id));
////            boolean isLock = simpleRedisLock.tryLock(CACHE_VOUCHER_ORDER_LOCK_TTL);
//            boolean isLock = lock.tryLock();
//            if (!isLock) {
//                return Result.fail(VOUCHER_ERROR_WAIT);
//            }
//            proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.createVoucher(voucherId);
//        } finally {
//            lock.unlock();
//        }
//    }


    @Override
    @Transactional
    public void createVoucherOrder(VoucherOrder order) {
        Long voucherId = order.getVoucherId();
        Long userId = order.getUserId();
        boolean flag = secKillvoucherMapper.updateVoucher(voucherId);
        if (!flag) {
            log.info("flag");
            return;
        }
        String KEY_PREFIX = "secKillVoucher";
        Long orderId = redisIdWorker.nextId(KEY_PREFIX);
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
//       TODO 感觉此处可以使用redis加锁
//        TODO 秒杀订单可以采用延迟队列+死信队列实现超时未支付自动取消
        this.save(voucherOrder);
        log.info("ok");
    }

}

