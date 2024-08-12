package cm.twentysix.brand.controller;

import cm.twentysix.brand.dto.CreateBrandForm;
import cm.twentysix.brand.dto.UpdateBrandForm;
import cm.twentysix.brand.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
public class BrandController {
    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<Void> createBrand(@Valid @RequestPart CreateBrandForm form,
                                            @Valid @RequestPart(required = false) MultipartFile image,
                                            @RequestHeader(value = "X-USER-ID") Long userId) {
        brandService.createBrand(userId, image, form);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<Void> updateBrand(@PathVariable Long brandId,
                                            @Valid @RequestPart UpdateBrandForm form,
                                            @Valid @RequestPart(required = false) MultipartFile image,
                                            @RequestHeader(value = "X-USER-ID") Long userId) {
        brandService.updateBrand(brandId, image, form, userId);
        return ResponseEntity.ok().build();
    }
}
