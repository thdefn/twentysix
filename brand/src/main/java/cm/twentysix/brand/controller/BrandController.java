package cm.twentysix.brand.controller;

import cm.twentysix.brand.controller.dto.CreateBrandForm;
import cm.twentysix.brand.controller.dto.UpdateBrandForm;
import cm.twentysix.brand.security.UserPrincipal;
import cm.twentysix.brand.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        brandService.createBrand(userPrincipal.getUserId(), image, form);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<Void> updateBrand(@PathVariable Long brandId,
                                            @Valid @RequestPart UpdateBrandForm form,
                                            @Valid @RequestPart(required = false) MultipartFile image,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        brandService.updateBrand(brandId, image, form, userPrincipal.getUserId());
        return ResponseEntity.ok().build();
    }
}
