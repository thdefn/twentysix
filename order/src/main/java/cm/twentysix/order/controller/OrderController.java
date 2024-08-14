package cm.twentysix.order.controller;

import cm.twentysix.order.dto.CreateOrderForm;
import cm.twentysix.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public void createOrder(@Valid @RequestBody CreateOrderForm form,
                            @RequestHeader(value = "X-USER-ID") Long userId) {
        orderService.receiveOrder(form, userId);
    }
}
