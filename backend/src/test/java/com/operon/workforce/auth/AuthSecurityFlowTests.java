package com.operon.workforce.auth;

import com.operon.workforce.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    public void pendingUserCannotLogin() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
                "Max",
                "Muster",
                "max@example.com",
                "password123"
        );
        String jsonRegisterRequest = objectMapper.writeValueAsString(registerUserRequest);

        RequestBuilder postRegister = post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRegisterRequest);

        mockMvc.perform(postRegister)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.approvalStatus").value(UserApprovalStatus.PENDING.toString()))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginRequest loginRequest = new LoginRequest(
                "max@example.com",
                "password123"
        );
        String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);

        RequestBuilder postLogin = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLoginRequest);

        mockMvc.perform(postLogin)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value(UserNotApprovedException.errorMessage));
    }

    @Test
    public void adminCanLoginAndReceivesToken() throws Exception {
        String adminFirstName = "Administrator";
        String adminLastName = "User";
        String adminEmail = "admin@operon.local";
        String adminPassword = "admin12345";
        String adminPasswordHash = passwordEncoder.encode(adminPassword);

        User admin = new User(adminFirstName, adminLastName, adminEmail, adminPasswordHash, UserRole.ADMIN);
        admin.approve();
        userRepository.save(admin);

        String adminToken = loginAndReturnToken(adminEmail, adminPassword);
        assertThat(adminToken).isNotBlank();
    }

    private String loginAndReturnToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                email,
                password
        );
        String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);

        RequestBuilder postLogin = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLoginRequest);

        MvcResult loginResult = mockMvc.perform(postLogin)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String loginResponseBody = loginResult.getResponse().getContentAsString();
        JsonNode loginResponseJson = objectMapper.readTree(loginResponseBody);
        return loginResponseJson.get("token").asString();
    }
}
