package model;

import contract.PaymentMethod;
import exception.RestaurantException;

import java.math.BigDecimal;

public class CashPayment implements PaymentMethod {

    public CashPayment(BigDecimal amountGiven) {
    }

    @Override
    public BigDecimal pay(BigDecimal amountDue) throws RestaurantException {
        throw new UnsupportedOperationException();
    }
}
