package io.kittycody.parking.domain.error;

import io.kittycody.parking.shared.error.AppError;

import java.util.Map;

public abstract class BadRequest extends AppError {
    @SafeVarargs
    public BadRequest(String reason, String invalidHours, Map.Entry<String, Object>... args) {
        super("bad_request:%s".formatted(reason), args);
    }
}
