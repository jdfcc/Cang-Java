package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.SeckillVoucherMapper;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.SimpleRedisLock;
import com.hmdp.utils.UserHolder;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hmdp.utils.RedisConstants.CACHE_VOUCHER_ORDER_KEY;
import static com.hmdp.utils.SystemConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-15
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Autowired
    private SeckillVoucherMapper voucherMapper;
    @Autowired
    private RedisIdWorker redisIdWorker;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> VOUCHER_SCRIPT;

    private IVoucherOrderService proxy;
    private BlockingQueue<VoucherOrder> queue = new ArrayBlockingQueue<>(1024 * 1024);
    ExecutorService SECKILL_VOUCHER_ORDER = Executors.newSingleThreadExecutor();

    /**
     * lua脚本的初始化
     */
    static {
        VOUCHER_SCRIPT = new DefaultRedisScript<>();
        VOUCHER_SCRIPT.setLocation(new ClassPathResource("order.lua"));
        VOUCHER_SCRIPT.setResultType(Long.class);
    }

    @PostConstruct
    private void init() {
        SECKILL_VOUCHER_ORDER.submit(new voucherOrderHandler());
    }

    /**
     * 处理订单
     */
    private class voucherOrderHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    VoucherOrder order = queue.take();
                    orderHandler(order);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void orderHandler(VoucherOrder order) {
        Long userId = order.getUserId();
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

    @Override
    public Result SecKillVoucher(Long voucherId) {
        Long id = UserHolder.getUser().getId();
        Long result = redisTemplate.execute(VOUCHER_SCRIPT,
                Collections.emptyList(), id.toString(), voucherId.toString());
        if (result != 0)
            return Result.fail(VOUCHER_ERROR);
        Long orderId = redisIdWorker.nextId("secKillVoucher");
        VoucherOrder order = new VoucherOrder();
        order.setVoucherId(voucherId);
        order.setUserId(id);
        order.setId(orderId);
        queue.add(order);
        proxy = (IVoucherOrderService) AopContext.currentProxy();
        return Result.ok(orderId);
    }


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

    @NotNull
    @Transactional
    public void createVoucherOrder(VoucherOrder order) {
        LambdaUpdateWrapper<VoucherOrder> voucherOrderWrapper = new LambdaUpdateWrapper<>();
        Long voucherId = order.getVoucherId();
        Long userId = order.getUserId();
        voucherOrderWrapper.eq(VoucherOrder::getVoucherId, voucherId)
                .eq(VoucherOrder::getUserId, userId);
        int count = this.count(voucherOrderWrapper);
        if (count > 0)
            return;
        boolean flag = voucherMapper.updateVoucher(voucherId);
        if (!flag)
            return;
        String KEY_PREFIX = "secKillVoucher";
        Long orderId = redisIdWorker.nextId(KEY_PREFIX);
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        this.save(voucherOrder);
    }

}
