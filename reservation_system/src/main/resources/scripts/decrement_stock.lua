local stock_key = KEYS[1]
local raw_amount = ARGV[1]

-- 패턴 매칭을 좀 더 엄격하게: 문자열 내의 모든 숫자 조합을 찾음
local amount_str = string.match(raw_amount, "%d+")
local amount = tonumber(amount_str)

local current_stock_raw = redis.call('get', stock_key)
local current_stock = tonumber(current_stock_raw)

-- 디버깅용 로그 (Redis log에서 확인 가능)
-- redis.log(redis.LOG_NOTICE, "Amount: " .. (amount_str or "nil"))

if not current_stock or not amount or current_stock < amount then
    return 0
end

redis.call('decrby', stock_key, amount)
return 1