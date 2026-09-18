package model;

import contract.PaymentMethod;
import exception.RestaurantException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {
    @Test
    @DisplayName("Cash payment good.")

    void cashPaymentTest() throws RestaurantException {
        PaymentMethod p1 = new CashPayment(new BigDecimal("12.30"));
        assertEquals(new BigDecimal("0.00"), p1.pay(new BigDecimal("12.30")));

    }
    @Test
    @DisplayName("Cash payment insufficient.")

    void cashInsufficientPaymentTest() throws RestaurantException {
        PaymentMethod p2 = new CashPayment(new BigDecimal("12.30"));
        assertThrows(RestaurantException.class, () -> {
            p2.pay(new BigDecimal("13.30"));
        });
    }

    @Test
    @DisplayName("Succesfull card payment.")


    void cardPaymentTest() throws RestaurantException {
        PaymentMethod p3 = new CardPayment("1234567891234567");
        assertEquals( BigDecimal.ZERO, p3.pay(new BigDecimal("13.30")));
    }

    @Test
    @DisplayName("card payment with empty card number.")

    void cardEmptyPaymentTest() throws RestaurantException {
        PaymentMethod p4 = new CardPayment("");
        assertThrows(RestaurantException.class, () -> {
            p4.pay(new BigDecimal("13.30"));
        });
    }
    @Test
    @DisplayName("Card payment with too many digits.")

    void cardToManyDigitPaymentTest() throws RestaurantException {
        PaymentMethod p5 = new CardPayment("123456789123456789");
        assertThrows(RestaurantException.class, () -> {
            p5.pay(new BigDecimal("13.30"));
        });

    };


}
