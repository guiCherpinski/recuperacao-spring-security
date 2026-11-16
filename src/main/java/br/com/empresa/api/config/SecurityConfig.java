package br.com.empresa.api.config;

import br.com.empresa.api.security.JwtAuthFilter;
import br.com.empresa.api.security.RestAccessDeniedHandler;
import br.com.empresa.api.security.RestAuthEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central de segurança da API.
 * <ul>
 *   <li>{@code @Configuration}: informa que a classe declara beans do Spring;</li>
 *   <li>{@code @EnableWebSecurity}: ativa e permite personalizar a segurança web;</li>
 *   <li>{@code @EnableMethodSecurity}: habilita anotações como {@code @PreAuthorize}.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Codificador de senhas. As senhas são guardadas somente como hash BCrypt
     * (com salt aleatório por usuário e custo de processamento configurável).
     *
     * @return o codificador usado no cadastro e na autenticação
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o gerenciador de autenticação, usado pelo endpoint de login.
     *
     * @param configuracao configuração de autenticação do Spring
     * @return o {@link AuthenticationManager} padrão
     * @throws Exception se não for possível obtê-lo
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuracao) throws Exception {
        return configuracao.getAuthenticationManager();
    }

    /**
     * Define a cadeia de filtros de segurança.
     * <p>
     * Ordem lógica: o {@link JwtAuthFilter} roda antes do filtro de usuário e
     * senha, preenche o contexto de segurança, e então o AuthorizationFilter
     * confere as regras de rota declaradas abaixo.
     *
     * @param http          construtor da configuração HTTP
     * @param jwtAuthFilter filtro que lê o token JWT
     * @param entryPoint    tratador de erro 401
     * @param deniedHandler tratador de erro 403
     * @return a cadeia de filtros configurada
     * @throws Exception se a configuração falhar
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthFilter jwtAuthFilter,
                                           RestAuthEntryPoint entryPoint,
                                           RestAccessDeniedHandler deniedHandler) throws Exception {
        http
                // CSRF desativado: a API não usa sessão nem cookies de autenticação; o token JWT
                // é enviado manualmente no cabeçalho Authorization, então o navegador não o anexa
                // sozinho. Se o token passar a ficar em cookie, esta proteção deve ser reativada.
                .csrf(csrf -> csrf.disable())
                // Usa o bean CorsConfigurationSource declarado em CorsConfig.
                .cors(Customizer.withDefaults())
                // Sem sessão no servidor: cada requisição precisa carregar o token.
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(regras -> regras
                        // 1) Rotas públicas
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // 2) Rotas restritas por perfil (as mais específicas primeiro)
                        .requestMatchers(HttpMethod.POST, "/auth/registrar").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 3) Rotas de uso comum
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        // 4) Todo o resto exige autenticação: rota esquecida fica fechada, não aberta
                        .anyRequest().authenticated())
                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
