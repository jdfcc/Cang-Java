---
--- Generated by Luanalysis
--- Created by Jdfcc.
--- DateTime: 2023/1/19 16:55
---

if (redis.call('get', KEYS[1]) == ARGV[1]) then
    return redis.call('del', KEYS[1])
end
return 0