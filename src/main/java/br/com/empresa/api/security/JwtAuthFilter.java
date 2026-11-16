package br.com.empresa.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que autentica a requisição a partir do cabeçalho
 * {@code Authorization: Bearer <token>}.
 * <p>
 * Se o token for válido, o usuário é colocado no {@link SecurityContextHolder}.
 * Se não houver token (ou ele for inválido), o filtro apenas segue a cadeia:
 * quem bloqueia o acesso às rotas protegidas é o AuthorizationFilter.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final TokenService tokenService;
    private final UsuarioDetailsService usuarioDetailsService;

    /**
     * @param tokenService          serviço de validação de tokens
     * @param usuarioDetailsService carregador de usuários
     */
    public JwtAuthFilter(TokenService tokenService, UsuarioDetailsService usuarioDetailsService) {
        this.tokenService = tokenService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    /**
     * Lê o token, valida e registra a autenticação no contexto de segurança.
     *
     * @param request  requisição HTTP
     * @param response resposta HTTP
     * @param chain    restante da cadeia de filtros
     * @throws ServletException em falha de servlet
     * @throws IOException      em falha de leitura/escrita
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String cabecalho = request.getHeader("Authorization");

        if (cabecalho != null && cabecalho.startsWith(PREFIXO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = cabecalho.substring(PREFIXO.length());

            if (tokenService.tokenValido(token)) {
                try {
                    UserDetails usuario = usuarioDetailsService
                            .loadUserByUsername(tokenService.extrairLogin(token));

                    UsernamePasswordAuthenticationToken autenticacao =
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(autenticacao);
                } catch (UsernameNotFoundException e) {
                    // Usuário apagado depois da emissão do token: segue como não autenticado.
                }
            }
        }

        chain.doFilter(request, response);
    }
}
