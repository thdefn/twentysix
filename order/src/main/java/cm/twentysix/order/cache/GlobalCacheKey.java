package cm.twentysix.order.cache;

import lombok.AllArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
public enum GlobalCacheKey {
    PRODUCT(Duration.ofMinutes(10L))
    ;

    public final Duration duration;

}
