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
public class Receiver {
    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "receiverName", length = 100)
    private String receiverName;

    @Column(name = "receiver_hubId", length = 100)
    private UUID receiverHubId;

    @Column(name = "receiverName", length = 100)
    private String receiverHubName;

    protected Receiver(UUID receiverId, CompanyProvider provider) {
        if (receiverId == null) {
            throw new IllegalArgumentException("receiverId is null");
        }
        if (provider == null) {
            throw new IllegalArgumentException("provider is null");
        }
        this.receiverId = receiverId;
        CompanyData companyData = provider.getCompany(receiverId);
        if (companyData == null) {
            throw new IllegalArgumentException("companyData is null");
        }
        this.receiverName = companyData.companyName();
        this.receiverHubId = companyData.hubId();
        this.receiverHubName = companyData.hubName();
    }

    public static Receiver from(UUID receiverId, CompanyProvider companyProvider) {
        return new Receiver(receiverId, companyProvider);
    }
}
