package com.example.botpoliclinica.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URISyntaxException;
import java.net.URL;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public org.springframework.cache.CacheManager cacheManager() throws URISyntaxException {
        CachingProvider provider = Caching.getCachingProvider();
        URL myUrl = getClass().getResource("/ehcache.xml");
        CacheManager cacheManager = provider.getCacheManager(myUrl.toURI(), getClass().getClassLoader());
        return new JCacheCacheManager(cacheManager);
    }
}
