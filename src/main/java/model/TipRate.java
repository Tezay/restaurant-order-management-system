package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum TipRate {
    NO_TIP("0.00"),
    PERCENT_15("0.15"),
    PERCENT_18("0.18"),
    PERCENT_20("0.20");

    private final BigDecimal rate;

    TipRate(String rate) {
        this.rate = new BigDecimal(rate);
    }

    /**
     * @param subtotal the subtotal of the order, before tax
     * @return the tip for this rate, rounded to 2 decimals (HALF_UP)
     */
    public BigDecimal applyTo(BigDecimal subtotal) {
        return subtotal.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
