package de.htwberlin.webtech.korbgeld.pantry;

import de.htwberlin.webtech.korbgeld.pantry.PantryDtos.ExpiryStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PantryServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 15);

    @Test
    void expiryStatusFollowsTheThreeDayRule() {
        assertThat(PantryService.expiryStatus(null, TODAY)).isEqualTo(ExpiryStatus.NO_DATE);
        assertThat(PantryService.expiryStatus(TODAY.minusDays(1), TODAY)).isEqualTo(ExpiryStatus.EXPIRED);
        assertThat(PantryService.expiryStatus(TODAY, TODAY)).isEqualTo(ExpiryStatus.EXPIRING_SOON);
        assertThat(PantryService.expiryStatus(TODAY.plusDays(3), TODAY)).isEqualTo(ExpiryStatus.EXPIRING_SOON);
        assertThat(PantryService.expiryStatus(TODAY.plusDays(4), TODAY)).isEqualTo(ExpiryStatus.OK);
    }
}
