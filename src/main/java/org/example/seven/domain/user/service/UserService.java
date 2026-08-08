package org.example.seven.domain.user.service;

import org.example.seven.domain.user.dto.ProfileResponse;
import org.example.seven.domain.user.dto.UserRequest;
import org.example.seven.domain.user.entity.UserEntity;
import org.example.seven.domain.user.entity.UserRole;
import org.example.seven.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 의존성 주입이 없었다면?
     *
     * public UserService() {
     *         // 주의: UserRepository는 보통 인터페이스라 직접 구현체를 만들거나 매핑해야 해서 실무에선 불가능
     *         this.userRepository = new MemoryUserRepository(); // 예시를 위한 임의의 구현체
     *
     *         // PasswordEncoder는 BCrypt 구현체를 직접 생성할 수 있음
     *         this.passwordEncoder = new BCryptPasswordEncoder();
     *     }
     */

    public String signUp(UserRequest request) {
        String username = request.username();
        String password = request.password();

        if(userRepository.existsByName(username)) {
            return "Failed";
        }

        UserEntity user = new UserEntity();

        user.setName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.USER);

        userRepository.save(user);

        return "Success";
    }

    public ProfileResponse getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        return ProfileResponse.from(user);
    }
}
