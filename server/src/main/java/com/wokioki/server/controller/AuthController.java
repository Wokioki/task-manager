package com.wokioki.server.controller;

import com.wokioki.server.dto.user.UserAuthResponse;
import com.wokioki.server.dto.user.UserLoginRequest;
import com.wokioki.server.dto.user.UserRegisterRequest;
import com.wokioki.server.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserAuthResponse> register(@Valid @RequestBody UserRegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> login(@Valid @RequestBody UserLoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}