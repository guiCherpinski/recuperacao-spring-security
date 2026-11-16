package br.com.empresa.api.config;

import br.com.empresa.api.entity.Perfil;
import br.com.empresa.api.entity.Usuario;
import br.com.empresa.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria o primeiro administrador na inicialização, para ser possível fazer login.
 * <p>
 * Login e senha vêm de variáveis de ambiente ({@code ADMIN_LOGIN} e
 * {@code ADMIN_SENHA}); em produção, troque esta etapa por um processo de
 * provisionamento controlado.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository repositorio;
    private final PasswordEncoder passwordEncoder;
    private final String login;
    private final String senha;

    /**
     * @param repositorio     repositório de usuários
     * @param passwordEncoder codificador de senhas
     * @param login           login do administrador inicial
     * @param senha           senha do administrador inicial (será salva como hash)
     */
    public DadosIniciais(UsuarioRepository repositorio,
                         PasswordEncoder passwordEncoder,
                         @Value("${app.admin.login}") String login,
                         @Value("${app.admin.senha}") String senha) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
        this.login = login;
        this.senha = senha;
    }

    /**
     * Cadastra o administrador caso ele ainda não exista.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    @Override
    public void run(String... args) {
        if (!repositorio.existsByLogin(login)) {
            repositorio.save(new Usuario(login, passwordEncoder.encode(senha), Perfil.ADMIN));
        }
    }
}
