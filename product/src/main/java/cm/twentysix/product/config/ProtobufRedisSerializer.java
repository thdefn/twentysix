package cm.twentysix.product.config;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ProtobufRedisSerializer<T extends Message> implements RedisSerializer<T> {
    private final Parser<T> parser;

    public ProtobufRedisSerializer(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public byte[] serialize(T value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        return value.toByteArray();
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return parser.parseFrom(bytes);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize Protobuf message", e);
        }
    }
}
