package cm.twentysix.order.controller;

import cm.twentysix.order.dto.CreateOrderForm;
import cm.twentysix.order.dto.ReceiveOrderResponse;
import cm.twentysix.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ReceiveOrderResponse> receiveOrder(@Valid @RequestBody CreateOrderForm form,
                                                             @RequestHeader(value = "X-USER-ID") Long userId) {
        return ResponseEntity.ok(orderService.receiveOrder(form, userId));
    }
}
