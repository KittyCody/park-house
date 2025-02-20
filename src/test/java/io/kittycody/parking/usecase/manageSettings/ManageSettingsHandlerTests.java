package io.kittycody.parking.usecase.manageSettings;

import io.kittycody.parking.domain.ParkingSettings;
import io.kittycody.parking.domain.error.InvalidOperationalHours;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class ManageSettingsHandlerTests {

    private ManageSettingsHandler handler;
    private ParkingSettingsRepository settingsRepository;

    @BeforeEach
    void setUp() {
        settingsRepository = Mockito.mock(ParkingSettingsRepository.class);
        when(settingsRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

        handler = new ManageSettingsHandler(settingsRepository);
    }

    @Test
    void updateOperationalHours_WhenOpenHourIsInvalid_returnsInvalidHoursError() {
       final var cmd = new ManageSettingsCommand(-1,23);
       final var err = handler.handle(cmd);

       assertThat(err)
               .as("Result should be error")
               .isExactlyInstanceOf(InvalidOperationalHours.class);

       assertThat(err.getCode())
                .as("Error should contain 'update'")
                .contains("bad_request:invalid_operational_hours");
    }

    @Test
    void updateOperationalHours_WhenOpenHourIsValid_returnsNoError() {
        final var cmd = new ManageSettingsCommand(0,24);
        final var err = handler.handle(cmd);

        final var mockedSettings = new ParkingSettings(0, 24);
        when(settingsRepository.save(any(ParkingSettings.class)))
                .thenReturn(mockedSettings);

        assertThat(err)
                .as("Error should be null")
                .isNull();
    }

    @Test
    void updateOperationalHours_WhenCloseHourIsInvalid_returnsInvalidHoursError() {

        final var cmd = new ManageSettingsCommand(8, -1);

        final var err = handler.handle(cmd);

        assertThat(err.getCode())
                .as("Error should contain 'invalid_operational_hours'")
                .contains("bad_request:invalid_operational_hours");

    }

    @Test
    void updateOperationalHours_WhenCloseHourIsValid_returnsNoError() {
        final var cmd = new ManageSettingsCommand(8,24);
        final var err = handler.handle(cmd);

        final var mockedSettings = new ParkingSettings(8, 24);
        when(settingsRepository.save(any(ParkingSettings.class)))
        .thenReturn(mockedSettings);

        assertThat(err)
        .as("Error should be null")
                .isNull();
    }

}