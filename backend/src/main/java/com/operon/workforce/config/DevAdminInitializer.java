package com.operon.workforce.config;

import com.operon.workforce.user.User;
import com.operon.workforce.user.UserRepository;
import com.operon.workforce.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DevAdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String[] args) {
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        String passwordHash = passwordEncoder.encode("admin12345");

        User user = new User("Administrator", "User", "admin@operon.local", passwordHash, UserRole.ADMIN);
        user.approve();
        userRepository.save(user);
    }
}
