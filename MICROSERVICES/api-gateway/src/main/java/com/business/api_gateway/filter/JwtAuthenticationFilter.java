package com.business.api_gateway.filter;

import com.business.api_gateway.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Inside JwtAuthenticationFilter");
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Extract Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            // Check Bearer prefix
            if (!authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header format", HttpStatus.UNAUTHORIZED);
            }

            // Extract token
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Extract JTI for blacklist check
                String jti = jwtUtil.extractJti(token);

                // Check if token is blacklisted (logout)
                return reactiveRedisTemplate.hasKey("blacklist:" + jti)
                        .flatMap(isBlacklisted -> {
                            if (Boolean.TRUE.equals(isBlacklisted)) {
                                log.warn("Attempted to use blacklisted token: {}", jti);
                                return onError(exchange, "Token has been revoked", HttpStatus.UNAUTHORIZED);
                            }

                            // Extract user information
                            String userId = jwtUtil.extractUserId(token);
                            String email = jwtUtil.extractEmail(token);
                            String roles = jwtUtil.extractRoles(token);
                            String tokenType = jwtUtil.extractTokenType(token);

                            // Verify it's an access token (not refresh token)
                            if (!"ACCESS".equals(tokenType)) {
                                return onError(exchange, "Invalid token type", HttpStatus.UNAUTHORIZED);
                            }

                            log.debug("JWT validated successfully for user: {}", email);

                            // Add user context to request headers for downstream services
                            ServerHttpRequest modifiedRequest = request.mutate()
                                    .header("X-User-Id", userId)
                                    .header("X-User-Email", email)
                                    .header("X-User-Roles", roles)
                                    .header("X-Token-JTI", jti)
                                    .build();

                            // Continue with modified request
                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        });

            } catch (Exception e) {
                log.error("Error processing JWT: ", e);
                return onError(exchange, "Token processing failed", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        String errorMessage = String.format("{\"error\": \"%s\", \"message\": \"%s\"}",
                httpStatus.getReasonPhrase(), error);

        log.warn("Authentication error: {} - Status: {}", error, httpStatus);

        return response.writeWith(Mono.just(response.bufferFactory()
                .wrap(errorMessage.getBytes())));
    }

    public static class Config {
        // Configuration properties if needed
    }
}
