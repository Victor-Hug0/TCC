package com.victor.TCC.dto.request;

import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(
        @NotNull(message = "nome é obrigatório") String name,
        @NotNull(message = "email é obrigatório") String email,
        @NotNull(message = "senha é obrigatória") String password
) {
}
