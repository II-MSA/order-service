package org.iimsa.orderservice.infrastructure.provider;

import java.util.Objects;
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
        Objects.requireNonNull(companyId, "companyId는 필수입니다.");
        CompanyData company = companyClient.getCompany(companyId);
        if (company == null
                || company.companyId() == null
                || company.companyName() == null || company.companyName().isBlank()
                || company.hubId() == null
                || company.hubName() == null || company.hubName().isBlank()) {
            throw new IllegalStateException("유효한 회사 정보를 조회하지 못했습니다.");
        }
        return company;
    }
}
