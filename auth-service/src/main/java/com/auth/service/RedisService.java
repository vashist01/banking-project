package com.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate  redisTemplate;
    private static final String TOKEN_PREFIX = "token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void storeToken(String token, String email) {
        String key = TOKEN_PREFIX +token;
        redisTemplate.opsForValue().set(key,email,24, TimeUnit.HOURS);
        log.debug("Token stored in Redis for user: {}", email);
    }

    public boolean isTokenBlackListed(String token) {
        String key = BLACKLIST_PREFIX+token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));

    }

    public long getTokenExpiration(String token) {
        String key = TOKEN_PREFIX + token;
        Long expiration = redisTemplate.getExpire(key,TimeUnit.SECONDS);
        return expiration!=null ? expiration :0;
    }

    public void blackListToken(String token, long expiration) {
        String key = BLACKLIST_PREFIX+token;
        redisTemplate.opsForValue().set(key,"blacklisted",expiration,TimeUnit.SECONDS);
        redisTemplate.delete(TOKEN_PREFIX+token);
        log.info("Token blacklisted");
    }
}
