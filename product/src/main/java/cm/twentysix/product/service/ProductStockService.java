package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.dto.OrderReplyEvent;
import cm.twentysix.product.dto.ProductOrderItem;
import cm.twentysix.product.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductStockService {
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSender messageSender;

    @Transactional
    public void checkProductStock(Map<String, Integer> orderedProductQuantity, String orderId) {
        List<Product> products = productRepository.findByIdInAndIsDeletedFalse(orderedProductQuantity.keySet());
        Map<String, ProductOrderItem> orderItems = getProductOrderItems(products, orderedProductQuantity);

        boolean isSuccess = orderItems.size() == orderedProductQuantity.size();
        if (isSuccess) {
            productRepository.saveAll(products);
        }
        applicationEventPublisher.publishEvent(OrderReplyEvent.of(orderId, isSuccess, orderItems));
    }


    private Map<String, ProductOrderItem> getProductOrderItems(List<Product> products, Map<String, Integer> orderedProductQuantity) {
        Map<String, ProductOrderItem> orderItems = new HashMap<>();
        for (Product p : products) {
            int requiredQuantity = orderedProductQuantity.get(p.getId());
            if (requiredQuantity <= p.getQuantity()) {
                orderItems.put(p.getId(), ProductOrderItem.from(p, requiredQuantity));
                p.minusQuantity(requiredQuantity);
            }
        }
        return orderItems;
    }
}
