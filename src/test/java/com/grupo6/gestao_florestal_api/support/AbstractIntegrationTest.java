package com.grupo6.gestao_florestal_api.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.Role;
import com.grupo6.gestao_florestal_api.domain.User;
import com.grupo6.gestao_florestal_api.domain.enums.FuncaoColaborador;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.repository.RoleRepository;
import com.grupo6.gestao_florestal_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("gestao_florestal_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected ColaboradorRepository colaboradorRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected final String adminUsername = "admin-it";
    protected final String adminEmail = "admin-it@florestal.com";
    protected final String adminPassword = "admin123";
    protected final String colaboradorUsername = "colab-it";
    protected final String colaboradorEmail = "colab-it@florestal.com";
    protected final String colaboradorPassword = "colab123";

    protected UUID adminUserId;
    protected UUID colaboradorUserId;
    protected UUID colaboradorId;

    @BeforeEach
    void ensureUsers() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        Role colaboradorRole = roleRepository.findByName("ROLE_COLABORADOR").orElseThrow();

        User admin = userRepository.findByUsername(adminUsername).orElseGet(() -> {
            User user = new User();
            user.setUsername(adminUsername);
            user.setEmail(adminEmail);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setRoles(Set.of(adminRole));
            return userRepository.save(user);
        });
        adminUserId = admin.getId();

        User colaboradorUser = userRepository.findByUsername(colaboradorUsername).orElseGet(() -> {
            User user = new User();
            user.setUsername(colaboradorUsername);
            user.setEmail(colaboradorEmail);
            user.setPassword(passwordEncoder.encode(colaboradorPassword));
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setRoles(Set.of(colaboradorRole));
            return userRepository.save(user);
        });
        colaboradorUserId = colaboradorUser.getId();

        Colaborador colaborador = colaboradorRepository.findByUserUsername(colaboradorUsername).orElseGet(() -> {
            Colaborador entity = Colaborador.builder()
                    .nomeCompleto("Colaborador Integração")
                    .cpf("999.999.999-99")
                    .matricula("MAT-IT-001")
                    .funcao(FuncaoColaborador.TECNICO_FLORESTAL)
                    .areaAtuacao("Campo")
                    .dataAdmissao(LocalDate.now().minusYears(1))
                    .qualificacoes("Operação de campo")
                    .certificacoes("NR")
                    .contatoEmergenciaNome("Contato IT")
                    .contatoEmergenciaTel("71999999999")
                    .ativo(true)
                    .user(colaboradorUser)
                    .build();
            return colaboradorRepository.save(entity);
        });
        colaboradorId = colaborador.getId();
    }

    protected String login(String login, String password) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "%s",
                                  "password": "%s"
                                }
                                """.formatted(login, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
