package org.iimsa.orderservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

    @Column(name = "supplierName", length = 100)
    private String supplierName;

    // 주소추가
    @Column(name = "supplier_address", length = 200)
    private String supplierAddress;


    protected Supplier(UUID supplierId, CompanyProvider provider) {
        if (supplierId == null) {
            throw new IllegalArgumentException("supplierId cannot be null");
        }

        if (provider == null) {
            throw new IllegalArgumentException("provider cannot be null");
        }
        this.supplierId = supplierId;
        CompanyData companyData = provider.getCompany(supplierId);
        this.supplierName = companyData.name();
        this.supplierAddress = companyData.address();
    }

    public static Supplier from(UUID companyId, CompanyProvider companyProvider) {
        return new Supplier(companyId, companyProvider);
    }
}
