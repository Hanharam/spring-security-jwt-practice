package org.example.seven.domain.user.service;

import org.example.seven.domain.user.dto.ProfileResponse;
import org.example.seven.domain.user.dto.UserRequest;
import org.example.seven.domain.user.entity.UserEntity;
import org.example.seven.domain.user.entity.UserRole;
import org.example.seven.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

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

    /**
     * AuthenticatonProvider 가 이 메서드를 호출함
     *
     * 클라이언트가 입력한 username을 기반으로 DB에서 사용자 정보를 조회
     * 조회된 데이터를 Spring Security 규격인 UserDetails 객체로 변환하여 반환
     * (이후 Security의 AuthenticationProvider가 이 객체의 정보와 입력된 비밀번호를 비교하여 인증을 수행)
     *
     * @param username 로그인을 시도하는 사용자 아이디
     * @return Spring Security가 이해할 수 있는 사용자 정보 (UserDetails)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));

        return User.builder()
                .username(user.getName())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
