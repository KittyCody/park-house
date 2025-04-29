package io.kittycody.parking.usecase.exitParking;

import io.kittycody.parking.AuthUtil;
import io.kittycody.parking.domain.Ticket;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExitParkingTestsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExitParkingTicketRepo ticketsRepo;

    @Test
    void whenNoAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(post("/v1/exits"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAuthenticatedWithOtherRole_shouldReturn403() throws Exception {
        final var jwt = AuthUtil.generateJwt(UUID.randomUUID().toString(), "other");
        final var ticketId = UUID.randomUUID();
        mockMvc.perform(
                post("/v1/exits")
                        .with(jwt)
                        .contentType("application/json")
                        .content("{\"ticketId\": \"%s\"}".formatted(ticketId))
        ).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void whenAuthenticatedAndTicketPayed_shouldReturn200() throws Exception {
        final var entryGateMachineId = UUID.randomUUID();
        final var exitGateMachineId = UUID.randomUUID();
        final var jwt = AuthUtil.generateJwt(exitGateMachineId.toString(), "gate_machine");

        final var timeOfEntry = LocalDateTime.now().plusMinutes(-5);
        final var ticket = new Ticket(entryGateMachineId, timeOfEntry);
        ticket.pay(timeOfEntry.plusMinutes(3));

        ticketsRepo.save(ticket);

        final var request = post("/v1/exits")
                .with(jwt)
                .contentType("application/json")
                .content("{\"ticketId\": \"%s\"}".formatted(ticket.getId())
                );

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        final var updatedTicket = ticketsRepo.findById(ticket.getId())
                .orElse(null);

        assertThat(updatedTicket)
                .isNotNull();

        assertThat(updatedTicket.getTimeOfExit())
                .isNotNull();

        assertThat(updatedTicket.getExitGateId())
                .isEqualTo(exitGateMachineId);
    }
}
