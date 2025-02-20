package io.kittycody.parking.usecase.issueEntry;

import io.kittycody.parking.AuthUtil;
import io.kittycody.parking.domain.Floor;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IssueEntryTestsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IssueEntryTicketRepo ticketsRepo;

    @Autowired
    private IssueEntryFloorRepo floorsRepo;

    @Test
    void whenNoAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(post("/v1/entries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAuthenticatedWithOtherRole_shouldReturn403() throws Exception {
        final var jwt = AuthUtil.generateJwt(UUID.randomUUID().toString(), "other");
        mockMvc.perform(
                post("/v1/entries")
                        .with(jwt)
                ).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void whenAuthenticatedWithGateMachineRoleAndNoSpaces_shouldReturn406() throws Exception {
        final var gateMachineId = UUID.randomUUID().toString();

        final var jwt = AuthUtil.generateJwt(gateMachineId, "gate_machine");

        mockMvc.perform(
                post("/v1/entries")
                        .with(jwt)
                )
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @Transactional
    void whenAuthenticatedWithGateMachineRoleAndSomeSpaces_shouldReturn200() throws Exception {
        final var gateMachineId = UUID.randomUUID().toString();

        final var jwt = AuthUtil.generateJwt(gateMachineId, "gate_machine");

        final var floor = new Floor(10, LocalDateTime.now());
        floorsRepo.save(floor);

        mockMvc.perform(
                        post("/v1/entries")
                                .with(jwt)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.entryTime").exists())
                .andExpect(jsonPath("$.entryTime").isNotEmpty());

        assertThat(ticketsRepo.count()).isEqualTo(1);
    }
}
