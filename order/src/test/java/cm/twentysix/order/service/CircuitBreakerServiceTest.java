package cm.twentysix.order.service;

import cm.twentysix.order.constant.CircuitBreakerDomain;
import cm.twentysix.order.exception.CircuitBreakerException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerServiceTest {
    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @Mock
    private CircuitBreaker circuitBreaker;
    @InjectMocks
    private CircuitBreakerService circuitBreakerService;

    @Test
    void getOrCreateCircuitBreaker_success() {
        //given
        given(circuitBreakerRegistry.circuitBreaker(anyString()))
                .willReturn(circuitBreaker);
        //when
        CircuitBreaker createdCircuitBreaker = circuitBreakerService
                .getOrCreateCircuitBreaker(CircuitBreakerDomain.ORDER, "abcd");
        //then
        assertEquals(circuitBreaker, createdCircuitBreaker);
        verify(circuitBreakerRegistry, times(1)).circuitBreaker(anyString());
    }

    @Test
    void testIsCircuitBreakerOpen_False() {
        circuitBreakerService.getOrCreateCircuitBreaker(CircuitBreakerDomain.ORDER, "abcd");

        boolean result = circuitBreakerService.isCircuitBreakerOpen(CircuitBreakerDomain.ORDER, "abcd");

        assertFalse(result);
    }

    @Test
    void testIsCircuitBreakerOpen_True() {
        given(circuitBreakerRegistry.circuitBreaker(anyString()))
                .willReturn(circuitBreaker);
        given(circuitBreaker.getState()).willReturn(CircuitBreaker.State.OPEN);
        CircuitBreaker createdCircuitBreaker = circuitBreakerService
                .getOrCreateCircuitBreaker(CircuitBreakerDomain.ORDER, "abcd");
        assertEquals(circuitBreaker, createdCircuitBreaker);

        boolean result = circuitBreakerService.isCircuitBreakerOpen(CircuitBreakerDomain.ORDER, "abcd");

        assertTrue(result);
    }


    @Test
    void validateServiceAvailability_CircuitBreakerException() {
        given(circuitBreakerRegistry.circuitBreaker(anyString()))
                .willReturn(circuitBreaker);
        given(circuitBreaker.getState()).willReturn(CircuitBreaker.State.OPEN);
        CircuitBreaker createdCircuitBreaker = circuitBreakerService
                .getOrCreateCircuitBreaker(CircuitBreakerDomain.ORDER, "abcd");
        assertEquals(circuitBreaker, createdCircuitBreaker);

        assertThrows(CircuitBreakerException.class, () ->  circuitBreakerService.validateServiceAvailability(CircuitBreakerDomain.ORDER, List.of("abcd", "1234")));

    }
}