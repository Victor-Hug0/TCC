package com.victor.TCC.dto.response;

import com.victor.TCC.entity.UserRole;

import java.util.List;

public record RegisterUserResponse(
        Long id,
        String name,
        String email,
        List<UserRole> roles
) {
}
