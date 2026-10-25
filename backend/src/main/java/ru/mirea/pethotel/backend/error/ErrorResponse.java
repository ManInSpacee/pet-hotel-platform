package ru.mirea.pethotel.backend.error;

import java.util.Map;

// Единый формат ошибки для всех эндпоинтов; fields заполняется только при ошибках валидации
public record ErrorResponse(int status, String message, Map<String, String> fields) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, Map.of());
    }
}
