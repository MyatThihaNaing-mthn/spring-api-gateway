package com.thiha.sneakershop.apigateway;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalFilterConfiguration {
    final Logger logger = LoggerFactory.getLogger(GlobalFilterConfiguration.class);

    @Bean
    @Order(1)
    GlobalFilter firstPrefilter(){
        
        return (exchange, chain) -> {
            logger.info("second pre filter");
            return chain.filter(exchange).then(Mono.fromRunnable(
                ()->{
                    logger.info("second post filter");
                }
            ));
        };
    }
}
