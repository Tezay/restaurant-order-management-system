package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TipRateTest {

    @Test
    @DisplayName("Tip on the subtotal")
    void appliesTheRateToTheSubtotal() {
        assertEquals(new BigDecimal("3.00"), TipRate.PERCENT_15.applyTo(new BigDecimal("20.00")));
    }

    @Test
    @DisplayName("Half a cent is rounded up")
    void roundsHalfUp() {
        assertEquals(new BigDecimal("1.82"), TipRate.PERCENT_15.applyTo(new BigDecimal("12.10")));
    }
}
