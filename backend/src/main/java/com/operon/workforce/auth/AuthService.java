package com.operon.workforce.auth;

import com.operon.workforce.user.User;
import com.operon.workforce.user.UserApprovalStatus;
import com.operon.workforce.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(InvalidLoginException::new);

        if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new InvalidLoginException();
        }

        if (user.getApprovalStatus() != UserApprovalStatus.APPROVED) {
            throw new UserNotApprovedException();
        }

        String token = jwtTokenService.generateToken(user);

        return new LoginResponse(token, user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getRole(),
                user.getApprovalStatus());
    }
}
