package br.com.empresa.api.security;

import br.com.empresa.api.dto.ErroResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Responde <strong>401 Unauthorized</strong> quando a requisição chega sem
 * autenticação válida (sem token, token inválido ou expirado).
 */
@Component
public class RestAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    /**
     * @param mapper conversor JSON (Jackson 3) configurado pelo Spring Boot
     */
    public RestAuthEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Escreve a resposta 401 em JSON.
     *
     * @param request       requisição HTTP
     * @param response      resposta HTTP
     * @param authException motivo da falha (não é exposto ao cliente)
     * @throws IOException em falha de escrita
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(),
                ErroResponse.de(401, "Autenticação necessária ou token inválido."));
    }
}
