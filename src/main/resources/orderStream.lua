---
--- Generated by Luanalysis
--- Created by Jdfcc.
--- DateTime: 2023/2/6 21:11
---
local userId=ARGV[1]
local voucherId=ARGV[2]
local orderId=ARGV[3]

-- 订单id
local orderKey='Cang:seckill:order:'..voucherId
-- 库存id
local stockKey='Cang:seckill:stock:'..voucherId

-- 库存为0
if(tonumber(redis.call('get',stockKey))==0) then
    return 1
end

-- 已买
if(redis.call('sismember',orderKey,userId)==1) then
    return 2
end

-- 库存减一
redis.call('incrby',stockKey,-1)
-- 将用户加至购买列表
redis.call('sadd',orderKey,userId)

-- 检查 stream.orders 是否存在
if (redis.call('exists', 'stream.orders') == 0) then
    -- 创建 stream.orders 队列
    redis.call('xadd','stream.orders','*','userId',userId,'voucherId',voucherId,'id',orderId)
    -- 创建名为 g1 的订阅组
    redis.call('xgroup', 'create', 'stream.orders', 'g1', '0', 'MKSTREAM')
    return 0
end

-- 发送至消息队列
redis.call('xadd','stream.orders','*','userId',userId,'voucherId',voucherId,'id',orderId)
return 0