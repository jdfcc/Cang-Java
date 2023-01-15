package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.Voucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherMapper;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.hmdp.utils.SystemConstants.VOUCHER_ERROR;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-15
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    private final String KEY_PREFIX="secKillVoucher";
    @Autowired
    private ISeckillVoucherService seckillVoucherService;
    @Autowired
    private RedisIdWorker redisIdWorker;
    @Transactional
    @Override
    public Result SecKillVoucher(Long voucherId){
        LambdaQueryWrapper<SeckillVoucher> secKillWrapper=new LambdaQueryWrapper<>();
        secKillWrapper.eq(SeckillVoucher::getVoucherId,voucherId);
        SeckillVoucher secKillVoucher = seckillVoucherService.getOne(secKillWrapper);
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(secKillVoucher.getEndTime()))
            return Result.fail(VOUCHER_ERROR);
        if(now.isBefore(secKillVoucher.getBeginTime()))
            return Result.fail(VOUCHER_ERROR);
        if(secKillVoucher.getStock()<1)
            return Result.fail(VOUCHER_ERROR);
        secKillVoucher.setStock(secKillVoucher.getStock()-1);
        seckillVoucherService.update(secKillVoucher,secKillWrapper);
        Long orderId = redisIdWorker.nextId(KEY_PREFIX);
        Long userId= UserHolder.getUser().getId();
        VoucherOrder voucherOrder=new VoucherOrder();
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        this.save(voucherOrder);
        return Result.ok(orderId);
    }
}
