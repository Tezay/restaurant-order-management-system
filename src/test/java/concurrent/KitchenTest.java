package concurrent;

import exception.RestaurantException;
import model.Category;
import model.DietaryTag;
import model.FoodItem;
import model.MenuItem;
import model.RestaurantOrder;
import model.RestaurantOrder.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KitchenTest {

    @RepeatedTest(10)
    @DisplayName("Two cooks prepare every line")
    void twoCooksPrepareEveryLine() throws RestaurantException, InterruptedException {
        KitchenBoard board = new KitchenBoard(threeLines());
        Thread first = new Thread(new Cook(board), "Cook 1");
        Thread second = new Thread(new Cook(board), "Cook 2");

        first.start();
        second.start();
        first.join();
        second.join();

        assertEquals(3, board.getFinishedCount());
        assertTrue(board.takeNext().isEmpty());
    }

    @RepeatedTest(10)
    @DisplayName("Interrupted cooks stop")
    void interruptedCooksStop() throws RestaurantException, InterruptedException {
        KitchenBoard board = new KitchenBoard(threeLines());
        Thread first = new Thread(new Cook(board), "Cook 1");
        Thread second = new Thread(new Cook(board), "Cook 2");

        first.start();
        second.start();
        first.interrupt();
        second.interrupt();
        first.join();
        second.join();

        assertFalse(first.isAlive());
        assertFalse(second.isAlive());
        assertTrue(board.getFinishedCount() <= 3);
    }

    private static List<OrderLine> threeLines() throws RestaurantException {
        RestaurantOrder order = new RestaurantOrder("O0001", "T01");
        order.addItem(item("M001", "Classic poutine"), 1);
        order.addItem(item("M002", "Double poutine"), 1);
        order.addItem(item("M003", "Maple poutine"), 1);
        return order.getLines();
    }

    private static MenuItem item(String id, String name) {
        return new FoodItem(id, name, new BigDecimal("12.95"), Category.MAIN, true, 1, DietaryTag.NONE);
    }
}
