package io.kittycody.parking.usecase.issueEntry;

import io.kittycody.parking.domain.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class IssueEntryHandlerTests {

    private IssueEntryTicketRepo ticketsMock;
    private IssueEntryFloorRepo floorsMock;

    private IssueEntryHandler handler;

    @BeforeEach
    void setUp() {
        ticketsMock = Mockito.mock(IssueEntryTicketRepo.class);
        floorsMock = Mockito.mock(IssueEntryFloorRepo.class);

        handler = new IssueEntryHandler(ticketsMock, floorsMock);
    }

    @Test
    void whenNoAvailableSpaces_ReturnsNoSpacesError() {
        // Arrange
        when(floorsMock.sumCapacity()).thenReturn(10);
        when(ticketsMock.countAllByTimeOfExitIsNull()).thenReturn(10);

        final var gateId = UUID.randomUUID();
        final var cmd = new IssueEntryCommand(gateId);

        // Act
        final var result = handler.handle(cmd);

        // Assert
        assertThat(result.isFailure())
                .as("Result should be failure")
                .isTrue();

        assertThat(result.errorOrThrow().getCode())
                .as("Error should contain 'capacity'")
                .contains("capacity");
    }

    @Test
    void whenOneAvailableSpace_ReturnsEntry() {
        // Arrange
        when(floorsMock.sumCapacity()).thenReturn(10);
        when(ticketsMock.countAllByTimeOfExitIsNull()).thenReturn(9);

        final var gateId = UUID.randomUUID();
        final var now = LocalDateTime.now();
        final var persistedTicket = new Ticket(gateId, now);

        when(ticketsMock.save(any(Ticket.class))).thenReturn(persistedTicket);

        final var cmd = new IssueEntryCommand(gateId);

        // Act
        final var result = handler.handle(cmd);

        // Assert
        assertThat(result.isSuccess())
                .as("Result should be success")
                .isTrue();

        assertThat(result.valueOrThrow().entryTime())
                .as("Entry time should be the one provided")
                .isEqualToIgnoringNanos(now);

        assertThat(result.valueOrThrow().id())
                .as("Entry id should be the one provided")
                .isEqualTo(persistedTicket.getId().toString());
    }
}
