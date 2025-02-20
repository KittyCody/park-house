package io.kittycody.parking.domain.error;

import java.util.Map;
import io.kittycody.parking.shared.error.InvalidInputData;

public class InvalidOperationalHours extends InvalidInputData {
    @SafeVarargs
    public InvalidOperationalHours(Map.Entry<String, Object>... args) {
        super("parking_settings", "invalid_operational_hours", args);
    }
}

