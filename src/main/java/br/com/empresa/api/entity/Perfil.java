package br.com.empresa.api.entity;

/**
 * Perfis de acesso (papéis) existentes no sistema.
 * <p>
 * Cada valor é convertido em uma autoridade com o prefixo {@code ROLE_}
 * (por exemplo, {@code ROLE_ADMIN}), que é o formato esperado pelo
 * {@code hasRole("ADMIN")} do Spring Security.
 */
public enum Perfil {
    /** Pode acessar as rotas administrativas e cadastrar usuários. */
    ADMIN,
    /** Usuário comum, com acesso às rotas de uso geral. */
    USER
}
