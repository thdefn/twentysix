package cm.twentysix.product.service;

import cm.twentysix.product.dto.ProductStockResponse;
import cm.twentysix.product.dto.ProductStockUpdateEvent;
import cm.twentysix.product.dto.StockCheckFailedEvent;
import cm.twentysix.product.messaging.MessageSender;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class NonLockProductStockFacade {
    private final ProductStockService productStockService;
    private final MessageSender messageSender;
    private final ApplicationEventPublisher applicationEventPublisher;

    public NonLockProductStockFacade(ProductStockService productStockService, MessageSender messageSender, ApplicationEventPublisher applicationEventPublisher) {
        this.productStockService = productStockService;
        this.messageSender = messageSender;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void handleOrder(Map<String, Integer> productIdQuantity, String orderId) {
        if (!checkAndDecreaseStock(productIdQuantity))
            messageSender.sendProductOrderFailedEvent(StockCheckFailedEvent.of(orderId));
        applicationEventPublisher.publishEvent(ProductStockUpdateEvent.of(productIdQuantity.keySet()));
    }

    private boolean checkAndDecreaseStock(Map<String, Integer> productIdQuantity) {
        for (String productId : productIdQuantity.keySet()) {
            try {
                if (!productStockService.checkAndDecreaseStock(productId, productIdQuantity.get(productId)))
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


    @Transactional
    public void rollbackOrder(Map<String, Integer> productIdQuantity) {
        for (String productId : productIdQuantity.keySet()) {
            try {
                productStockService.restoreProductStock(productId, productIdQuantity.get(productId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        applicationEventPublisher.publishEvent(ProductStockUpdateEvent.of(productIdQuantity.keySet()));
    }

    public ProductStockResponse retrieveProductStock(String productId) {
        return productStockService.retrieveProductStock(productId);
    }
}
