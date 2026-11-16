package br.com.empresa.api;

import br.com.empresa.api.entity.Perfil;
import br.com.empresa.api.entity.Usuario;
import br.com.empresa.api.repository.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração das regras de segurança.
 * Cada teste cobre um cenário do roteiro do passo 15 do tutorial.
 * As propriedades de teste ficam em {@code src/test/resources/application.properties}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SegurancaIntegracaoTest {

    private static final String SENHA_USER = "SenhaUser@12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository repositorio;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Garante que existe um usuário comum para os testes de perfil. */
    @BeforeEach
    void prepararUsuarioComum() {
        if (!repositorio.existsByLogin("maria")) {
            repositorio.save(new Usuario("maria", passwordEncoder.encode(SENHA_USER), Perfil.USER));
        }
    }

    /** Cenário 1: rota protegida sem token deve responder 401. */
    @Test
    void rotaProtegidaSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/ola")).andExpect(status().isUnauthorized());
    }

    /** Cenário 2: login com senha errada deve responder 401. */
    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
                        .content("{\"login\":\"maria\",\"senha\":\"senha-errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    /** Cenário 3: login válido deve responder 200 e devolver o token. */
    @Test
    void loginValidoRetornaToken() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
                        .content("{\"login\":\"maria\",\"senha\":\"" + SENHA_USER + "\"}"))
                .andExpect(status().isOk());
    }

    /** Cenário 4: rota comum com token válido deve responder 200. */
    @Test
    void rotaComumComTokenRetorna200() throws Exception {
        String token = obterToken("maria", SENHA_USER);
        mockMvc.perform(get("/api/ola").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /** Cenário 5: rota de administração com perfil USER deve responder 403. */
    @Test
    void rotaAdminComUsuarioComumRetorna403() throws Exception {
        String token = obterToken("maria", SENHA_USER);
        mockMvc.perform(get("/admin/painel").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    /** Cenário 6: rota de administração com perfil ADMIN deve responder 200. */
    @Test
    void rotaAdminComAdministradorRetorna200() throws Exception {
        String token = obterToken("admin", "SenhaAdmin@12345");
        mockMvc.perform(get("/admin/painel").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * Faz login e devolve o token JWT.
     *
     * @param login login do usuário
     * @param senha senha em texto
     * @return o token recebido
     * @throws Exception se a requisição falhar
     */
    private String obterToken(String login, String senha) throws Exception {
        String json = mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON)
                        .content("{\"login\":\"" + login + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.token");
    }
}
