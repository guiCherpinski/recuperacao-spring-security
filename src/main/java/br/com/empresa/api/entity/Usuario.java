package br.com.empresa.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Usuário do sistema, persistido no banco e compatível com o Spring Security.
 * <p>
 * O campo {@code senha} armazena <strong>somente o hash BCrypt</strong>,
 * nunca a senha digitada pelo usuário.
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String login;

    /** Hash BCrypt da senha (o BCrypt gera 60 caracteres). */
    @Column(nullable = false, length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    /** Construtor exigido pelo JPA. */
    protected Usuario() {
    }

    /**
     * Cria um usuário.
     *
     * @param login  nome de login (único)
     * @param senha  senha <strong>já criptografada</strong> com o PasswordEncoder
     * @param perfil perfil de acesso do usuário
     */
    public Usuario(String login, String senha, Perfil perfil) {
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    /**
     * Converte o perfil em autoridade do Spring Security.
     *
     * @return lista com uma autoridade no formato {@code ROLE_<PERFIL>}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    /** @return o hash BCrypt da senha, usado pelo framework na comparação do login */
    @Override
    public String getPassword() {
        return senha;
    }

    /** @return o login do usuário */
    @Override
    public String getUsername() {
        return login;
    }
}
