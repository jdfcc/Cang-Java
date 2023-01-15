package com.hmdp.mapper;

import com.hmdp.entity.SeckillVoucher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 Mapper 接口
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-15
 */
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {

    @Update("update tb_seckill_voucher set stock=stock-1 where voucher_id=#{voucher_id} and stock >0;")
    boolean updateVoucher(@Param("voucher_id") Long voucher_id);


}
