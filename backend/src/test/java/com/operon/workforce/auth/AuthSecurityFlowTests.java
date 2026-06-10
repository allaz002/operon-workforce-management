package com.operon.workforce.auth;

import com.operon.workforce.user.RegisterUserRequest;
import com.operon.workforce.user.UserApprovalStatus;
import com.operon.workforce.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import tools.jackson.databind.ObjectMapper;

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
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

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
}
