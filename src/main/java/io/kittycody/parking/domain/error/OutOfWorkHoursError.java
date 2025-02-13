package io.kittycody.parking.domain.error;

import io.kittycody.parking.shared.error.InvalidOperation;

import java.util.Map;

public class OutOfWorkHoursError extends InvalidOperation {

    @SafeVarargs
    public OutOfWorkHoursError(Map.Entry<String, Object>... args) {
        super("entry", "working_hours", args);
    }
}
