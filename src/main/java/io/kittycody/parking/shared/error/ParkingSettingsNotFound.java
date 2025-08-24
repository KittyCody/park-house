package io.kittycody.parking.shared.error;


import java.util.Map;

public class ParkingSettingsNotFound extends AppError {

    @SafeVarargs
    public ParkingSettingsNotFound(Map.Entry<String, Object>... args) {
        super("parking_settings:not_found", args);
    }
}
