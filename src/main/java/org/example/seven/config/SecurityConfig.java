package org.example.seven.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 암호화
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 시큐리티 필터를 통제할 수 있는 @Bean 등록
     *
     * 보안 관리, 로그인, 로그아웃, 세션 관리, 인가 등 담당한다.
     * JWT를 활용한 stateless 방식으로 구현한다.
     *
     * @param http
     * @return
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        // CSRF 필터 disable
        // CSRF: 로그인된 사용자의 권한을 도용해, 공격자가 의도한 요청을 서버로 전송하게 만드는 기법
        // 이유: 세션 쿠키 방식을 사용하지 않기 때문에 사용 안 함
        http
                .csrf(csrf -> csrf.disable());

        // 경로별 인가
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                );

        // 세션 설정 STATELESS
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

}
