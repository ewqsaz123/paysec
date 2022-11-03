package com.test.kakaopaysecTest.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfig {

	
		@Bean
		public CacheManager cacheManager() {
			 SimpleCacheManager cacheManager = new SimpleCacheManager();
	         cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("top"), new ConcurrentMapCache("hit"), new ConcurrentMapCache("increase"), new ConcurrentMapCache("decrease"), new ConcurrentMapCache("trade")));
	         return cacheManager;
		}
}
