package org.iimsa.orderservice.infrastructure.messaging.kafka;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "topics.order")
public record OrderTopicProperties(
        @NotBlank String created // yml의 topics.order.created와 매핑
) {
}
