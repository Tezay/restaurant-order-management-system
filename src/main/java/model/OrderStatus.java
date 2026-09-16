package model;

public enum OrderStatus {
    OPEN,
    CONFIRMED,
    PREPARING,
    READY,
    PAID,
    CLOSED,
    CANCELLED;

    public boolean canMoveTo(OrderStatus next) {
        throw new UnsupportedOperationException();
    }

    public boolean isActive() {
        throw new UnsupportedOperationException();
    }
}
