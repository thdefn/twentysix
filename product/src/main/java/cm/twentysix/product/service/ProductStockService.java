package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.cache.ProductItemResponseGlobalCacheRepository;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.ProductStockResponse;
import cm.twentysix.product.dto.StockCheckFailedEvent;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import cm.twentysix.product.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProductStockService {
    private final ProductRepository productRepository;
    private final MessageSender messageSender;
    private final ProductItemResponseGlobalCacheRepository productItemResponseGlobalCacheRepository;


    @Transactional
    public void checkProductStock(Map<String, Integer> productIdQuantity, String orderId) {
        List<Product> products = productRepository.findByIdInAndIsDeletedFalse(productIdQuantity.keySet());
        if (checkAndUpdateProductStock(products, productIdQuantity))
            productRepository.saveAll(products);
        else
            messageSender.sendProductOrderFailedEvent(StockCheckFailedEvent.of(orderId));
    }


    private boolean checkAndUpdateProductStock(List<Product> products, Map<String, Integer> orderedProductQuantity) {
        for (Product p : products) {
            int requiredQuantity = orderedProductQuantity.get(p.getId());
            if (requiredQuantity > p.getQuantity())
                return false;
            p.minusQuantity(requiredQuantity);
        }
        CompletableFuture.runAsync(() -> productItemResponseGlobalCacheRepository.putAll(products));
        return true;
    }

    @Transactional
    public void restoreProductStock(Map<String, Integer> productIdQuantity) {
        List<Product> products = productRepository.findByIdInAndIsDeletedFalse(productIdQuantity.keySet());

        for (Product p : products) {
            int quantityToAdded = productIdQuantity.get(p.getId());
            p.addQuantity(quantityToAdded);
        }
        CompletableFuture.runAsync(() -> productItemResponseGlobalCacheRepository.putAll(products));
        productRepository.saveAll(products);
    }


    public ProductStockResponse retrieveProductStock(String productId) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));
        return ProductStockResponse.from(product);
    }
}
