package io.kittycody.parking.domain.error;

import java.util.Map;
import io.kittycody.parking.shared.error.BadRequest;

public class InvalidOperationalHours extends BadRequest {
    @SafeVarargs
    public InvalidOperationalHours(Map.Entry<String, Object>... args) {
        super("bad_request", "invalid_operational_hours", args);
    }
}

