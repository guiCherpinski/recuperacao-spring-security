package br.com.empresa.api.exception;

import br.com.empresa.api.dto.ErroResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Manipulador global de exceções dos controladores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata falha de validação dos DTOs (campos vazios, tamanho inválido etc.).
     *
     * @param ex exceção lançada pelo Bean Validation
     * @return resposta 400 listando os campos com problema
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ErroResponse.de(400, mensagem));
    }

    /**
     * Trata login ou senha incorretos.
     * <p>
     * A mensagem é a mesma para os dois casos, para não revelar se o login existe.
     *
     * @param ex exceção lançada na autenticação
     * @return resposta 401 genérica
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> tratarCredenciais(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(ErroResponse.de(401, "Login ou senha inválidos."));
    }
}
