package io.kittycody.parking.usecase.issueEntry;

import an.awesome.pipelinr.Command;
import io.kittycody.parking.domain.Floor;
import io.kittycody.parking.domain.Ticket;
import io.kittycody.parking.domain.error.NotEnoughSpaces;
import io.kittycody.parking.shared.error.AppError;
import io.kittycody.parking.shared.result.Result;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

record IssueEntryCommand(UUID gateId) implements Command<Result<EntryViewModel>> {}

record EntryViewModel(String id, LocalDateTime entryTime) {}

@Component
class IssueEntryHandler implements Command.Handler<IssueEntryCommand, Result<EntryViewModel>> {

    private final IssueEntryTicketRepo tickets;
    private final IssueEntryFloorRepo floors;

    IssueEntryHandler(IssueEntryTicketRepo tickets, IssueEntryFloorRepo floors) {
        this.tickets = tickets;
        this.floors = floors;
    }

    @Override
    public Result<EntryViewModel> handle(IssueEntryCommand cmd) {
        final var err = this.validateCapacity();
        if (err != null) {
            return Result.failure(err);
        }

        final var parkingTicket = new Ticket(cmd.gateId(), LocalDateTime.now());
        final var persistedTicket = this.tickets.save(parkingTicket);

        final var result = new EntryViewModel(
                persistedTicket.getId().toString(),
                persistedTicket.getTimeOfEntry()
        );

        return Result.success(result);
    }

    @Nullable
    private AppError validateCapacity() {
        final var vehicles = tickets.countAllByTimeOfExitIsNull();
        final var availableSpaces = this.floors.sumCapacity();

        if (availableSpaces.isEmpty() || vehicles >= availableSpaces.get()) {
            return new NotEnoughSpaces();
        }

        return null;
    }
}

@Repository
interface IssueEntryTicketRepo extends CrudRepository<Ticket, UUID> {
    int countAllByTimeOfExitIsNull();
}

@Repository
interface IssueEntryFloorRepo extends CrudRepository<Floor, Integer> {
    @Query(value = "SELECT SUM(f.capacity) FROM Floor f")
    Optional<Long> sumCapacity();
}
