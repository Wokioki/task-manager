package com.wokioki.server.dto.user;

public record UserAuthResponse (
        Long id,
        String username,
        String email,
        String token
){}
