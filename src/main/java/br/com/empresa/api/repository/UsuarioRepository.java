package br.com.empresa.api.repository;

import br.com.empresa.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acesso aos dados de {@link Usuario}.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo login.
     *
     * @param login nome de login
     * @return o usuário, se existir
     */
    Optional<Usuario> findByLogin(String login);

    /**
     * Verifica se já existe usuário com o login informado.
     *
     * @param login nome de login
     * @return {@code true} se o login já estiver em uso
     */
    boolean existsByLogin(String login);
}
