package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.ProductOrderFailedEvent;
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
        if(checkAndUpdateProductStock(products, productIdQuantity))
            productRepository.saveAll(products);
        else messageSender.sendProductOrderFailedEvent(ProductOrderFailedEvent.of(orderId));
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
}
