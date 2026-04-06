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

    protected Receiver(UUID receiverId, CompanyProvider provider) {
        this.receiverId = receiverId;
        CompanyData companyData = provider.getCompany(receiverId);
        this.receiverName = companyData.name();
    }

    private Receiver(UUID receiverId, String name, String address) {
        this.receiverId = receiverId;
        this.receiverName = name;
    }

    public static Receiver from(UUID receiverId, CompanyProvider companyProvider) {
        return new Receiver(receiverId, companyProvider);
    }
}
