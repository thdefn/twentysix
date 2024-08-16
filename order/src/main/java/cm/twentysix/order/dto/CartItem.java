package cm.twentysix.order.dto;

import cm.twentysix.BrandProto.BrandInfo;
import cm.twentysix.ProductProto.ProductItemResponse;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record CartItem(
        Long brandId,
        String brandName,
        Integer freeDeliveryInfimum,
        Integer deliveryFee,
        List<CartProductItem> items

) {
    public static CartItem from(BrandInfo brandInfo) {
        return CartItem.builder()
                .brandId(brandInfo.getId())
                .brandName(brandInfo.getName())
                .deliveryFee(brandInfo.getDeliveryFee())
                .freeDeliveryInfimum(brandInfo.getFreeDeliveryInfimum())
                .items(new ArrayList<>())
                .build();
    }

    public void addCartProductItem(ProductItemResponse productItem, int quantity){
        items.add(CartProductItem.from(productItem, quantity));
    }
}
