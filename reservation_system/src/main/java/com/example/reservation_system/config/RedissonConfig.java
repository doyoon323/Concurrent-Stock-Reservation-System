package com.example.reservation_system.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class RedissonConfig {
    @Bean /// 스프링이 시작될 때 이 메서드를 실행해서 객체를 미리 만든다. 
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://redis:6379");
        return Redisson.create(config);
    }


    @Bean
    public String stockDecrementScript() throws IOException {
        //파일 읽어서 String으로 반환 
        return new String(new ClassPathResource("scripts/decrement_stock.lua").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}

// redis 서버와 대화를 위한 전화기 설정
/// 서버 1대짜리 reids 쓸거임. localdml 6379에 접속해줘. 


///  서버가 처음 뜰 때, Lua Script를 보내서 SHA1 암호화 알고리즘을 통해 fingerprint만든다
///  이후부터 SHA1 기억해서 실행하라는 해시값만 보냄