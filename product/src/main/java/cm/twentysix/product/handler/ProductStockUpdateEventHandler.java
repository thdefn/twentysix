package cm.twentysix.product.handler;

import cm.twentysix.product.cache.global.ProductItemResponseGlobalCacheRepository;
import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.ProductStockUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductStockUpdateEventHandler {
    private final ProductRepository productRepository;
    private final ProductItemResponseGlobalCacheRepository productItemResponseGlobalCacheRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductStockUpdateEvent(ProductStockUpdateEvent productStockUpdateEvent) {
        List<Product> products = productRepository.findByIdInAndIsDeletedFalse(productStockUpdateEvent.productIds());
        productItemResponseGlobalCacheRepository.putAll(products);
    }
}
