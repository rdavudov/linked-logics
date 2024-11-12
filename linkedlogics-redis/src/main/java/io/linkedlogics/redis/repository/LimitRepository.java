package io.linkedlogics.redis.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

@Slf4j
public class LimitRepository extends JedisRepository {
	private static final String LIMIT = "limit:";

	public LimitRepository(StringRedisTemplate redisTemplate) {
		super(redisTemplate);
	}
	

	public boolean create(String key, OffsetDateTime expiresAt, Long limit, Long increment) {
		redisTemplate.opsForValue().set(getKey(key), String.valueOf(increment));
		return true;
	}

	public boolean update(String key, OffsetDateTime expiresAt, Long limit, Long increment) {
		String currentValue = redisTemplate.opsForValue().get(getKey(key));
		if (currentValue != null && !currentValue.isEmpty()) {
			redisTemplate.opsForValue().set(getKey(key), ""+increment);
		} else {
			redisTemplate.opsForValue().set(getKey(key), ""+increment);
		}
		return true;
	}
	
	public Optional<Long> get(String key) {
		return null;
	}

	public void delete(String key) {
		
	}
	
	private String getKey(String key) {
		return LIMIT + key;
	}
	
	private static boolean performCAS(Jedis jedis, String key, int oldValue, int newValue) {
        while (true) {
            // Begin a transaction
            Transaction tx = jedis.multi();

            // Get the current value
            tx.get(key);

            // Check if the current value matches the expected old value
            String currentValue = (String) tx.exec().get(0);
            int currentIntValue = Integer.parseInt(currentValue);
            if (currentIntValue != oldValue) {
                return false; // CAS operation failed
            }

            // Update the value to the new value
            tx.set(key, String.valueOf(newValue));

            // Execute the transaction atomically
            if (tx.exec() != null) {
                return true; // CAS operation successful
            }
        }
    }
}
