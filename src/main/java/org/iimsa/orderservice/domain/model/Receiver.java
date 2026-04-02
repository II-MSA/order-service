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
public class Receiver {
    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "receiverName", length = 100)
    private String receiverName;

    // 주소추가
    @Column(name = "receiver_address", length = 200)
    private String receiverAddress;

    protected Receiver(UUID receiverId, CompanyProvider provider) {
        this.receiverId = receiverId;
        CompanyData companyData = provider.getCompany(receiverId);
        this.receiverName= companyData.name();
        this.receiverAddress = companyData.address();
    }

    public static Receiver from(UUID receiverId, CompanyProvider companyProvider) {
        return new Receiver(receiverId,companyProvider);
    }
}
