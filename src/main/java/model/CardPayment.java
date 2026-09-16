package model;

import contract.PaymentMethod;
import exception.RestaurantException;

import java.math.BigDecimal;

public class CardPayment implements PaymentMethod {

    public CardPayment(String cardNumber) {
    }

    @Override
    public BigDecimal pay(BigDecimal amountDue) throws RestaurantException {
        throw new UnsupportedOperationException();
    }
}
