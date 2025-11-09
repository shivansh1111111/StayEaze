package com.business.api_gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@RestController
public class GatewayController {

    public static final String GENERAL_SERVICE_NOT_AVAILABLE_CODE = "19906002";
    public static final String GENERAL_SERVICE_NOT_AVAILABLE_MSG = "Sorry! Currently Our Service is Not Available. Please try again later";

    private static final String SERVICE_UNDER_MAINTENANCE_CODE = "19907002";
    private static final String SERVICE_UNDER_MAINTENANCE_MSG = "Sorry! The service is currently under maintenance. Please try again after some time";

    @RequestMapping("/serviceNotAvailable")
    public ResponseEntity<Response> serviceNotAvailable(ServerWebExchange exchange) {
        Throwable throwable = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);

        String message = GENERAL_SERVICE_NOT_AVAILABLE_MSG;
        String responseCode = GENERAL_SERVICE_NOT_AVAILABLE_CODE;

        if (throwable != null) {
            log.info("Exception captured in fallback: {}", throwable.getClass().getName());
            log.info("Exception message captured in fallback: {}", throwable.getMessage());
            if (throwable instanceof java.net.ConnectException || throwable instanceof java.net.UnknownHostException) {
                log.error("Service is down due to maintenance");
                message = SERVICE_UNDER_MAINTENANCE_MSG;
                responseCode = SERVICE_UNDER_MAINTENANCE_CODE;
            } else if (throwable instanceof java.util.concurrent.TimeoutException) {
                log.error("Service is not available due to timeout");
            } else {
                log.error("Service is not available due to unknown error");
            }
        } else {
            log.info("No exception captured in fallback");
        }

        return new ResponseEntity<>(
                new Response(
                    responseCode,
                    message),
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
