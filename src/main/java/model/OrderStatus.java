package model;

public enum OrderStatus {
    OPEN,
    CONFIRMED,
    PREPARING,
    READY,
    PAID,
    CLOSED,
    CANCELLED;

    /**
     * open -> confirmed -> preparing -> ready -> paid -> closed
     * An open order can also be cancelled.
     * A preparing order goes back to confirmed when the kitchen closes too early.
     * @param next the next status
     * @return {@code true} if the next status is possible,
     *         {@code false} if it's not
     */
    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case OPEN -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == PREPARING;
            case PREPARING -> next == READY || next == CONFIRMED;
            case READY -> next == PAID;
            case PAID -> next == CLOSED;
            case CLOSED, CANCELLED -> false;
        };
    }

    /**
     * @return {@code true} from open to paid,
     *         {@code false} otherwise
     */
    public boolean isActive() {
        return switch (this) {
            case OPEN, CONFIRMED, PREPARING, READY, PAID -> true;
            default -> false;
        };
    }
}
