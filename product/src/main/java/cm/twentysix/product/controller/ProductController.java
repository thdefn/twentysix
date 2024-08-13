package cm.twentysix.product.controller;

import cm.twentysix.product.dto.CreateProductForm;
import cm.twentysix.product.dto.UpdateProductForm;
import cm.twentysix.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

}
