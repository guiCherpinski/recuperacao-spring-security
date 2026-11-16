package br.com.empresa.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Política de CORS da API.
 * <p>
 * As origens permitidas vêm da configuração ({@code app.cors.origens}),
 * não ficam fixas no código, e nunca se usa "*" em produção.
 */
@Configuration
public class CorsConfig {

    /**
     * Declara quais origens, métodos e cabeçalhos o navegador pode usar.
     *
     * @param origens lista de origens permitidas (ex.: endereço do front-end)
     * @return a fonte de configuração usada pelo Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origens}") List<String> origens) {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(origens);
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuracao.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", configuracao);
        return fonte;
    }
}
