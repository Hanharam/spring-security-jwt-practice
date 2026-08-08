package org.example.seven.domain.user.dto;

import org.example.seven.domain.user.entity.UserEntity;
import org.example.seven.domain.user.entity.UserRole;

public record ProfileResponse(
        String name,
        UserRole role
) {
    // static을 붙여서 객체를 만들지 않고 "클래스명.메서드명()" 이런 형태로 호출
    public static ProfileResponse from(UserEntity user) {
        return new ProfileResponse(
                user.getName(),
                user.getRole()
        );
    }
}
