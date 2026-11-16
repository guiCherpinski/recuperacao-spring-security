package br.com.empresa.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Gera e valida tokens JWT.
 * <p>
 * O segredo de assinatura e o tempo de expiração são lidos da configuração
 * externa (variáveis de ambiente), nunca escritos no código-fonte.
 */
@Service
public class TokenService {

    private final SecretKey chave;
    private final long expiracaoMs;

    /**
     * @param segredo     segredo de assinatura (mínimo de 32 caracteres para HS256)
     * @param expiracaoMs validade do token em milissegundos
     */
    public TokenService(@Value("${app.jwt.secret}") String segredo,
                        @Value("${app.jwt.expiracao-ms}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    /**
     * Gera um token assinado para o usuário autenticado.
     *
     * @param usuario usuário que acabou de se autenticar
     * @return o JWT em formato compacto
     */
    public String gerarToken(UserDetails usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    /**
     * Extrai o login (campo {@code sub}) de um token já validado.
     *
     * @param token JWT recebido
     * @return o login do usuário dono do token
     * @throws JwtException se o token for inválido ou estiver expirado
     */
    public String extrairLogin(String token) {
        return lerClaims(token).getSubject();
    }

    /**
     * Verifica se o token tem assinatura válida e não está expirado.
     *
     * @param token JWT recebido
     * @return {@code true} se o token for aceito
     */
    public boolean tokenValido(String token) {
        try {
            lerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** @return a validade configurada, em segundos */
    public long getExpiracaoSegundos() {
        return expiracaoMs / 1000;
    }

    private Claims lerClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
