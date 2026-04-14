package org.iimsa.orderservice.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable e) {
        return ProductId -> {
            log.error("[Product Service Fallback] ID: {} 조회 중 장애 발생. 사유: {}", ProductId, e.getMessage(), e);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Product Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.");
        };
    }
}
