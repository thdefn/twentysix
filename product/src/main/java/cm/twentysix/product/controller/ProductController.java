package cm.twentysix.product.controller;

import cm.twentysix.product.dto.CreateProductForm;
import cm.twentysix.product.dto.ProductItem;
import cm.twentysix.product.dto.ProductResponse;
import cm.twentysix.product.dto.UpdateProductForm;
import cm.twentysix.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> createProduct(@Valid @RequestPart CreateProductForm form,
                                              @RequestPart MultipartFile thumbnail,
                                              @RequestPart MultipartFile descriptionImage,
                                              @RequestHeader(value = "X-USER-ID") Long userId) {
        productService.createProduct(thumbnail, descriptionImage, form, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable String productId,
                                              @Valid @RequestPart UpdateProductForm form,
                                              @RequestPart MultipartFile thumbnail,
                                              @RequestPart MultipartFile descriptionImage,
                                              @RequestHeader(value = "X-USER-ID") Long userId) {
        productService.updateProduct(productId, thumbnail, descriptionImage, form, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId,
                                              @RequestHeader(value = "X-USER-ID") Long userId) {
        productService.deleteProduct(productId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductItem>> retrieveProducts(@RequestParam(required = false, defaultValue = "0") int page,
                                                              @RequestParam(required = false, defaultValue = "26") int size,
                                                              @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        return ResponseEntity.ok(productService.retrieveProducts(page, size, Optional.ofNullable(userId)));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> retrieveProducts(@PathVariable String productId,
                                                            @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        return ResponseEntity.ok(productService.retrieveProduct(productId, Optional.ofNullable(userId)));
    }

}
