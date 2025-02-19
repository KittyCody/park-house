package io.kittycody.parking.domain.error;

import io.kittycody.parking.shared.error.InvalidOperation;

import java.util.Map;

public class InvalidOperationalHours extends InvalidOperation {
    @SafeVarargs
    public InvalidOperationalHours(Map.Entry<String, Object>... args) {
        super("settings_update", "invalid_hours", args);
    }
}
