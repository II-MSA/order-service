package org.iimsa.orderservice.domain.service;

import java.util.UUID;

public interface ProductProvider {
    String getProductName(UUID productId);
}
