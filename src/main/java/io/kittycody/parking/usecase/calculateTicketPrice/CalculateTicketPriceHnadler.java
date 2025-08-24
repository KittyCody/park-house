package io.kittycody.parking.usecase.calculateTicketPrice;

import an.awesome.pipelinr.Command;
import io.kittycody.parking.domain.ParkingSettings;
import io.kittycody.parking.domain.Ticket;
import io.kittycody.parking.domain.error.UnknownTicket;
import io.kittycody.parking.shared.error.ParkingSettingsNotFound;
import io.kittycody.parking.shared.result.Result;
import io.kittycody.parking.shared.service.TimeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface CalculateTicketPriceTicketRepo extends CrudRepository<Ticket, UUID> {
    @NotNull Optional<Ticket> findById(@NotNull UUID id);
}

@Repository
interface CalculateTicketPriceSettingsRepo extends CrudRepository<ParkingSettings, Long> {
    Optional<ParkingSettings> findTopByOrderByIdDesc();
}

record CalculateTicketPriceCommand(UUID ticketId) implements Command<Result<Integer>> {
}

@Component
class CalculateTicketPriceHandler implements Command.Handler<CalculateTicketPriceCommand, Result<Integer>> {

    private final CalculateTicketPriceTicketRepo ticketRepo;
    private final CalculateTicketPriceSettingsRepo settingsRepo;
    private final TimeService timeService;

    CalculateTicketPriceHandler(
            CalculateTicketPriceTicketRepo ticketRepo,
            CalculateTicketPriceSettingsRepo settingsRepo,
            TimeService timeService
    ) {
        this.ticketRepo = ticketRepo;
        this.settingsRepo = settingsRepo;
        this.timeService = timeService;
    }

    @Override
    public Result<Integer> handle(CalculateTicketPriceCommand cmd) {

        var ticketOpt = ticketRepo.findById(cmd.ticketId());
        if (ticketOpt.isEmpty()) {
            return Result.failure(new UnknownTicket());
        }

        var settingsOpt = settingsRepo.findTopByOrderByIdDesc();
        if (settingsOpt.isEmpty()) {
            return Result.failure(new ParkingSettingsNotFound());
        }

        var ticket = ticketOpt.get();
        var costPolicy = settingsOpt.get().getCostPolicy();
        var now = timeService.now();

        return costPolicy.calculate(ticket, now);
    }
}
