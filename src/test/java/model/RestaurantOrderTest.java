package model;

import exception.RestaurantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantOrderTest {

    private RestaurantOrder order;
    private MenuItem poutine;
    private MenuItem spruceBeer;

    @BeforeEach
    void setUp(){
        order = new RestaurantOrder("O001", "T01");

        poutine = new FoodItem("M001", "Classic poutine", new BigDecimal("12.95"),
            Category.MAIN, true, 8, DietaryTag.NONE);

        spruceBeer = new Beverage("M023", "Spruce beer", new BigDecimal("3.95"),
            Category.DRINK, true, 355, false);
    }

    @Test
    @DisplayName("Valid order with Tip and Taxes verification")
    void validOrder_withItemsAndTip_isConsistent() throws RestaurantException {
        order.addItem(poutine, 2);   // 25.90
        order.addItem(spruceBeer, 1);      // 3.95
        order.setTipRate(TipRate.PERCENT_18);
        order.moveTo(OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(2, order.getLines().size());
        assertEquals(0, order.getSubtotal().compareTo(new BigDecimal("29.85")));
        assertTrue(order.getTax().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(order.getTip().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(0, order.getTotal().compareTo(
            order.getSubtotal().add(order.getTax()).add(order.getTip())));
    }

    @Test
    @DisplayName("Unavailable item leaves order unchanged")
    void addItem_unavailableItem_orderUnchanged() throws RestaurantException {
        MenuItem unavailable = new FoodItem("M013", "Expired poutine", new BigDecimal("4.75"),
            Category.MAIN, false, 2, DietaryTag.NONE);
        order.addItem(poutine, 1);

        assertThrows(RestaurantException.class, () -> order.addItem(unavailable, 1));

        assertEquals(1, order.getLines().size());
        assertNull(order.findItemById("M013"));
        assertEquals(0, order.getSubtotal().compareTo(new BigDecimal("12.95")));
    }

    @Test
    @DisplayName("Refused change of state")
    void moveTo_fromTerminalState_isRefused() throws RestaurantException {
        Map<MenuItem, Integer> items = new HashMap<>();
        RestaurantOrder closedOrder = RestaurantOrder.restore(
            "O007", "T07", OrderStatus.CLOSED, TipRate.NO_TIP, items);

        assertThrows(RestaurantException.class, () -> closedOrder.moveTo(OrderStatus.OPEN));
        assertEquals(OrderStatus.CLOSED, closedOrder.getStatus());
    }
}
