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
    @DisplayName("Cash, exact amount")
    void cashPaymentTest() throws RestaurantException {
        PaymentMethod cash = new CashPayment(new BigDecimal("12.30"));

        assertEquals(new BigDecimal("0.00"), cash.pay(new BigDecimal("12.30")));
    }

    @Test
    @DisplayName("Cash gives the change")
    void cashPaymentGivesTheChange() throws RestaurantException {
        PaymentMethod cash = new CashPayment(new BigDecimal("20.00"));

        assertEquals(new BigDecimal("7.70"), cash.pay(new BigDecimal("12.30")));
    }

    @Test
    @DisplayName("Cash refused when it is not enough")
    void cashInsufficientPaymentTest() {
        PaymentMethod cash = new CashPayment(new BigDecimal("12.30"));

        assertThrows(RestaurantException.class, () -> cash.pay(new BigDecimal("13.30")));
    }

    @Test
    @DisplayName("Card refused when the number is empty")
    void cardEmptyPaymentTest() {
        PaymentMethod card = new CardPayment("");

        assertThrows(RestaurantException.class, () -> card.pay(new BigDecimal("13.30")));
    }

    @Test
    @DisplayName("Card refused when there are too many digits")
    void cardTooManyDigitsPaymentTest() {
        PaymentMethod card = new CardPayment("123456789123456789");

        assertThrows(RestaurantException.class, () -> card.pay(new BigDecimal("13.30")));
    }

    @Test
    @DisplayName("Card refused when the number is not digits")
    void cardNotDigitsPaymentTest() {
        PaymentMethod card = new CardPayment("abcdefghijklmnop");

        assertThrows(RestaurantException.class, () -> card.pay(new BigDecimal("13.30")));
    }
}
