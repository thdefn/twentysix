package cm.twentysix.payment.controller;

import cm.twentysix.payment.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cm.twentysix.OrderProto.OrderInfoResponse;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @GetMapping("/{orderId}")
    public String checkout(@PathVariable String orderId, Model model) {
        OrderInfoResponse response = checkoutService.getOrderInfo(orderId);
        model.addAttribute("amount", response.getTotalAmount());
        model.addAttribute("orderId", response.getOrderId());
        model.addAttribute("userId", response.getUserId());
        model.addAttribute("orderName", response.getOrderName());
        return "checkout";
    }

    @GetMapping("/fail")
    public String fail(@RequestParam String code, @RequestParam String message, @RequestParam String orderId) {
        return "fail";
    }

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Integer amount) {
        return "success";
    }
}
