package model;

import contract.PaymentMethod;
import exception.RestaurantException;

import java.math.BigDecimal;

public class CardPayment implements PaymentMethod {

    private String cardNumber;

    public CardPayment(String cardNumber) {
        this.cardNumber=cardNumber;
    }

    @Override
    public BigDecimal pay(BigDecimal amountDue) throws RestaurantException {
        if (cardNumber == null || !cardNumber.matches("\\d{16}" )){
            throw new RestaurantException("You must enter 16 digits");
        }
        return BigDecimal.ZERO;
    }
}
