package br.com.empresa.api.security;

import br.com.empresa.api.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega o usuário do banco de dados durante a autenticação.
 * <p>
 * É chamado pelo Spring Security no login e pelo {@link JwtAuthFilter}
 * a cada requisição autenticada por token.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repositorio;

    /**
     * @param repositorio repositório de usuários
     */
    public UsuarioDetailsService(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Busca o usuário pelo login.
     *
     * @param login login informado
     * @return os dados do usuário, incluindo hash da senha e autoridades
     * @throws UsernameNotFoundException se não existir usuário com esse login
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return repositorio.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }
}
