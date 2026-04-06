package org.iimsa.orderservice.infrastructure.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service"
        // fallback 처리
)
public interface ProductClient {
    // 나중에 id로 요청하는 지, productId로 요청하는 지 확인할 것
    @GetMapping("{id}")
    String getProduct(@PathVariable("id") UUID id);
}
