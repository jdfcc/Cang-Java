package com.Cang.service;

import com.Cang.dto.Result;
import com.Cang.entity.VoucherOrder;
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
    Result secKillVoucher(Long voucherId);

    void createVoucherOrder(VoucherOrder order);
}
