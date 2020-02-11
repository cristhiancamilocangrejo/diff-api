package com.waes.diff.api.config;

import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * As redis needs to be deployed in an external server, this class provides
 * a embedded redis in order to do not have dependencies
 */
@Configuration
public class EmbeddedRedisConfiguration
{
	private RedisServer redisServer;

	public EmbeddedRedisConfiguration(RedisProperties redisProperties) {
		this.redisServer = new RedisServer(redisProperties.getRedisPort());
	}

	@PostConstruct
	public void postConstruct() {
		redisServer.start();
	}

	@PreDestroy
	public void preDestroy(){
		redisServer.stop();
	}
}
