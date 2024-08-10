package cm.twentysix.brand.service;

import cm.twentysix.brand.controller.dto.CreateBrandForm;
import cm.twentysix.brand.controller.dto.UpdateBrandForm;
import cm.twentysix.brand.domain.model.Brand;
import cm.twentysix.brand.domain.repository.BrandRepository;
import cm.twentysix.brand.exception.BrandException;
import cm.twentysix.brand.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    public void createBrand(Long userId, MultipartFile file, CreateBrandForm form) {
        if (brandRepository.countByUserId(userId) > 4)
            throw new BrandException(Error.BRAND_LIMIT_OVER);
        // TODO : 썸네일 저장 로직
        brandRepository.save(Brand.from(form, userId));
    }

    @Transactional
    public void updateBrand(Long brandId, MultipartFile file, UpdateBrandForm form, Long userId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new BrandException(Error.BRAND_NOT_FOUND));
        if (!brand.getUserId().equals(userId))
            throw new BrandException(Error.NOT_BRAND_OWNER);
        // TODO : 썸네일 저장 로직
        brand.update(form);
    }


}
