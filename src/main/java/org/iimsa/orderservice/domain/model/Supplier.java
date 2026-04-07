package org.iimsa.orderservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.iimsa.orderservice.domain.service.CompanyProvider;
import org.iimsa.orderservice.domain.service.dto.CompanyData;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기술 제약
public class Supplier {
    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Column(name = "supplier_hubId", length = 100)
    private UUID supplierHubId;

    @Column(name = "supplier_hubName", length = 100)
    private String supplierHubName;

    private Supplier(UUID supplierId, String supplierName, String supplierAddress) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    protected Supplier(UUID supplierId, CompanyProvider provider) {
        if (supplierId == null) {
            throw new IllegalArgumentException("supplierId cannot be null");
        }

        if (provider == null) {
            throw new IllegalArgumentException("provider cannot be null");
        }
        this.supplierId = supplierId;
        CompanyData companyData = provider.getCompany(supplierId);
        if (companyData == null) {
            throw new IllegalArgumentException("companyData is null");
        }
        this.supplierName = companyData.companyName();
        this.supplierHubId = companyData.hubId();
        this.supplierHubName = companyData.hubName();
    }

    public static Supplier from(UUID companyId, CompanyProvider companyProvider) {
        return new Supplier(companyId, companyProvider);
    }
}
