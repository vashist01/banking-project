package com.banking.gateway.filter;

import com.banking.gateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter implements GlobalFilter, Ordered {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private final RedisTemplate<String,Object> redisTemplate;
    private final JwtUtils jwtUtils;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain gatewayFilterChain){
        String path = exchange.getRequest().getURI().getPath();
        log.info("Request received in api-gateway");
        if (path.contains("/api/auth/login") ||
                path.contains("/api/auth/register") ||
                path.contains("/api/auth/register/admin") ||
                path.contains("/actuator")) {
            return gatewayFilterChain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(authHeader ==null || !authHeader.startsWith(TOKEN_PREFIX)){
            log.warn("Missing or invalid Authorization header");
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }
        String token = authHeader.substring(TOKEN_PREFIX.length());
        // Check Token is black listed or not
        boolean isTokenBlackList = redisTemplate.hasKey(BLACKLIST_PREFIX+token);
       if(isTokenBlackList){
           log.warn("Token is blacklisted");
           return unauthorizedResponse(exchange, "Token is blacklisted");
       }
       // validate token
        boolean isTokenValid = jwtUtils.isTokenValid(token);
       if(!isTokenValid){
           log.warn("Invalid JWT token");
           return unauthorizedResponse(exchange, "Invalid JWT token");
       }
        // Extract user information from token
        try {

            Claims claims = jwtUtils.extractAllClaims(token);
            String username = claims.get("sub").toString();
            Integer userId = (Integer) claims.get("userId");
            Set<String> roles = Set.of(claims.get("roles").toString());

            // Add user context to headers for downstream services
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate().header("userId",String.valueOf(userId))
                            .header("email",username).
                            header("roles",String.join(",",roles)).header("is_token_valid"
                                    ,String.valueOf(true)).build())
                    .build();
            log.debug("Request authenticated for user: {}", username);
            return gatewayFilterChain.filter(mutatedExchange);

        } catch (Exception e) {
            log.error("Error extracting user context: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Invalid token");
        }
    }
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        String responseBody = String.format(
                "{\"error\": \"Unauthorized\", \"message\": \"%s\"}",
                message
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(responseBody.getBytes(StandardCharsets.UTF_8));

        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // High priority
    }
}
