package br.com.empresa.api.controller;

import br.com.empresa.api.dto.CadastroRequest;
import br.com.empresa.api.dto.LoginRequest;
import br.com.empresa.api.dto.TokenResponse;
import br.com.empresa.api.entity.Usuario;
import br.com.empresa.api.repository.UsuarioRepository;
import br.com.empresa.api.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Endpoints de autenticação: login (público) e cadastro (somente ADMIN).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository repositorio;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param authenticationManager gerenciador de autenticação do Spring Security
     * @param tokenService          gerador de tokens JWT
     * @param repositorio           repositório de usuários
     * @param passwordEncoder       codificador de senhas (BCrypt)
     */
    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UsuarioRepository repositorio,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica o usuário e devolve um token JWT.
     * <p>
     * Credenciais incorretas geram {@code BadCredentialsException}, convertida em
     * 401 pelo {@code GlobalExceptionHandler}.
     *
     * @param requisicao login e senha
     * @return o token e seu tempo de validade
     */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest requisicao) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requisicao.login(), requisicao.senha()));

        Usuario usuario = (Usuario) autenticacao.getPrincipal();
        return new TokenResponse(tokenService.gerarToken(usuario), "Bearer", tokenService.getExpiracaoSegundos());
    }

    /**
     * Cadastra um novo usuário. A senha é criptografada antes de ser salva.
     * Rota restrita ao perfil ADMIN (ver {@code SecurityConfig}).
     *
     * @param requisicao dados do novo usuário
     * @throws ResponseStatusException 409 se o login já existir
     */
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@Valid @RequestBody CadastroRequest requisicao) {
        if (repositorio.existsByLogin(requisicao.login())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já está em uso.");
        }
        repositorio.save(new Usuario(
                requisicao.login(),
                passwordEncoder.encode(requisicao.senha()),
                requisicao.perfil()));
    }
}
