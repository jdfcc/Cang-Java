package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jdfcc
 * @since 2023-1-15
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {
    Result SecKillVoucher(Long voucherId);
}
