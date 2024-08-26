package cm.twentysix.product.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LockDomain {
    PRODUCT_STOCK(180L, 300L);

    public final long waitSecond;
    public final long leaseSecond;
}
