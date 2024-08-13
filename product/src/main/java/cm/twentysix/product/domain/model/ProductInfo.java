package cm.twentysix.product.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductInfo {
    private String manufacturer;
    private String countryOfManufacture;
    private String contact;

    @Builder
    public ProductInfo(String manufacturer, String countryOfManufacture, String contact) {
        this.manufacturer = manufacturer;
        this.countryOfManufacture = countryOfManufacture;
        this.contact = contact;
    }

    public static ProductInfo from(String manufacturer, String countryOfManufacture, String contact) {
        return ProductInfo.builder()
                .contact(contact)
                .manufacturer(manufacturer)
                .countryOfManufacture(countryOfManufacture)
                .build();
    }
}
