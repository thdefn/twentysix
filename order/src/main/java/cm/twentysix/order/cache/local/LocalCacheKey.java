package cm.twentysix.order.cache.local;

import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public enum LocalCacheKey {
    RESERVED_PRODUCT_STOCK(Duration.ofMinutes(10L));

    public final Duration duration;

}
