package com.operon.workforce.auth;

import com.operon.workforce.availability.AvailabilityRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityFlowTests {

    // Admin Data
    private static final String ADMIN_FIRST_NAME = "Administrator";
    private static final String ADMIN_LAST_NAME = "User";
    private static final String ADMIN_EMAIL = "admin@operon.local";
    private static final String ADMIN_PASSWORD = "admin12345";

    // User 1 Data
    private static final String FIRST_USER_FIRST_NAME = "Max";
    private static final String FIRST_USER_LAST_NAME = "Muster";
    private static final String FIRST_USER_EMAIL = "max@example.com";
    private static final String FIRST_USER_PASSWORD = "max12345";

    // User 2 Data
    private static final String SECOND_USER_FIRST_NAME = "Thomas";
    private static final String SECOND_USER_LAST_NAME = "Mueller";
    private static final String SECOND_USER_EMAIL = "thomas@example.com";
    private static final String SECOND_USER_PASSWORD = "thomas12345";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @BeforeEach
    public void prepareDatabase() {
        availabilityRepository.deleteAll();
        userRepository.deleteAll();

        String adminPasswordHash = passwordEncoder.encode(ADMIN_PASSWORD);

        User admin = new User(ADMIN_FIRST_NAME, ADMIN_LAST_NAME, ADMIN_EMAIL, adminPasswordHash, UserRole.ADMIN);
        admin.approve();
        userRepository.save(admin);
    }

    private Long registerAndReturnId(String firstName, String lastName, String email, String password) throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
                firstName,
                lastName,
                email,
                password);
        String jsonRegisterRequest = objectMapper.writeValueAsString(registerUserRequest);

        RequestBuilder postRegister = post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRegisterRequest);

        MvcResult registerResult = mockMvc.perform(postRegister)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.approvalStatus").value(UserApprovalStatus.PENDING.toString()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn();

        String idResponseBody = registerResult.getResponse().getContentAsString();
        JsonNode idResponseJson = objectMapper.readTree(idResponseBody);
        return idResponseJson.get("id").asLong();
    }

    private void approveUserWithAdminToken(Long userId, String adminToken) throws Exception {
        RequestBuilder approveUserRequest = patch("/api/users/{userId}/approve", userId)
                .header("Authorization", "Bearer " + adminToken);

        mockMvc.perform(approveUserRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.role").value(UserRole.EMPLOYEE.toString()))
                .andExpect(jsonPath("$.approvalStatus").value(UserApprovalStatus.APPROVED.toString()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
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

    @Test
    public void pendingUserCannotLogin() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
                FIRST_USER_FIRST_NAME,
                FIRST_USER_LAST_NAME,
                FIRST_USER_EMAIL,
                FIRST_USER_PASSWORD
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
                FIRST_USER_EMAIL,
                FIRST_USER_PASSWORD
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
        String adminToken = loginAndReturnToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        assertThat(adminToken).isNotBlank();
    }

    @Test
    public void adminCanApprovePendingUser() throws Exception {
        String adminToken = loginAndReturnToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        Long userId = registerAndReturnId(FIRST_USER_FIRST_NAME, FIRST_USER_LAST_NAME, FIRST_USER_EMAIL,
                FIRST_USER_PASSWORD);
        approveUserWithAdminToken(userId, adminToken);
    }

    @Test
    public void employeeCannotApprovePendingUser() throws Exception {
        String adminToken = loginAndReturnToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        Long firstUserId = registerAndReturnId(FIRST_USER_FIRST_NAME, FIRST_USER_LAST_NAME, FIRST_USER_EMAIL,
                FIRST_USER_PASSWORD);
        approveUserWithAdminToken(firstUserId, adminToken);

        String userToken = loginAndReturnToken(FIRST_USER_EMAIL, FIRST_USER_PASSWORD);

        Long secondUserId = registerAndReturnId(SECOND_USER_FIRST_NAME, SECOND_USER_LAST_NAME, SECOND_USER_EMAIL,
                SECOND_USER_PASSWORD);

        RequestBuilder approveUserRequest = patch("/api/users/{userId}/approve", secondUserId)
                .header("Authorization", "Bearer " + userToken);

        mockMvc.perform(approveUserRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    public void missingTokenCannotApprovePendingUser() throws Exception {
        Long userId = registerAndReturnId(FIRST_USER_FIRST_NAME, FIRST_USER_LAST_NAME, FIRST_USER_EMAIL,
                FIRST_USER_PASSWORD);

        RequestBuilder approveUserRequest = patch("/api/users/{userId}/approve", userId);

        mockMvc.perform(approveUserRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void invalidTokenCannotApprovePendingUser() throws Exception {
        Long userId = registerAndReturnId(FIRST_USER_FIRST_NAME, FIRST_USER_LAST_NAME, FIRST_USER_EMAIL,
                FIRST_USER_PASSWORD);

        RequestBuilder approveUserRequest = patch("/api/users/{userId}/approve", userId)
                .header("Authorization", "Bearer " + "invalid");

        mockMvc.perform(approveUserRequest)
                .andExpect(status().isUnauthorized());
    }
}
