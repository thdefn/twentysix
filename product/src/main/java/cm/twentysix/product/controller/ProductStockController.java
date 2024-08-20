package cm.twentysix.product.controller;

import cm.twentysix.product.dto.ProductStockResponse;
import cm.twentysix.product.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductStockController {
    private final ProductStockService productStockService;

    @GetMapping("/{productId}/stock")
    public ResponseEntity<ProductStockResponse> retrieveProductStock(@PathVariable String productId) {
        return ResponseEntity.ok(productStockService.retrieveProductStock(productId));
    }
}
