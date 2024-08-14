package cm.twentysix.order.dto;

public record ProductOrderItem(
        String name,
        String thumbnail,
        Integer quantity,
        Integer amount,
        Long brandId,
        String brandName,
        Integer deliveryFee

) {

}
