package cm.twentysix.product.config;

import cm.twentysix.ProductProto.ProductItemResponse;
import cm.twentysix.product.client.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
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
    public RedisClient<ProductItemResponse> productItemResponseRedisClient(RedisTemplate<String, ProductItemResponse> redisTemplate) {
        return new RedisClient<>(redisTemplate);
    }

}
