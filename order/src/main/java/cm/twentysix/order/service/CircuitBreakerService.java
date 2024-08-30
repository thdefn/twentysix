package cm.twentysix.order.service;

import cm.twentysix.order.constant.CircuitBreakerDomain;
import cm.twentysix.order.exception.CircuitBreakerException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class CircuitBreakerService {
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final ConcurrentMap<String, CircuitBreaker> circuitBreakers;

    public CircuitBreakerService(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.circuitBreakers = new ConcurrentHashMap<>();
    }

    private String getCircuitBreakerKey(CircuitBreakerDomain domain, String key) {
        return domain.name() + key;
    }

    public CircuitBreaker getOrCreateCircuitBreaker(CircuitBreakerDomain domain, String key) {
        return circuitBreakers
                .computeIfAbsent(getCircuitBreakerKey(domain, key), circuitBreakerRegistry::circuitBreaker);
    }

    public boolean isCircuitBreakerOpen(CircuitBreakerDomain domain, String key) {
        String circuitBreakerKey = getCircuitBreakerKey(domain, key);
        if (!circuitBreakers.containsKey(circuitBreakerKey))
            return false;
        return CircuitBreaker.State.OPEN.equals(circuitBreakers.get(circuitBreakerKey).getState());
    }

    public void validateServiceAvailability(CircuitBreakerDomain domain, List<String> keys) {
        boolean isAnyCircuitBreakerOpen = keys.stream()
                .anyMatch(productId -> isCircuitBreakerOpen(domain, productId));

        if (isAnyCircuitBreakerOpen)
            throw new CircuitBreakerException("Service is temporarily unavailable. Please try again later.");
    }
}
