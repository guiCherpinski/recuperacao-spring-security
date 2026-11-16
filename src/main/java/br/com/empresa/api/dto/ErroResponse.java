package br.com.empresa.api.dto;

import java.time.Instant;

/**
 * Formato padrão das respostas de erro da API.
 * <p>
 * A mensagem é propositalmente genérica: nada de nomes de classe ou rastros
 * de pilha, para não revelar detalhes internos ao cliente.
 *
 * @param status   código HTTP
 * @param mensagem descrição curta do erro
 * @param dataHora momento do erro, em formato ISO-8601
 */
public record ErroResponse(int status, String mensagem, String dataHora) {

    /**
     * Cria uma resposta de erro com a data e hora atuais.
     *
     * @param status   código HTTP
     * @param mensagem descrição do erro
     * @return a resposta pronta para serialização em JSON
     */
    public static ErroResponse de(int status, String mensagem) {
        return new ErroResponse(status, mensagem, Instant.now().toString());
    }
}
