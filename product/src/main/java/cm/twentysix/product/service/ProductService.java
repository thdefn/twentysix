package cm.twentysix.product.service;

import cm.twentysix.product.client.FileStorageClient;
import cm.twentysix.product.constant.FileDomain;
import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.BrandResponse;
import cm.twentysix.product.dto.CreateProductForm;
import cm.twentysix.product.dto.UpdateProductForm;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import cm.twentysix.product.service.dto.CategoryInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final FileStorageClient fileStorageClient;

    public void createProduct(MultipartFile thumbnail, MultipartFile descriptionImage, CreateProductForm form, Long userId) {
        List<CategoryInfoDto> categoryInfoDtos = categoryService.retrieveBelongingCategories(form.categoryId());
        // TODO: request brand info
        // TODO: check user's authority about brand

        String thumbnailPath = fileStorageClient.upload(thumbnail, FileDomain.PRODUCT);
        String descriptionPath = fileStorageClient.upload(descriptionImage, FileDomain.PRODUCT);
        productRepository.save(Product.of(form, new BrandResponse(null, null, null), userId, categoryInfoDtos, thumbnailPath, descriptionPath));
    }

    public void updateProduct(String productId, MultipartFile thumbnail, MultipartFile descriptionImage, UpdateProductForm form, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUD));
        if (!product.getUserId().equals(userId))
            throw new ProductException(Error.NOT_PRODUCT_ADMIN);
        if (product.isDeleted())
            throw new ProductException(Error.ALREADY_DELETED_PRODUCT);
        List<CategoryInfoDto> categoryInfoDtos = categoryService.retrieveBelongingCategories(form.categoryId());
        // TODO: request brand info
        // TODO: check user's authority about brand

        String thumbnailPath = fileStorageClient.upload(thumbnail, FileDomain.PRODUCT);
        String descriptionPath = fileStorageClient.upload(descriptionImage, FileDomain.PRODUCT);
        product.update(form, new BrandResponse(null, null, null), userId, categoryInfoDtos, thumbnailPath, descriptionPath);
        productRepository.save(product);
    }


    public void deleteProduct(String productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUD));
        if (!product.getUserId().equals(userId))
            throw new ProductException(Error.NOT_PRODUCT_ADMIN);
        if (product.isDeleted())
            throw new ProductException(Error.ALREADY_DELETED_PRODUCT);

        fileStorageClient.deleteAll(product.getFilePaths());
        product.delete();
        productRepository.save(product);
    }
}
