package com.operon.workforce.availability;

import com.operon.workforce.auth.LoginRequest;
import com.operon.workforce.user.User;
import com.operon.workforce.user.UserRepository;
import com.operon.workforce.user.UserRole;
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

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AvailabilityControllerTests {

    // User 1 Data
    private static final String FIRST_USER_FIRST_NAME = "Max";
    private static final String FIRST_USER_LAST_NAME = "Muster";
    private static final String FIRST_USER_EMAIL = "max@example.com";
    private static final String FIRST_USER_PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void prepareDatabase() {
        availabilityRepository.deleteAll();
        userRepository.deleteAll();

        String userPasswordHash = passwordEncoder.encode(FIRST_USER_PASSWORD);
        User user = new User(FIRST_USER_FIRST_NAME, FIRST_USER_LAST_NAME, FIRST_USER_EMAIL, userPasswordHash,
                UserRole.EMPLOYEE);
        user.approve();
        userRepository.save(user);
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
    public void approvedEmployeeCanCreateAvailability() throws Exception {
        String userToken = loginAndReturnToken(FIRST_USER_EMAIL, FIRST_USER_PASSWORD);

        CreateAvailabilityRequest createAvailabilityRequest = new CreateAvailabilityRequest(
                Instant.parse("2026-01-01T08:00:00Z"),
                Instant.parse("2026-01-01T14:00:00Z"),
                "Doctor appointment"
        );
        String jsonCreateAvailabilityRequest = objectMapper.writeValueAsString(createAvailabilityRequest);

        RequestBuilder postCreateAvailability = post("/api/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreateAvailabilityRequest)
                .header("Authorization", "Bearer " + userToken);

        mockMvc.perform(postCreateAvailability)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.startTime").value("2026-01-01T08:00:00Z"))
                .andExpect(jsonPath("$.endTime").value("2026-01-01T14:00:00Z"))
                .andExpect(jsonPath("$.note").value("Doctor appointment"))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(availabilityRepository.count()).isEqualTo(1);
    }
}
