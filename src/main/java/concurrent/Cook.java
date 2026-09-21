package concurrent;

import model.RestaurantOrder.OrderLine;

import java.util.Optional;

public class Cook implements Runnable {

    // one preparation minute lasts 100 ms, so a demo does not take real minutes
    private static final long MS_PER_MINUTE = 100;
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
            } catch (InterruptedException e) {
                // sleep cleared the flag -> put it back so the caller knows this cook was interrupted
                Thread.currentThread().interrupt();
                return;
            }

            board.markFinished();
        }
    }
}
