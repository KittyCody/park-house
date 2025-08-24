package io.kittycody.parking.domain;

import io.kittycody.parking.domain.error.InvalidOperationalHours;
import io.kittycody.parking.shared.error.AppError;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "parking_settings")
public class ParkingSettings {

    private static final int MinOperationalHours = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private int openHour;
    private int closeHour;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private CostPolicy costPolicy;

    protected ParkingSettings() {
    }

    public ParkingSettings(int openHour, int closeHour, CostPolicy costPolicy) {

        if (!isValidOperationalHours(openHour, closeHour)) {
            throw new InvalidOperationalHours();
        }

        this.openHour = openHour;
        this.closeHour = closeHour;
        this.costPolicy = costPolicy;
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
        return new ParkingSettings(8, 22, CostPolicy.createDefault());
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

    public int openHour() {
        return openHour;
    }

    public int closeHour() {
        return closeHour;
    }

    public CostPolicy getCostPolicy() {
        return costPolicy;
    }
}
