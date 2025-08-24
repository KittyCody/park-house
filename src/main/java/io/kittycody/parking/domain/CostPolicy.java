package io.kittycody.parking.domain;

import io.kittycody.parking.shared.result.Result;

import java.time.Duration;
import java.time.LocalDateTime;

public class CostPolicy {

    private final int costPerHour;
    private final int gratisMinutes;

    public CostPolicy(int costPerHour, int gratisMinutes) {
        this.costPerHour = costPerHour;
        this.gratisMinutes = gratisMinutes;
    }

    protected CostPolicy() {
        this.costPerHour = 0;
        this.gratisMinutes = 0;
    }

    public static CostPolicy createDefault() {
        return new CostPolicy(300, 5);
    }

    public Result<Integer> calculate(Ticket ticket, LocalDateTime now) {
        var minutesOfStay = Duration.between(ticket.getTimeOfEntry(), now)
                .toMinutes() - gratisMinutes;

        if (minutesOfStay <= 0) {
            return Result.success(0);
        }

        final var hours = (int) Math.ceil(minutesOfStay / 60.0);

        return Result.success(hours * costPerHour);
    }

}
