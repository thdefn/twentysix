package cm.twentysix.order.config;

import cm.twentysix.order.cache.local.LocalCacheKey;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@EnableCaching
@Configuration
public class LocalCacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(defaultCacheConfiguration());
        cacheManager.setCacheNames(List.of(LocalCacheKey.BRAND_INFO.name()));
        return cacheManager;
    }

    private Caffeine<Object, Object> defaultCacheConfiguration() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10L))
                .maximumSize(50);
    }

}
