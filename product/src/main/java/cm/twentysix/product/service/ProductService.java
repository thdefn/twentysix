package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
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

    public void createProduct(MultipartFile thumbnail, MultipartFile descriptionImage, CreateProductForm form, Long userId) {
        List<CategoryInfoDto> categoryInfoDtos = categoryService.retrieveBelongingCategories(form.categoryId());
        // TODO: request brand info
        // TODO: check user's authority about brand

        // TODO: image processing
        productRepository.save(Product.of(form, null, userId, categoryInfoDtos));
    }

    public void updateProduct(String productId, MultipartFile thumbnail, MultipartFile descriptionImage, UpdateProductForm form, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUD));
        if (!product.getUserId().equals(userId))
            throw new ProductException(Error.NOT_PRODUCT_ADMIN);
        List<CategoryInfoDto> categoryInfoDtos = categoryService.retrieveBelongingCategories(form.categoryId());
        // TODO: request brand info
        // TODO: check user's authority about brand

        // TODO: image processing
        product.update(form, null, userId, categoryInfoDtos);
        productRepository.save(product);
    }


}
