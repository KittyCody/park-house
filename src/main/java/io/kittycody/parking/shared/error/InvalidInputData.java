package io.kittycody.parking.shared.error;

import java.util.Map;

public abstract class InvalidInputData extends AppError {
    @SafeVarargs
    public InvalidInputData (String operationName, String reason, Map.Entry<String, Object>... args) {
        super("%s:%s".formatted(operationName, reason), args);
    }
}
