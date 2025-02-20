package io.kittycody.parking.usecase.manageSettings;

import an.awesome.pipelinr.Pipeline;
import io.kittycody.parking.domain.ParkingSettings;
import io.kittycody.parking.domain.error.InvalidOperationalHours;
import io.kittycody.parking.shared.auth.HasAuthority;
import io.kittycody.parking.shared.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageSettingsController extends BaseController {

    private record ManageSettingsRequest(int openHour, int closeHour) {};

    private final Pipeline pipeline;

    ManageSettingsController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PutMapping("/v1/settings/current")
    @PreAuthorize(HasAuthority.ADMIN_ROLE)
    ResponseEntity<Void> updateParkingSettings(@RequestBody ManageSettingsRequest req) {
        if (!ParkingSettings.isValidOperationalHours(req.openHour, req.closeHour)) {
            throw new InvalidOperationalHours();
        }

        final var command = new ManageSettingsCommand(req.openHour, req.closeHour);

        final var err = this.pipeline.send(command);

        return this.toNoContentOrThrow(err);
    }
}
