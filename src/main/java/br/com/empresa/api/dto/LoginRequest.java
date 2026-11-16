package br.com.empresa.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados recebidos no login.
 *
 * @param login nome de login
 * @param senha senha em texto, trafegada apenas sobre HTTPS e nunca registrada em log
 */
public record LoginRequest(
        @NotBlank(message = "o login é obrigatório") String login,
        @NotBlank(message = "a senha é obrigatória") String senha) {
}
