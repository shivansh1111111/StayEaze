package com.business.api_gateway.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate-limit.default-requests-per-minute:100}")
    private int defaultRequestsPerMinute;

    // In-memory bucket cache (per user)
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public RateLimitFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.debug("Inside RateLimitFilter");
        return (exchange, chain) -> {
            if (!rateLimitEnabled) {
                return chain.filter(exchange);
            }

            // Get user identifier (IP or userId if authenticated)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String userKey = userId != null ? userId :
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

            // Get requests per minute from config or use default
            int requestsPerMinute = config.getRequestsPerMinute() > 0 ?
                    config.getRequestsPerMinute() : defaultRequestsPerMinute;

            // Get or create bucket for this user
            Bucket bucket = bucketCache.computeIfAbsent(userKey,
                    k -> createBucket(requestsPerMinute));

            // Try to consume 1 token
            if (bucket.tryConsume(1)) {
                // Request allowed
                log.debug("Request allowed for user: {}", userKey);
                return chain.filter(exchange);
            } else {
                // Rate limit exceeded
                log.warn("Rate limit exceeded for user: {}", userKey);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                response.getHeaders().add("X-Rate-Limit-Retry-After-Seconds", "60");

                String errorMessage = "{\"error\": \"Too Many Requests\", " +
                        "\"message\": \"Rate limit exceeded. Please try again later.\"}";

                return response.writeWith(Mono.just(response.bufferFactory()
                        .wrap(errorMessage.getBytes())));
            }
        };
    }

    private Bucket createBucket(int requestsPerMinute) {
        // Create bucket with capacity and refill rate
        Bandwidth limit = Bandwidth.classic(requestsPerMinute,
                Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Data
    public static class Config {
        private int requestsPerMinute;
    }
}
