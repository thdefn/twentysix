package cm.twentysix.order.config;

import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.order.client.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {
    @Bean
    public RedisTemplate<String, ProductItemResponse> productItemResponseRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ProductItemResponse> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new ProtobufRedisSerializer<>(ProductItemResponse.parser()));
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisClient<ProductItemResponse> productItemResponseRedisClient(RedisTemplate<String, ProductItemResponse> redisTemplate) {
        return new RedisClient<>(redisTemplate);
    }

    @Bean
    public RedisClient<Object> objectRedisClient(RedisTemplate<String, Object> redisTemplate) {
        return new RedisClient<>(redisTemplate);
    }
}
