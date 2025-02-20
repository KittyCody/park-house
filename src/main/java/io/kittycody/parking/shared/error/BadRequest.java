package io.kittycody.parking.shared.error;

import java.util.Map;

public abstract class BadRequest extends AppError {
    @SafeVarargs
    public BadRequest(String operationName, String reason, Map.Entry<String, Object>... args) {
        super("%s:%s".formatted(operationName, reason), args);
    }
}
