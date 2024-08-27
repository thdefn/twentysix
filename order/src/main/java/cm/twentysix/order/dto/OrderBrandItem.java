package cm.twentysix.order.dto;

import cm.twentysix.order.domain.model.OrderProduct;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record OrderBrandItem(
        Long brandId,
        Integer deliveryFee,
        List<BrandProductItem> products
) {
    public static OrderBrandItem of(Long brandId, Integer deliveryFee) {
        return OrderBrandItem.builder()
                .deliveryFee(deliveryFee)
                .products(new ArrayList<>())
                .brandId(brandId).build();
    }

    public void addProduct(String productId, OrderProduct product) {
        products.add(BrandProductItem.from(productId, product));
    }
}
