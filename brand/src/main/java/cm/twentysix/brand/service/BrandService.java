package cm.twentysix.brand.service;

import cm.twentysix.brand.client.FileStorageClient;
import cm.twentysix.brand.constant.FileDomain;
import cm.twentysix.brand.domain.model.Brand;
import cm.twentysix.brand.domain.repository.BrandRepository;
import cm.twentysix.brand.dto.CreateBrandForm;
import cm.twentysix.brand.dto.UpdateBrandForm;
import cm.twentysix.brand.exception.BrandException;
import cm.twentysix.brand.exception.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final FileStorageClient fileStorageClient;

    public void createBrand(Long userId, Optional<MultipartFile> maybeFile, CreateBrandForm form) {
        if (brandRepository.countByUserId(userId) > 4)
            throw new BrandException(Error.BRAND_LIMIT_OVER);

        String filePath =  maybeFile.stream().findFirst()
                .map(file -> fileStorageClient.upload(file, FileDomain.BRAND)).orElse(null);
        brandRepository.save(Brand.from(form, userId, filePath));
    }

    @Transactional
    public void updateBrand(Long brandId, Optional<MultipartFile> maybeFile, UpdateBrandForm form, Long userId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new BrandException(Error.BRAND_NOT_FOUND));
        if (!brand.getUserId().equals(userId))
            throw new BrandException(Error.NOT_BRAND_OWNER);

        String filePath =  maybeFile.stream().findFirst()
                .map(file -> fileStorageClient.upload(file, FileDomain.BRAND)).orElse(null);
        brand.update(form, filePath);
    }


}
