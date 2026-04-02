package org.iimsa.orderservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.iimsa.orderservice.domain.service.ProductProvider;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기술 제약
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "productName", length = 100)
    private String productName;

    protected Product(UUID productId, Integer quantity, ProductProvider productProvider) {
        if(productId == null) {
            throw new IllegalArgumentException("productId cannot be null");
        }
        if(quantity == null) {
            throw new IllegalArgumentException("quantity cannot be null");
        }
        if(productProvider == null) {
            throw new IllegalArgumentException("productProvider cannot be null");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productProvider.getProductName(productId);
    }

    public static Product from(UUID productId, Integer quantity, ProductProvider productProvider) {
        return new Product(productId, quantity, productProvider);
    }
}
