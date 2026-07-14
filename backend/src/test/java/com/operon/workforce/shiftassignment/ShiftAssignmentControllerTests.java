package com.operon.workforce.shiftassignment;

import com.operon.workforce.auth.LoginRequest;
import com.operon.workforce.availability.AvailabilityRepository;
import com.operon.workforce.shift.Shift;
import com.operon.workforce.shift.ShiftRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShiftAssignmentControllerTests {

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

    // Admin Data
    private static final String ADMIN_FIRST_NAME = "Administrator";
    private static final String ADMIN_LAST_NAME = "User";
    private static final String ADMIN_EMAIL = "admin@operon.local";
    private static final String ADMIN_PASSWORD = "admin12345";

    // Shift 1 Data
    private static final Instant FIRST_SHIFT_START_TIME = Instant.parse("2026-01-01T08:00:00Z");
    private static final Instant FIRST_SHIFT_END_TIME = Instant.parse("2026-01-01T14:00:00Z");
    private static final String FIRST_SHIFT_ROLE = "Service";
    private static final Integer FIRST_SHIFT_REQUIRED_EMPLOYEES = 3;
    private static final String FIRST_SHIFT_LOCATION = "Main branch";
    private static final String FIRST_SHIFT_NOTE = "Saturday morning service shift";

    // Shift 2 Data
    private static final Instant SECOND_SHIFT_START_TIME = Instant.parse("2026-01-02T10:00:00Z");
    private static final Instant SECOND_SHIFT_END_TIME = Instant.parse("2026-01-02T18:00:00Z");
    private static final String SECOND_SHIFT_ROLE = "Kitchen";
    private static final Integer SECOND_SHIFT_REQUIRED_EMPLOYEES = 2;
    private static final String SECOND_SHIFT_LOCATION = "Main branch";
    private static final String SECOND_SHIFT_NOTE = "Lunch and afternoon kitchen shift";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShiftAssignmentRepository shiftAssignmentRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void prepareDatabase() {
        shiftAssignmentRepository.deleteAll();
        availabilityRepository.deleteAll();
        shiftRepository.deleteAll();
        userRepository.deleteAll();
        createUser(ADMIN_FIRST_NAME, ADMIN_LAST_NAME, ADMIN_EMAIL, ADMIN_PASSWORD, UserRole.ADMIN);
        createUser(FIRST_USER_FIRST_NAME, FIRST_USER_LAST_NAME, FIRST_USER_EMAIL, FIRST_USER_PASSWORD,
                UserRole.EMPLOYEE);
        createUser(SECOND_USER_FIRST_NAME, SECOND_USER_LAST_NAME, SECOND_USER_EMAIL, SECOND_USER_PASSWORD,
                UserRole.EMPLOYEE);
    }

    private void createUser(String firstName, String lastName, String email, String password, UserRole role) {
        String userPasswordHash = passwordEncoder.encode(password);

        User user = new User(firstName, lastName, email, userPasswordHash, role);
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
    public void adminCanAssignApprovedUserToShift() throws Exception {
        String adminToken = loginAndReturnToken(ADMIN_EMAIL, ADMIN_PASSWORD);
        User user = userRepository.findByEmail(FIRST_USER_EMAIL).orElseThrow(UserNotFoundException::new);

        Shift shift = new Shift(
                FIRST_SHIFT_START_TIME,
                FIRST_SHIFT_END_TIME,
                FIRST_SHIFT_ROLE,
                FIRST_SHIFT_REQUIRED_EMPLOYEES,
                FIRST_SHIFT_LOCATION,
                FIRST_SHIFT_NOTE
        );
        shiftRepository.save(shift);

        CreateShiftAssignmentRequest shiftAssignment = new CreateShiftAssignmentRequest(
                user.getId()
        );

        RequestBuilder shiftAssignmentRequest = post("/api/shifts/" + shift.getId() + "/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shiftAssignment))
                .header("Authorization", "Bearer " + adminToken);

        mockMvc.perform(shiftAssignmentRequest)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.shiftId").value(shift.getId()))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(status().isCreated());

        assertThat(shiftAssignmentRepository.count()).isEqualTo(1);
    }
}
