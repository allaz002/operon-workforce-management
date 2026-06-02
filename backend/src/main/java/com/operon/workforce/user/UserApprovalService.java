package com.operon.workforce.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserApprovalService {
    private final UserRepository userRepository;

    public UserApprovalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse approveUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.approve();
        User savedUser = this.userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(),
                savedUser.getEmail(), savedUser.getRole(), savedUser.getApprovalStatus(), savedUser.getCreatedAt());

    }

}
