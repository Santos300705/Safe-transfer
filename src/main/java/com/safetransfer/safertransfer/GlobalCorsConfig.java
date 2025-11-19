package com.safetransfer.safertransfer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Durante o trabalho, vamos liberar geral pra testar.
        // Se a prof quiser mais restrito depois, a gente fecha.
        config.addAllowedOrigin("https://front-lqki.onrender.com");
        // Se quiser testar de qualquer lugar, pode usar:
        // config.addAllowedOriginPattern("*");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica para todas as rotas da API
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}