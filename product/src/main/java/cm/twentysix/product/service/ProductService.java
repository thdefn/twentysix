package cm.twentysix.product.service;

import cm.twentysix.BrandProto;
import cm.twentysix.product.client.BrandGrpcClient;
import cm.twentysix.product.client.FileStorageClient;
import cm.twentysix.product.constant.FileDomain;
import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.CreateProductForm;
import cm.twentysix.product.dto.ProductItem;
import cm.twentysix.product.dto.ProductResponse;
import cm.twentysix.product.dto.UpdateProductForm;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import cm.twentysix.product.service.dto.CategoryInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final FileStorageClient fileStorageClient;
    private final BrandGrpcClient brandGrpcClient;

    public void createProduct(MultipartFile thumbnail, MultipartFile descriptionImage, CreateProductForm form, Long userId) {
        List<CategoryInfoDto> categoryInfoDtos = categoryService.retrieveBelongingCategories(form.categoryId());
        BrandProto.BrandDetailResponse response = brandGrpcClient.getBrandDetail(form.brandId());
        if (!userId.equals(response.getUserId()))
            throw new ProductException(Error.NOT_PRODUCT_ADMIN);

        String thumbnailPath = fileStorageClient.upload(thumbnail, FileDomain.PRODUCT);
        String descriptionPath = fileStorageClient.upload(descriptionImage, FileDomain.PRODUCT);

        productRepository.save(Product.of(form, response, userId, categoryInfoDtos, thumbnailPath, descriptionPath));
    }

    public void updateProduct(String productId, MultipartFile thumbnail, MultipartFile descriptionImage, UpdateProductForm form, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));
        if (!product.getUserId().equals(userId))
            throw new ProductException(Error.NOT_PRODUCT_ADMIN);
        if (product.isDeleted())
            throw new ProductException(Error.ALREADY_DELETED_PRODUCT);

        List<CategoryInfoDto> categoryInfoDtos = categoryService.retrieveBelongingCategories(form.categoryId());
        BrandProto.BrandDetailResponse response = brandGrpcClient.getBrandDetail(product.getProductBrand().getId());

        String thumbnailPath = fileStorageClient.upload(thumbnail, FileDomain.PRODUCT);
        String descriptionPath = fileStorageClient.upload(descriptionImage, FileDomain.PRODUCT);

        product.update(form, response, userId, categoryInfoDtos, thumbnailPath, descriptionPath);
        productRepository.save(product);
    }


    public void deleteProduct(String productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));
        if (!product.getUserId().equals(userId))
            throw new ProductException(Error.NOT_PRODUCT_ADMIN);
        if (product.isDeleted())
            throw new ProductException(Error.ALREADY_DELETED_PRODUCT);

        fileStorageClient.deleteAll(product.getFilePaths());
        product.delete();
        productRepository.save(product);
    }

    public List<ProductItem> retrieveProducts(int page, int size, Optional<Long> optionalUserId) {
        return productRepository.findByIsDeletedFalseOrderByIdDesc(PageRequest.of(page, size))
                .stream().map(product -> ProductItem.from(product, optionalUserId))
                .collect(Collectors.toList());
    }

    public ProductResponse retrieveProduct(String productId, Optional<Long> optionalUserId) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        BrandProto.BrandDetailResponse response = brandGrpcClient.getBrandDetail(product.getProductBrand().getId());
        return ProductResponse.of(product, response, optionalUserId);
    }


}
