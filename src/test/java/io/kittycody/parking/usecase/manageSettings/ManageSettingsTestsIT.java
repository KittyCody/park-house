package io.kittycody.parking.usecase.manageSettings;

import io.kittycody.parking.AuthUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ManageSettingsTestsIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingSettingsRepository settingsRepo;

    @Test
    void updateOperationalHours_whenNoAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(put("/v1/settings/current"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateOperationalHours_whenAuthenticatedWithOtherRole_shouldReturn403() throws Exception {
            final var jwt = AuthUtil.generateJwt("userId", "user");
            mockMvc.perform(
                    put("/v1/settings/current")
                            .with(jwt)
                            .contentType("application/json")
                            .content("{\"openHour\": 8, \"closeHour\": 18}")
            )
                    .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void updateOperationalHours_whenAuthenticatedWithAdminRoleAndValidHours_shouldReturn204() throws Exception {
        final var jwt = AuthUtil.generateJwt("adminId", "parking_admin");

        mockMvc.perform(
                        put("/v1/settings/current")
                                .with(jwt)
                                .contentType("application/json")
                                .content("{\"openHour\": 8, \"closeHour\": 18}")
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var settings = settingsRepo.findTopByOrderByIdDesc().orElseThrow();
        assertThat(settings.openHour()).isEqualTo(8);
        assertThat(settings.closeHour()).isEqualTo(18);
    }

    @Test
    void updateOperationalHours_whenInvalidOperationalHours_shouldReturn400() throws Exception {
        final var jwt = AuthUtil.generateJwt("adminId", "parking_admin");

        mockMvc.perform(
                put("/v1/settings/current")
                .with(jwt)
                .contentType("application/json")
                .content("{\"openHour\": 18, \"closeHour\": 8}")
        )
                .andExpect(status().isBadRequest());
    }
}
