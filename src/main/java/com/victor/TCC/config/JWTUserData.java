package com.victor.TCC.config;

import lombok.Builder;

import java.util.List;

@Builder
public record JWTUserData(Long userId, String email, List<String> roles) {
}
