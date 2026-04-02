package org.iimsa.orderservice.infrastructure.provider;

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
        return productClient.getProduct(productId);
    }
}
