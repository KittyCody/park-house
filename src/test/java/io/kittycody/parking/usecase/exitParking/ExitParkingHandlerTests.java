package io.kittycody.parking.usecase.exitParking;
import io.kittycody.parking.domain.Ticket;
import io.kittycody.parking.domain.error.AlreadyExited;
import io.kittycody.parking.domain.error.PendingPayment;
import io.kittycody.parking.domain.error.UnknownTicket;
import io.kittycody.parking.shared.error.AppError;
import io.kittycody.parking.shared.timeService.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExitParkingHandlerTests {

    private ExitParkingTicketRepo tickets;
    private ExitParkingHandler handler;
    private TimeService timeService;

    @BeforeEach
    void setUp() {
        tickets = mock(ExitParkingTicketRepo.class);
        timeService = mock(TimeService.class);
        handler = new ExitParkingHandler(tickets, timeService);
    }

    @Test
    void exitParking_whenTicketIsNotFound_returnsEntityNotPresent() {
        UUID ticketId = UUID.randomUUID();
        UUID gateId = UUID.randomUUID();

        when(tickets.findById(ticketId)).thenReturn(Optional.empty());

        ExitParkingCommand command = new ExitParkingCommand(ticketId, gateId);
        AppError error = handler.handle(command);

        assertInstanceOf(UnknownTicket.class, error);
    }

    @Test
    void exitParking_whenPaymentIsPending_returnsPendingPayment() {
        UUID ticketId = UUID.randomUUID();
        UUID gateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2025, 4, 8, 12,0);

        Ticket ticket = new Ticket(ticketId, now);

        when(tickets.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(timeService.now()).thenReturn(now.plusMinutes(30));

        ExitParkingCommand command = new ExitParkingCommand(ticketId, gateId);
        AppError error = handler.handle(command);

        assertInstanceOf(PendingPayment.class, error);

    }

    @Test
    void exitParking_whenAlreadyExited_returnsAlreadyExited() {
        UUID ticketId = UUID.randomUUID();
        UUID gateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2025, 4, 8, 12,0);

        Ticket ticket = new Ticket(UUID.randomUUID(), now);

        ticket.pay(now.plusMinutes(10));
        AppError firstExitError = ticket.exit(UUID.randomUUID(), now.plusMinutes(15));
        assertNull(firstExitError);

        when(tickets.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(timeService.now()).thenReturn(now.plusMinutes(30));

        ExitParkingCommand command = new ExitParkingCommand(ticketId, gateId);
        AppError error = handler.handle(command);

        assertInstanceOf(AlreadyExited.class, error);
    }

}
