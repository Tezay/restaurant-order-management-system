package concurrent;

import model.RestaurantOrder.OrderLine;

import java.util.List;
import java.util.Optional;

public class KitchenBoard {

    public KitchenBoard(List<OrderLine> lines) {
    }

    public synchronized Optional<OrderLine> takeNext() {
        throw new UnsupportedOperationException();
    }

    public synchronized void markFinished() {
        throw new UnsupportedOperationException();
    }

    public synchronized int getFinishedCount() {
        throw new UnsupportedOperationException();
    }
}
