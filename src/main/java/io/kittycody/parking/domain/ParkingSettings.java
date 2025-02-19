package io.kittycody.parking.domain;

import io.kittycody.parking.domain.error.InvalidOperationalHours;
import io.kittycody.parking.shared.error.AppError;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "parking_settings")
public class ParkingSettings {

    private static final int MinOperationalHours = 1;

    @Id
    private int id;

    private int openHour;
    private int closeHour;

    protected ParkingSettings() {}
    
    public ParkingSettings(int openHour, int closeHour) {
        this.openHour = openHour;
        this.closeHour = closeHour;
    }

    @Nullable
    public AppError updateOperationalHours(int openHour, int closeHour) {
        if (!isValidOperationalHours(openHour, closeHour)) {
            return new InvalidOperationalHours();
        }

        this.openHour = openHour;
        this.closeHour = closeHour;

        return null;
    }

    public static boolean isValidOperationalHours(int openHour, int closeHour) {
        return is24HourFormat(openHour)
                && is24HourFormat(closeHour)
                && closeHour > openHour
                && closeHour - openHour > MinOperationalHours;
    }

    private static boolean is24HourFormat(int hour) {
        return hour >= 0 && hour <= 24;
    }

    public static ParkingSettings createDefault() {
        return new ParkingSettings(8, 22);
    }

    public int openHour() {
        return openHour;
    }

    public int closeHour() {
        return closeHour;
    }



}
