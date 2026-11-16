package br.com.empresa.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints de exemplo para testar as regras de acesso.
 * Substitua pelos controladores reais da sua API.
 */
@RestController
public class ExemploController {

    /**
     * Rota de uso comum: qualquer usuário autenticado (USER ou ADMIN).
     *
     * @param autenticacao usuário atual, fornecido pelo Spring Security
     * @return mensagem com o login de quem chamou
     */
    @GetMapping("/api/ola")
    public Map<String, String> ola(Authentication autenticacao) {
        return Map.of("mensagem", "Olá, " + autenticacao.getName() + "!");
    }

    /**
     * Rota administrativa. Além da regra por rota em {@code SecurityConfig},
     * o {@code @PreAuthorize} cria uma segunda camada de defesa no próprio método.
     * Só o perfil ADMIN pode chamar.
     *
     * @return mensagem de confirmação
     */
    @GetMapping("/admin/painel")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> painel() {
        return Map.of("mensagem", "Painel administrativo.");
    }
}
