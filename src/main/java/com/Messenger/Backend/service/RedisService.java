package com.Messenger.Backend.service;

import org.springframework.data.redis.core.RedisTemplate;
import javax.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Transactional(value = Transactional.TxType.NOT_SUPPORTED)
    public void deleteKey(String key) {
        final Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            redisTemplate.delete(key);
        }
    }

    public Optional<String> getValueFromCache(String key) {
        final ValueOperations<String, String> operations = redisTemplate.opsForValue();
        final Boolean hasKey = redisTemplate.hasKey(key);
        String returnVal = null;
        if (hasKey) {
            returnVal = operations.get(key);
        }
        return Optional.ofNullable(returnVal);
    }

    public void setKeyValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
