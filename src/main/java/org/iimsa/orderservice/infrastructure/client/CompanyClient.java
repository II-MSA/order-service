package org.iimsa.orderservice.infrastructure.client;

import java.util.UUID;
import org.iimsa.orderservice.domain.service.dto.CompanyData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "company-service",
        fallbackFactory = CompanyClientFallbackFactory.class
)
public interface CompanyClient {
    @GetMapping("/{companyId}")
    CompanyData getCompany(@PathVariable("companyId") UUID companyId);

}
