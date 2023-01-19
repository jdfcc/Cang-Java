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
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.hmdp.utils.RedisConstants.CACHE_VOUCHER_ORDER_LOCK_TTL;
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

    @Override
    public Result SecKillVoucher(Long voucherId) {
        LambdaQueryWrapper<SeckillVoucher> secKillWrapper = new LambdaQueryWrapper<>();
        secKillWrapper.eq(SeckillVoucher::getVoucherId, voucherId).gt(SeckillVoucher::getStock, 0);
        SeckillVoucher secKillVoucher = voucherMapper.selectOne(secKillWrapper);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(secKillVoucher.getEndTime())) {
            return Result.fail(VOUCHER_ERROR);
        }
        if (now.isBefore(secKillVoucher.getBeginTime())) {
            return Result.fail(VOUCHER_ERROR);
        }
        if (secKillVoucher.getStock() < 1) {
            return Result.fail(VOUCHER_ERROR);
        }

        Long id = UserHolder.getUser().getId();
        SimpleRedisLock simpleRedisLock = null;
        IVoucherOrderService proxy = null;
        try {
            simpleRedisLock = new SimpleRedisLock(redisTemplate, String.valueOf(id));
            boolean isLock = simpleRedisLock.tryLock(CACHE_VOUCHER_ORDER_LOCK_TTL);
            if (!isLock) {
                return Result.fail(VOUCHER_ERROR_WAIT);
            }
            proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucher(voucherId);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        } finally {
            simpleRedisLock.unLock();
        }
    }

    @NotNull
    @Transactional
    public Result createVoucher(Long voucherId) {
        LambdaUpdateWrapper<VoucherOrder> voucherOrderWrapper = new LambdaUpdateWrapper<>();
        voucherOrderWrapper.eq(VoucherOrder::getVoucherId, voucherId)
                .eq(VoucherOrder::getUserId, UserHolder.getUser().getId());
        int count = this.count(voucherOrderWrapper);
        if (count > 0)
            return Result.fail(VOUCHER_ERROR_ALREADY_BUY);
        boolean flag = voucherMapper.updateVoucher(voucherId);
        if (!flag)
            return Result.fail(VOUCHER_ERROR);
        String KEY_PREFIX = "secKillVoucher";
        Long orderId = redisIdWorker.nextId(KEY_PREFIX);
        Long userId = UserHolder.getUser().getId();
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        this.save(voucherOrder);
        return Result.ok(orderId);
    }

}
