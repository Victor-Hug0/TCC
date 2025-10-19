package com.victor.TCC.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        Integer status,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
