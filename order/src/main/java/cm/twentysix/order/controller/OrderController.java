package cm.twentysix.order.controller;

import cm.twentysix.order.dto.CreateOrderForm;
import cm.twentysix.order.dto.OrderItem;
import cm.twentysix.order.dto.ReceiveOrderResponse;
import cm.twentysix.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ReceiveOrderResponse> receiveOrder(@Valid @RequestBody CreateOrderForm form,
                                                             @RequestAttribute("requestedAt") LocalDateTime requestedAt,
                                                             @RequestHeader(value = "X-USER-ID") Long userId) {
        return ResponseEntity.ok(orderService.receiveOrder(form, userId, requestedAt));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId,
                                            @RequestHeader(value = "X-USER-ID") Long userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/return")
    public ResponseEntity<Void> returnOrder(@PathVariable Long orderId,
                                            @RequestHeader(value = "X-USER-ID") Long userId) {
        orderService.returnOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Slice<OrderItem>> retrieveMyOrder(@RequestParam(required = false, defaultValue = "0") int page,
                                                            @RequestParam(required = false, defaultValue = "20") int size,
                                                            @RequestHeader(value = "X-USER-ID") Long userId) {
        return ResponseEntity.ok(orderService.retrieveMyOrder(page, size, userId));
    }
}
