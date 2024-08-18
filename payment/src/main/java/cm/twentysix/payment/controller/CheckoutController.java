package cm.twentysix.payment.controller;

import cm.twentysix.payment.dto.RequiredPaymentResponse;
import cm.twentysix.payment.exception.Error;
import cm.twentysix.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {
    private final PaymentService paymentService;

    @GetMapping("/{orderId}")
    public String checkout(@PathVariable String orderId, Model model) {
        RequiredPaymentResponse response = paymentService.getRequiredPayment(orderId);
        model.addAttribute("amount", response.amount());
        model.addAttribute("orderId", orderId);
        model.addAttribute("userId", response.userId());
        model.addAttribute("orderName", response.orderName());
        return "checkout";
    }

    @GetMapping("/fail")
    public String fail(@RequestParam String code, @RequestParam String message, @RequestParam String orderId) {
        return "fail";
    }

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Integer amount) {
        boolean shouldRedirect = !paymentService.success(paymentKey, orderId, amount);
        if (shouldRedirect)
            return "redirect:/fail?code=" + Error.STOCK_SHORTAGE +
                    "?message=" + Error.STOCK_SHORTAGE.message + "?orderId=" + orderId;
        return "success";
    }
}
