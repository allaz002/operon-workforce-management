package com.operon.workforce.availability;

import com.operon.workforce.auth.LoginRequest;
import com.operon.workforce.user.User;
import com.operon.workforce.user.UserNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvailabilityControllerTests {

    // Availability 1 Data
    private static final Instant AVAILABILITY_START_TIME = Instant.parse("2026-01-01T08:00:00Z");
    private static final Instant AVAILABILITY_END_TIME = Instant.parse("2026-01-01T14:00:00Z");
    private static final String AVAILABILITY_NOTE = "Doctor appointment";

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
                AVAILABILITY_START_TIME,
                AVAILABILITY_END_TIME,
                AVAILABILITY_NOTE
        );
        String jsonCreateAvailabilityRequest = objectMapper.writeValueAsString(createAvailabilityRequest);

        RequestBuilder postCreateAvailability = post("/api/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreateAvailabilityRequest)
                .header("Authorization", "Bearer " + userToken);

        mockMvc.perform(postCreateAvailability)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.startTime").value(AVAILABILITY_START_TIME.toString()))
                .andExpect(jsonPath("$.endTime").value(AVAILABILITY_END_TIME.toString()))
                .andExpect(jsonPath("$.note").value(AVAILABILITY_NOTE))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(availabilityRepository.count()).isEqualTo(1);
    }

    @Test
    public void missingTokenCannotCreateAvailability() throws Exception {
        CreateAvailabilityRequest createAvailabilityRequest = new CreateAvailabilityRequest(
                AVAILABILITY_START_TIME,
                AVAILABILITY_END_TIME,
                AVAILABILITY_NOTE
        );

        String jsonCreateAvailabilityRequest = objectMapper.writeValueAsString(createAvailabilityRequest);

        RequestBuilder postCreateAvailability = post("/api/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreateAvailabilityRequest);

        mockMvc.perform(postCreateAvailability)
                .andExpect(status().isUnauthorized());

        assertThat(availabilityRepository.count()).isZero();
    }

    @Test
    public void invalidTimeRangeReturnsBadRequest() throws Exception {
        String userToken = loginAndReturnToken(FIRST_USER_EMAIL, FIRST_USER_PASSWORD);

        CreateAvailabilityRequest createAvailabilityRequest = new CreateAvailabilityRequest(
                AVAILABILITY_END_TIME,
                AVAILABILITY_START_TIME,
                AVAILABILITY_NOTE
        );
        String jsonCreateAvailabilityRequest = objectMapper.writeValueAsString(createAvailabilityRequest);

        RequestBuilder postCreateAvailability = post("/api/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreateAvailabilityRequest)
                .header("Authorization", "Bearer " + userToken);

        mockMvc.perform(postCreateAvailability)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(InvalidAvailabilityTimeRangeException.errorMessage));

        assertThat(availabilityRepository.count()).isZero();
    }

    @Test
    public void approvedEmployeeCanReadOwnAvailabilities() throws Exception {
        String userToken = loginAndReturnToken(FIRST_USER_EMAIL, FIRST_USER_PASSWORD);
        User user = userRepository.findByEmail(FIRST_USER_EMAIL).orElseThrow(UserNotFoundException::new);

        Availability availability = new Availability(
                user,
                AVAILABILITY_START_TIME,
                AVAILABILITY_END_TIME,
                AVAILABILITY_NOTE
        );

        availabilityRepository.save(availability);

        RequestBuilder getAvailabilityRequest = get("/api/availabilities/my")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken);

        mockMvc.perform(getAvailabilityRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].userId").value(user.getId()))
                .andExpect(jsonPath("$[0].startTime").value(AVAILABILITY_START_TIME.toString()))
                .andExpect(jsonPath("$[0].endTime").value(AVAILABILITY_END_TIME.toString()))
                .andExpect(jsonPath("$[0].note").value(AVAILABILITY_NOTE))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
