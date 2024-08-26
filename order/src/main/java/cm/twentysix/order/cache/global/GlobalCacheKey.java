package cm.twentysix.order.cache.global;

import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public enum GlobalCacheKey {
    PRODUCT_ITEM_RESPONSE(Duration.ofMinutes(10L)),
    RESERVED_PRODUCT_STOCK(Duration.ofMinutes(10L))
    ;

    public final Duration duration;

}
