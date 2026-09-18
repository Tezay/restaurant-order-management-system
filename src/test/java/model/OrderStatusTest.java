package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusTest {

    @Test
    @DisplayName("Allowed changes")
    void allowsTheChangesOfTheAnalysis() {
        assertAll(
                () -> assertTrue(OrderStatus.OPEN.canMoveTo(OrderStatus.CONFIRMED)),
                () -> assertTrue(OrderStatus.OPEN.canMoveTo(OrderStatus.CANCELLED)),
                () -> assertTrue(OrderStatus.CONFIRMED.canMoveTo(OrderStatus.PREPARING)),
                () -> assertTrue(OrderStatus.PREPARING.canMoveTo(OrderStatus.READY)),
                () -> assertTrue(OrderStatus.PREPARING.canMoveTo(OrderStatus.CONFIRMED)),
                () -> assertTrue(OrderStatus.READY.canMoveTo(OrderStatus.PAID)),
                () -> assertTrue(OrderStatus.PAID.canMoveTo(OrderStatus.CLOSED)));
    }

    @Test
    @DisplayName("Other changes are refused")
    void refusesTheOtherChanges() {
        assertAll(
                () -> assertFalse(OrderStatus.OPEN.canMoveTo(OrderStatus.PAID)),
                () -> assertFalse(OrderStatus.READY.canMoveTo(OrderStatus.CLOSED)),
                () -> assertFalse(OrderStatus.CONFIRMED.canMoveTo(OrderStatus.CANCELLED)),
                () -> assertFalse(OrderStatus.CLOSED.canMoveTo(OrderStatus.OPEN)),
                () -> assertFalse(OrderStatus.CANCELLED.canMoveTo(OrderStatus.OPEN)));
    }

    @Test
    @DisplayName("Active until closed or cancelled")
    void isActiveUntilClosedOrCancelled() {
        assertAll(
                () -> assertTrue(OrderStatus.OPEN.isActive()),
                () -> assertTrue(OrderStatus.PREPARING.isActive()),
                () -> assertTrue(OrderStatus.PAID.isActive()),
                () -> assertFalse(OrderStatus.CLOSED.isActive()),
                () -> assertFalse(OrderStatus.CANCELLED.isActive()));
    }
}
