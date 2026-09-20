package concurrent;

import model.RestaurantOrder.OrderLine;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class KitchenBoard {

    private final Queue<OrderLine> pending;
    private int finishedCount;

    public KitchenBoard(List<OrderLine> lines) {
        this.pending = new LinkedList<>(lines);
    }

    /**
     * @return an Optional containing the next pending order line,
     *         or an empty Optional if no order line is pending
     */
    public synchronized Optional<OrderLine> takeNext() {
        return Optional.ofNullable(pending.poll());
    }

    public synchronized void markFinished() {
        finishedCount++;
    }

    public synchronized int getFinishedCount() {
        return finishedCount;
    }
}
