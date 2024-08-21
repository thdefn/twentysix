package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.StockCheckFailedEvent;
import cm.twentysix.product.dto.ProductStockResponse;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import cm.twentysix.product.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductStockService {
    private final ProductRepository productRepository;
    private final MessageSender messageSender;


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
        return true;
    }

    @Transactional
    public void restoreProductStock(Map<String, Integer> productIdQuantity) {
        List<Product> products = productRepository.findByIdInAndIsDeletedFalse(productIdQuantity.keySet());

        for (Product p : products) {
            int quantityToAdded = productIdQuantity.get(p.getId());
            p.addQuantity(quantityToAdded);
        }
        productRepository.saveAll(products);
    }


    public ProductStockResponse retrieveProductStock(String productId) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));
        return ProductStockResponse.from(product);
    }
}
