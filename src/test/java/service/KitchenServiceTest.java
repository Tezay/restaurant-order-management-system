package service;

import exception.RestaurantException;
import model.Category;
import model.DietaryTag;
import model.FoodItem;
import model.OrderStatus;
import model.RestaurantOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KitchenServiceTest {

    @RepeatedTest(10)
    @DisplayName("The order is READY when the kitchen has finished")
    void orderIsReadyAfterTheKitchen() throws RestaurantException {
        RestaurantOrder order = confirmedOrderOf(1);
        KitchenService kitchen = new KitchenService();

        kitchen.start(order);
        assertEquals(OrderStatus.PREPARING, order.getStatus());

        kitchen.waitUntilDone();

        assertEquals(OrderStatus.READY, order.getStatus());
    }

    @RepeatedTest(10)
    @DisplayName("A closed kitchen sends the order back to CONFIRMED")
    void orderGoesBackToConfirmedWhenTheKitchenCloses() throws RestaurantException {
        RestaurantOrder order = confirmedOrderOf(20);
        KitchenService kitchen = new KitchenService();

        kitchen.start(order);
        kitchen.close();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    private static RestaurantOrder confirmedOrderOf(int preparationMinutes) throws RestaurantException {
        RestaurantOrder order = new RestaurantOrder("O0001", "T01");
        order.addItem(new FoodItem("M001", "Classic poutine", new BigDecimal("12.95"),
                Category.MAIN, true, preparationMinutes, DietaryTag.NONE), 1);
        order.moveTo(OrderStatus.CONFIRMED);
        return order;
    }
}
