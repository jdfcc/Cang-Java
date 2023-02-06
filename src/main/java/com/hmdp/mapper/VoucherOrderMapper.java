package com.hmdp.mapper;

import com.hmdp.entity.VoucherOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author jdfcc
 * @since 2023-2-6
 */
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {
    @Select("select count(*) from tb_voucher_order where voucher_id=#{voucher_id} and user_id=1010;")
    int getCount(@Param("voucher_id") Long voucher_id, @Param("user_id") Long user_id);

}
