package cm.twentysix.product.service;

import cm.twentysix.product.constant.LockDomain;
import cm.twentysix.product.dto.ProductStockResponse;
import cm.twentysix.product.dto.ProductStockUpdateEvent;
import cm.twentysix.product.dto.StockCheckFailedEvent;
import cm.twentysix.product.messaging.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductStockFacade {
    private final LockService lockService;
    private final ProductStockService productStockService;
    private final MessageSender messageSender;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public boolean handleOrder(Map<String, Integer> productIdQuantity, String orderId) {
        boolean isSuccess = checkAndDecreaseStock(productIdQuantity);
        if (!isSuccess)
            messageSender.sendProductOrderFailedEvent(StockCheckFailedEvent.of(orderId));
        applicationEventPublisher.publishEvent(ProductStockUpdateEvent.of(productIdQuantity.keySet()));
        return isSuccess;
    }

    private boolean checkAndDecreaseStock(Map<String, Integer> productIdQuantity) {
        for (String productId : productIdQuantity.keySet()) {
            try {
                lockService.lock(LockDomain.PRODUCT_STOCK, productId);
                if (!productStockService.checkAndDecreaseStock(productId, productIdQuantity.get(productId)))
                    return false;
            } catch (Exception e) {
                return false;
            } finally {
                lockService.unlock(LockDomain.PRODUCT_STOCK, productId);
            }
        }
        return true;
    }


    @Transactional
    public void rollbackOrder(Map<String, Integer> productIdQuantity) {
        for (String productId : productIdQuantity.keySet()) {
            try {
                lockService.lock(LockDomain.PRODUCT_STOCK, productId);
                productStockService.restoreProductStock(productId, productIdQuantity.get(productId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                lockService.unlock(LockDomain.PRODUCT_STOCK, productId);
            }
        }
        applicationEventPublisher.publishEvent(ProductStockUpdateEvent.of(productIdQuantity.keySet()));
    }

    public ProductStockResponse retrieveProductStock(String productId) {
        return productStockService.retrieveProductStock(productId);
    }
}
