package io.kittycody.parking.usecase.createEntry;

import an.awesome.pipelinr.Command;
import io.kittycody.parking.domain.Floor;
import io.kittycody.parking.domain.ParkingSettings;
import io.kittycody.parking.domain.Ticket;
import io.kittycody.parking.domain.error.InvalidOperationalHours;
import io.kittycody.parking.domain.error.NotEnoughSpaces;
import io.kittycody.parking.shared.error.AppError;
import io.kittycody.parking.shared.result.Result;
import io.kittycody.parking.shared.timeService.TimeService;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

record CreateEntryCommand(UUID gateId) implements Command<Result<EntryViewModel>> {}

record EntryViewModel(String id, LocalDateTime entryTime) {}

@Component
class CreateEntryHandler implements Command.Handler<CreateEntryCommand, Result<EntryViewModel>> {

    private final CreateEntryTicketRepo tickets;
    private final CreateEntryFloorRepo floors;
    private final TimeService timeService;
    private final CreateEntryParkingSettingsRepo parkingSettings;

    CreateEntryHandler(CreateEntryTicketRepo tickets, CreateEntryFloorRepo floors, TimeService timeService, CreateEntryParkingSettingsRepo parkingSettings) {
        this.tickets = tickets;
        this.floors = floors;
        this.timeService = timeService;
        this.parkingSettings = parkingSettings;
    }

    @Override
    public Result<EntryViewModel> handle(CreateEntryCommand cmd) {

        AppError validateErr = this.validateEntry();
        if (validateErr != null) {
            return Result.failure(validateErr);
        }
        final var parkingTicket = new Ticket(cmd.gateId(), timeService.now());
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

    @Nullable
    private AppError validateOperationalHours() {
        Optional<ParkingSettings> settingsOpt = this.parkingSettings.findTopByOrderByIdDesc();
        if (settingsOpt.isEmpty()) {
            return null;
        }

        ParkingSettings settings = settingsOpt.get();
        LocalDateTime now = timeService.now();
        int currentHour = now.getHour();

        if (currentHour < settings.openHour() || currentHour >= settings.closeHour()) {
            return new InvalidOperationalHours();
        }

        return null;
    }

    @Nullable
    private AppError validateEntry() {
        AppError operationalHoursErr = validateOperationalHours();
        if (operationalHoursErr != null) {
            return operationalHoursErr;
        }

        AppError capacityErr = validateCapacity();
        if (capacityErr != null) {
            return capacityErr;
        }

        return null;
    }

}
    @Repository
    interface CreateEntryTicketRepo extends CrudRepository<Ticket, UUID> {
        int countAllByTimeOfExitIsNull();
    }

    @Repository
    interface CreateEntryFloorRepo extends CrudRepository<Floor, Integer> {
        @Query(value = "SELECT SUM(f.capacity) FROM Floor f")
        Optional<Long> sumCapacity();
    }

    @Repository
    interface CreateEntryParkingSettingsRepo extends CrudRepository<ParkingSettings, Long> {
        Optional<ParkingSettings> findTopByOrderByIdDesc();
    }
