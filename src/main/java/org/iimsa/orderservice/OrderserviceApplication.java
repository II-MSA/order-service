package org.iimsa.orderservice;

import org.iimsa.common.exception.GlobalExceptionAdvice;
import org.iimsa.config.security.LoginFilter;
import org.iimsa.config.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@Import({SecurityConfig.class, LoginFilter.class, GlobalExceptionAdvice.class})
@ConfigurationPropertiesScan("org.iimsa.orderservice.infrastructure.messaging.kafka") // 해당 패키지 스캔
public class OrderserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderserviceApplication.class, args);
    }

}
