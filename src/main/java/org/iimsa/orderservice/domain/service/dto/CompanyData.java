package org.iimsa.orderservice.domain.service.dto;

import java.util.UUID;

public record CompanyData(
        UUID companyId,
        String companyName,
        UUID hubId,
        String hubName
) {
}
