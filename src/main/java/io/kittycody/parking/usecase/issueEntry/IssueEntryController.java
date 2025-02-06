package io.kittycody.parking.usecase.issueEntry;

import an.awesome.pipelinr.Pipeline;
import io.kittycody.parking.shared.auth.HasAuthority;
import io.kittycody.parking.shared.controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class IssueEntryController extends BaseController {

    private final Pipeline pipeline;

    IssueEntryController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping("/v1/entries")
    @PreAuthorize(HasAuthority.GATE_MACHINE)
    ResponseEntity<EntryViewModel> createEntryTicket(@AuthenticationPrincipal Jwt token) {
        final var gateMachineId = UUID.fromString(token.getSubject());

        final var cmd = new IssueEntryCommand(gateMachineId);
        final var result = this.pipeline.send(cmd);

        return this.toResponse(HttpStatus.OK, result);
    }
}