package com.Cang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.Cang.dto.Result;
import com.Cang.entity.Voucher;
import com.Cang.mapper.VoucherMapper;
import com.Cang.entity.SeckillVoucher;
import com.Cang.service.ISeckillVoucherService;
import com.Cang.service.IVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.Cang.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-2
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 查询并筛选出未过期的优惠券
     * @param shopId
     * @return
     */
    @Override
    public Result queryVoucherOfShop(Long shopId) {
        // 查询优惠券信息
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        List<Voucher> list = new ArrayList<>();
        for (Voucher voucher : vouchers) {
           if(LocalDateTime.now().isBefore(voucher.getEndTime())&&
           LocalDateTime.now().isAfter(voucher.getBeginTime()))
               list.add(voucher);
        }
        // 返回结果
        return Result.ok(list);
    }

    /**
     * 将优惠券存进redis以及数据库
     *
     * @param voucher
     */
    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        redisTemplate.opsForValue()
                .set(SECKILL_STOCK_KEY + voucher.getId(), String.valueOf(voucher.getStock()));
        Duration duration = Duration.between(LocalDateTime.now(), voucher.getEndTime());
        redisTemplate.expire(SECKILL_STOCK_KEY + voucher.getId(), duration.toMinutes(), TimeUnit.MINUTES);
    }
}
