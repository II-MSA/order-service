package org.iimsa.orderservice.domain.service;

import java.util.UUID;
import org.iimsa.orderservice.domain.service.dto.CompanyData;

public interface CompanyProvider {
    CompanyData getCompany(UUID companyId);
}
