package com.safetransfer.safertransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {

    private static final Logger log = LoggerFactory.getLogger(GlobalCorsConfig.class);

    @Bean
    public CorsFilter corsFilter() {
        log.info(">>> Registrando CORS filter global...");

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");   // libera qualquer origem (pra testar)
        config.addAllowedHeader("*");          // qualquer header
        config.addAllowedMethod("*");          // GET/POST/PUT/DELETE/OPTIONS...
        config.setAllowCredentials(false);     // sem cookies, mais simples

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}