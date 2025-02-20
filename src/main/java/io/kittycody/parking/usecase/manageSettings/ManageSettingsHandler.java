package io.kittycody.parking.usecase.manageSettings;

import an.awesome.pipelinr.Command;
import io.kittycody.parking.domain.ParkingSettings;
import io.kittycody.parking.shared.error.AppError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.util.Optional;

record ManageSettingsCommand(int OpenHour, int CloseHour) implements Command<AppError> {}

@Component
class ManageSettingsHandler implements Command.Handler<ManageSettingsCommand, AppError> {

    private final ParkingSettingsRepository settingsRepository;

    ManageSettingsHandler(ParkingSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public AppError handle(ManageSettingsCommand cmd) {
        var settings = settingsRepository.findTopByOrderByIdDesc()
                .orElseGet(ParkingSettings::createDefault);

        final var err = settings.updateOperationalHours(cmd.OpenHour(), cmd.CloseHour());
        if (err != null) {
            return err;
        }

        settingsRepository.save(settings);
        return null;
    }
}

@Repository
interface ParkingSettingsRepository extends CrudRepository<ParkingSettings, Long> {
    Optional<ParkingSettings> findTopByOrderByIdDesc();
}

