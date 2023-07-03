package com.dh.msgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withJwkSetUri;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain (ServerHttpSecurity http) {
        http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().authenticated())
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(jwtSpec ->
                        jwtSpec.jwtDecoder(jwtDecoder())));

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(){
        return withJwkSetUri("http://localhost:8080/realms/ecommerce-realm/protocol/openid-connect/certs").build();
    }

    @Bean
    public WebSessionManager webSessionManager(){
        // permite que no se guarde datos de sesión del usuario
        return exchange -> Mono.empty();
    }
}
