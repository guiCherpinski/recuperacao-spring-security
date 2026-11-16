package br.com.empresa.api.security;

import br.com.empresa.api.dto.ErroResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Responde <strong>403 Forbidden</strong> quando o usuário está autenticado,
 * mas o perfil dele não permite acessar o recurso.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    /**
     * @param mapper conversor JSON (Jackson 3) configurado pelo Spring Boot
     */
    public RestAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Escreve a resposta 403 em JSON.
     *
     * @param request               requisição HTTP
     * @param response              resposta HTTP
     * @param accessDeniedException motivo da negação (não é exposto ao cliente)
     * @throws IOException em falha de escrita
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(),
                ErroResponse.de(403, "Você não tem permissão para acessar este recurso."));
    }
}
