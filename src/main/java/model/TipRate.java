package model;

import java.math.BigDecimal;

public enum TipRate {
    NO_TIP,
    PERCENT_15,
    PERCENT_18,
    PERCENT_20;

    public BigDecimal applyTo(BigDecimal subtotal) {
        throw new UnsupportedOperationException();
    }
}
