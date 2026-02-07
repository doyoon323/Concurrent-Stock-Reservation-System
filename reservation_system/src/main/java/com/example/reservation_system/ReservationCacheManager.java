package com.example.reservation_system;

import java.util.Arrays;
import java.util.Collections;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ReservationCacheManager {
    private final RedissonClient redissonClient;
    private final String stockDecrementScript;


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean decrementStock(Long productId, int amount){
        Long result = redissonClient.getScript().eval(
            RScript.Mode.READ_WRITE,
            stockDecrementScript,
            RScript.ReturnType.INTEGER,
            Collections.singletonList("stock:product:"+productId),
            String.valueOf(amount)
            );

        return result != null && result == 1;
    }
}
