package br.com.empresa.api.dto;

import br.com.empresa.api.entity.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados para cadastrar um novo usuário (operação restrita a administradores).
 * <p>
 * O limite de 72 caracteres existe porque o BCrypt só considera os 72
 * primeiros bytes da senha.
 *
 * @param login  nome de login desejado
 * @param senha  senha em texto, com tamanho mínimo de 10 caracteres
 * @param perfil perfil de acesso do novo usuário
 */
public record CadastroRequest(
        @NotBlank @Size(min = 3, max = 60, message = "o login deve ter de 3 a 60 caracteres") String login,
        @NotBlank @Size(min = 10, max = 72, message = "a senha deve ter de 10 a 72 caracteres") String senha,
        @NotNull(message = "o perfil é obrigatório") Perfil perfil) {
}
