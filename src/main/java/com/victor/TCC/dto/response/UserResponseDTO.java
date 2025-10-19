package com.victor.TCC.dto.response;

import com.victor.TCC.entity.User;

public record UserResponseDTO(
        Long id,
        String name,
        String email
) {

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(), user.getName(), user.getEmail()
        );
    }
}
