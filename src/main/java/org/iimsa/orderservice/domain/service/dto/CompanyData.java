package org.iimsa.orderservice.domain.service.dto;

import java.util.UUID;

public record CompanyData(
        UUID id,
        String name
) {
}
