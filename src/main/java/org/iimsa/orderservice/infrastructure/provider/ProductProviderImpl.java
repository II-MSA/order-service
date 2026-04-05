package org.iimsa.orderservice.infrastructure.provider;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.domain.service.ProductProvider;
import org.iimsa.orderservice.infrastructure.client.ProductClient;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductProviderImpl implements ProductProvider {

    private final ProductClient productClient;

    @Override
    public String getProductName(UUID productId) {
        Objects.requireNonNull(productId, "productId는 필수입니다.");
        String productName = productClient.getProduct(productId);
        if (productName == null || productName.isBlank()) {
            throw new IllegalStateException("유효한 상품명을 조회하지 못했습니다.");
        }
        return productName;
    }
}
