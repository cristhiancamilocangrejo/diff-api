package com.waes.diff.api.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a configuration to connect to redis in order to keep
 * data in memory
 */
@Configuration
public class CachingConfig
{
	private final static String REDIS_CONF = "redis://%s:%s";
	private final static String DIFF_CONFIG = "diffConfig";

	private final RedisProperties redisProperties;
	private final long ttl;
	private final long maxTimeIdle;

	public CachingConfig(RedisProperties redisProperties, @Value("${spring.redis.ttl}") long ttl,
		@Value("${spring.redis.maxTimeIdle}") long maxTimeIdle) {
		this.redisProperties = redisProperties;
		this.ttl = ttl;
		this.maxTimeIdle = maxTimeIdle;
	}

	@Bean(destroyMethod="shutdown")
	RedissonClient redisson() {
		Config config = new Config();
		config.useSingleServer().setAddress(String.format(REDIS_CONF, redisProperties.getRedisHost(),
			redisProperties.getRedisPort()));
		return Redisson.create(config);
	}
	@Bean
	CacheManager cacheManager(RedissonClient redissonClient) {
		Map<String, CacheConfig> config = new HashMap<>();
		config.put(DIFF_CONFIG, new CacheConfig(ttl, maxTimeIdle));
		return new RedissonSpringCacheManager(redissonClient, config);
	}
}
