package com.business.api_gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class ExceptionCaptureFilter extends AbstractGatewayFilterFactory<ExceptionCaptureFilter.Config> {

    public static class Config {}

    public ExceptionCaptureFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange)
            .onErrorResume(throwable -> {
                log.error("Inside Exception Capture Filter");

                exchange.getAttributes().put(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, throwable);

                log.error("Exception Details:", throwable);
                log.error("Request Path: {}", exchange.getRequest().getPath());
                log.error("Request Method: {}", exchange.getRequest().getMethod());

                return Mono.empty();
            });
    }
}
