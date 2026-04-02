package org.iimsa.orderservice.infrastructure.provider;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.iimsa.orderservice.domain.service.CompanyProvider;
import org.iimsa.orderservice.domain.service.dto.CompanyData;
import org.iimsa.orderservice.infrastructure.client.CompanyClient;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CompanyProviderImpl implements CompanyProvider {
    private final CompanyClient companyClient;
    @Override
    public CompanyData getCompany(UUID companyId) {
        return companyClient.getCompany(companyId);
    }
}
