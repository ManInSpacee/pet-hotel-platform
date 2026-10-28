package ru.mirea.pethotel.desktop.api;

import java.util.Map;

public class ApiException extends RuntimeException {

    private final int status;
    private final Map<String, String> fields;

    public ApiException(int status, String message) {
        this(status, message, Map.of());
    }

    public ApiException(int status, String message, Map<String, String> fields) {
        super(message);
        this.status = status;
        this.fields = fields;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}
