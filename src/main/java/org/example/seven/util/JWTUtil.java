package org.example.seven.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @Component 방식 vs Static 메서드 방식
 *
 * ⚬ @Component (Spring Bean 등록 방식)
 * ⚬ 장점: application.yml 등에 등록해둔 JWT 시크릿 키나 만료 시간을 @Value를 통해 필드에 주입 가능.
 *   다른 서비스나 필터에서 이 클래스를 모킹(Mocking)하여 단위 테스트를 작성하기 쉬움.
 *
 * ⚬ 단점: 사용할 때마다 의존성 주입(DI)을 받아야 함.
 *   토큰이 필요한 모든 컨트롤러, 서비스, 필터의 생성자에 JwtUtil을 선언해 주어야 함 -> lombok 사용
 *
 * ⚬ Static 메소드 (유틸리티 클래스 방식)
 * ⚬ 장점: 전역적으로 접근 가능하므로 의존성 주입 과정 없이 JwtUtil.generateToken(...) 형태로
 *   어디서든 즉시 호출할 수 있어 코드가 간결함.
 *
 * ⚬ 단점: 정적(static) 필드에는 Spring의 @Value로 환경변수를 바로 주입하기 어려움.
 *   토큰을 생성/검증할 때마다 서비스 계층에서 시크릿 키를 매개변수로 계속 넘겨주어야 함.
 *   객체 지향적인 단위 테스트(모킹)를 하기가 어려움.
 */
@Component
public class JWTUtil {

    private final SecretKey key;
    private final long accessTokenExpireTime;

    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expire-time}") long  accessTokenExpireTime
    ) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.accessTokenExpireTime = accessTokenExpireTime;
    }

    /**
     * ## Jwts.builder() 요소
     *
     * .subject(username): 토큰의 주제(sub)를 설정. 누구의 토큰인지 식별하는 유저 ID나 이메일이 주로 들어감.
     * .claim("key", "value"): 커스텀 클레임입니다. 개발자가 자유롭게 추가하는 추가 정보(Payload)로, 사용자의 권한(role)과 토큰의 용도(tokenType)를 담음.
     * .issuedAt(now): 토큰 발급 시간(iat)을 기록.
     * .expiration(expireDate): 토큰 만료 시간(exp)을 기록. 이 시간이 지나면 서버는 토큰을 거부.
     * .signWith(key): 서버가 가진 시크릿 키로 암호화 서명을 남김. 누군가 내용을 조작하면 서명이 깨져서 서버가 바로 알아차릴 수 있음.
     * .compact(): 설정한 모든 정보(Header, Payload, Signature)를 조립하여 실제 통신에 사용하는 하나의 긴 문자열 형태로 압축해 반환.
     *
     * @param username
     * @param role
     * @return Access Token
     */
    public String createAccessToken(String username, String role) {

        Date now = new Date();
        Date expireDate = new Date(now.getTime() * accessTokenExpireTime);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("tokenType", "ACCESS")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * ## Jwts.parser(): 토큰을 읽고 해석하기 위한 파서(해독기)를 생성.
     *
     * .verifyWith(key): 서버가 가진 비밀키를 설정하여, 토큰의 서명이 위조되지 않았는지 검증.
     * .build(): 설정한 정보들을 바탕으로 파서 객체를 완성.
     * .parseSignedClaims(token): 전달받은 토큰 문자열을 실제로 해독하고 유효성(서명, 만료일 등)을 확인.
     * .getPayload(): 검증이 무사히 완료된 토큰 내부에서 필요한 실제 데이터(Claims)만 추출하여 반환.
     *
     * @param token
     * @return Claims
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
