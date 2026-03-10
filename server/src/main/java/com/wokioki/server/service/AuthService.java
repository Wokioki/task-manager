package com.wokioki.server.service;

import com.wokioki.server.dto.user.UserAuthResponse;
import com.wokioki.server.dto.user.UserLoginRequest;
import com.wokioki.server.dto.user.UserRegisterRequest;
import com.wokioki.server.model.User;
import com.wokioki.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserAuthResponse register(UserRegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        String username = req.username().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(req.password()))
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getEmail());

        return new UserAuthResponse(saved.getId(), saved.getUsername(), saved.getEmail(), token);
    }

    public UserAuthResponse login(UserLoginRequest req) {
        String email = req.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        boolean ok = passwordEncoder.matches(req.password(), user.getPassword());
        if (!ok) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new UserAuthResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}