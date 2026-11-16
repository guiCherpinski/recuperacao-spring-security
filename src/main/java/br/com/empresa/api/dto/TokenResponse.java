package br.com.empresa.api.dto;

/**
 * Resposta do login bem-sucedido.
 *
 * @param token            JWT assinado
 * @param tipo             esquema do cabeçalho Authorization (sempre "Bearer")
 * @param expiraEmSegundos tempo de validade do token, em segundos
 */
public record TokenResponse(String token, String tipo, long expiraEmSegundos) {
}
