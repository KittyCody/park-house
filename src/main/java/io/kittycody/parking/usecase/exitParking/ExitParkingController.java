package io.kittycody.parking.usecase.exitParking;

import an.awesome.pipelinr.Pipeline;
import io.kittycody.parking.shared.auth.HasAuthority;
import io.kittycody.parking.shared.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ExitParkingController extends BaseController {

    private record ExitParkingRequest(UUID ticketId) {
    }

    private final Pipeline pipeline;

    public ExitParkingController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping("/v1/exits")
    @PreAuthorize(HasAuthority.GATE_MACHINE_ROLE)
    ResponseEntity<Void> exitParking(
            @AuthenticationPrincipal Jwt token,
            @RequestBody ExitParkingRequest req) {

        final var exitGateId = UUID.fromString(token.getSubject());
        final var cmd = new ExitParkingCommand(req.ticketId, exitGateId);

        final var err = this.pipeline.send(cmd);

        return this.toNoContentOrThrow(err);
    }

}
