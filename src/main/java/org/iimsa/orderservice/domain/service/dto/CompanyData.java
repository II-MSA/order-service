package org.iimsa.orderservice.domain.service.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CompanyData(
        UUID companyId,
        String companyName,
        UUID hubId,
        String hubName
) {
}
