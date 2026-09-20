package concurrent;

import model.RestaurantOrder.OrderLine;

import java.util.Optional;

public class Cook implements Runnable {

    private static final long MS_PER_MINUTE = 100; // one preparation minute lasts 100ms in reality
    private final KitchenBoard board;

    public Cook(KitchenBoard board) {
        this.board = board;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Optional<OrderLine> line = board.takeNext();

            if (line.isEmpty()) {
                return; // nothing left to cook
            }

            try {
                Thread.sleep(line.get().getItem().getPreparationMinutes() * MS_PER_MINUTE);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // ensure the correct interruption of the cooker if needed
                return;
            }

            board.markFinished();
        }
    }
}
