package io.kittycody.parking.usecase.exitParking;

import an.awesome.pipelinr.Command;
import io.kittycody.parking.domain.Ticket;
import io.kittycody.parking.domain.error.UnknownTicket;
import io.kittycody.parking.shared.error.AppError;
import io.kittycody.parking.shared.service.TimeService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

record ExitParkingCommand(UUID ticketId, UUID exitGateId) implements Command<AppError> {
}

@Component
class ExitParkingHandler implements Command.Handler<ExitParkingCommand, AppError> {

    private final ExitParkingTicketRepo tickets;
    private final TimeService timeService;

    ExitParkingHandler(ExitParkingTicketRepo tickets, TimeService timeService) {
        this.tickets = tickets;
        this.timeService = timeService;
    }

    @Override
    public AppError handle(ExitParkingCommand cmd) {

        final var ticket = tickets.findById(cmd.ticketId())
                .orElse(null);

        if (ticket == null) {
            return new UnknownTicket();
        }

        final var err = ticket.exit(cmd.exitGateId(), timeService.now());
        if (err != null) {
            return err;
        }

        tickets.save(ticket);

        return null;
    }
}

@Repository
interface ExitParkingTicketRepo extends CrudRepository<Ticket, UUID> {
}