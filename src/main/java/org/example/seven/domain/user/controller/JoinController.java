package org.example.seven.domain.user.controller;

import org.example.seven.domain.user.dto.ProfileResponse;
import org.example.seven.domain.user.dto.UserRequest;
import org.example.seven.domain.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class JoinController {

    private final UserService userService;

    public JoinController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public String join(UserRequest request) {
        return userService.signUp(request);
    }

    @GetMapping("/myProfile")
    public ProfileResponse getMyProfile(Long userId) {
        return userService.getProfile(userId);
    }
}
