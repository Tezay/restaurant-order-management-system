package model;

import contract.PaymentMethod;
import exception.RestaurantException;

import java.math.BigDecimal;

public class CashPayment implements PaymentMethod {
    private BigDecimal amountGiven;

    public CashPayment(BigDecimal amountGiven) {
        this.amountGiven=amountGiven;
    }

    @Override
    public BigDecimal pay(BigDecimal amountDue) throws RestaurantException {
        if (amountGiven.compareTo(amountDue) < 0 ){
            throw new RestaurantException("This was not the price of the plate...");
        }
        return amountGiven.subtract(amountDue);
    }
}
