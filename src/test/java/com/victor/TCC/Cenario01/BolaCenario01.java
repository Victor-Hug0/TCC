package com.victor.TCC.Cenario01;

import com.victor.TCC.dto.request.LoginRequest;
import com.victor.TCC.dto.request.RegisterUserRequest;
import com.victor.TCC.dto.response.LoginResponse;
import com.victor.TCC.dto.response.RegisterUserResponse;
import com.victor.TCC.entity.User;
import com.victor.TCC.entity.UserRole;
import com.victor.TCC.exception.ResourceNotFoundException;
import com.victor.TCC.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("BOLA Vulnerability Tests - Scenario 01: Unauthorized Admin Creation")
class BolaCenario01 {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User normalUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Cleanup: garantir estado limpo antes de cada teste
        userRepository.deleteAll();

        // Arrange: criar usuário normal
        normalUser = createUser(
                "Normal User",
                "normalUser@email.com",
                "normalPassword",
                Set.of(UserRole.CUSTOMER)
        );

        // Arrange: criar usuário administrador
        adminUser = createUser(
                "Admin User",
                "admin@email.com",
                "adminPassword",
                Set.of(UserRole.ADMIN)
        );
    }

    @AfterEach
    void tearDown() {
        // Cleanup: garantir limpeza após cada teste
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("CVE-2023-3287: Broken Object Level Authorization")
    class VulnerableScenario {

        @Test
        @DisplayName("Deve permitir usuário normal criar conta admin (vulnerável)")
        void shouldAllowNormalUserToCreateAdminAccount_Vulnerable() {
            // Arrange
            String normalUserToken = loginAs("normalUser@email.com", "normalPassword");
            RegisterUserRequest evilAdminRequest = new RegisterUserRequest(
                    "Evil Admin",
                    "evil@attacker.com",
                    "evilPassword"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(normalUserToken);
            HttpEntity<RegisterUserRequest> request = new HttpEntity<>(evilAdminRequest, headers);

            // Act
            ResponseEntity<RegisterUserResponse> response = restTemplate.postForEntity(
                    "/api/v1/admin",
                    request,
                    RegisterUserResponse.class
            );

            // Assert - usando AssertJ para melhor legibilidade
            assertThat(response.getStatusCode().value()).isEqualTo(201);
            assertThat(response.getBody()).isNotNull();
            assertThat(Objects.requireNonNull(response.getBody()).email()).isEqualTo("evil@attacker.com");

            // Assert - verificar se usuário foi realmente criado no banco
            User createdUser = userRepository.findByEmail(("evil@attacker.com"))
                    .orElseThrow(() -> new ResourceNotFoundException("User (evil@attacker.com) not found"));

            assertThat(createdUser.getRoles()).isEqualTo(Set.of(UserRole.ADMIN));
            assertThat(createdUser.getName()).isEqualTo("Evil Admin");
        }
    
    }

    // Helper Methods

    private String loginAs(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                "/auth/login",
                loginRequest,
                LoginResponse.class
        );

        assertThat(loginResponse.getStatusCode().is2xxSuccessful())
                .as("Login deve ser bem-sucedido para %s", email)
                .isTrue();

        assertThat(loginResponse.getBody())
                .as("Resposta de login não deve ser nula")
                .isNotNull();

        return loginResponse.getBody().token();
    }

    private User createUser(String name, String email, String password, Set<UserRole> roles) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        return userRepository.save(user);
    }
}

