package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.ProductStockResponse;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStockService {
    private final ProductRepository productRepository;

    @Transactional
    public boolean checkAndDecreaseStock(String productId, int requestedQuantity) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));
        if (requestedQuantity > product.getQuantity())
            return false;
        product.minusQuantity(requestedQuantity);
        productRepository.save(product);
        return true;
    }

    @Transactional
    public void restoreProductStock(String productId, int orderedQuantity) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        product.addQuantity(orderedQuantity);
        productRepository.save(product);
    }


    public ProductStockResponse retrieveProductStock(String productId) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));
        return ProductStockResponse.from(product);
    }
}
