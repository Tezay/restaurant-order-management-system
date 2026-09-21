package model;

import exception.RestaurantException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum TipRate {
    NO_TIP(0, "0.00"),
    PERCENT_15(15, "0.15"),
    PERCENT_18(18, "0.18"),
    PERCENT_20(20, "0.20");

    private final int percent;
    private final BigDecimal rate;

    TipRate(int percent, String rate) {
        this.percent = percent;
        this.rate = new BigDecimal(rate);
    }

    /**
     * @param subtotal the subtotal of the order, before tax
     * @return the tip for this rate, rounded to 2 decimals (HALF_UP)
     */
    public BigDecimal applyTo(BigDecimal subtotal) {
        return subtotal.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public int getPercent() {
        return percent;
    }

    /**
     * @param percent the percent written in orders.txt
     * @return the matching rate
     * @throws RestaurantException if no rate uses this percent
     */
    public static TipRate fromPercent(int percent) throws RestaurantException {
        for (TipRate tipRate : values()) {
            if (tipRate.getPercent() == percent) {
                return tipRate;
            }
        }
        throw new RestaurantException("Unknown tip percent: " + percent);
    }
}
