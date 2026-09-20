package service;

import contract.PaymentMethod;
import exception.RestaurantException;
import model.CashPayment;
import model.Category;
import model.DiningTable;
import model.DietaryTag;
import model.FoodItem;
import model.MenuItem;
import model.OrderStatus;
import model.RestaurantOrder;
import model.TipRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.Menu;
import repository.Repository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderServiceTest {

    private Menu menu;
    private Repository<DiningTable> tables;
    private Repository<RestaurantOrder> orders;
    private OrderService orderService;

    @BeforeEach
    void setUp() throws RestaurantException {
        menu = new Menu();
        tables = new Repository<>();
        orders = new Repository<>();

        tables.add(new DiningTable("T01", 4));

        orderService = new OrderService(menu, tables, orders);
    }

    @Test
    @DisplayName("Verify if second order on a busy table -> not possible")
    void impossibleSecondOrderOnBusyTable() throws RestaurantException {
        orderService.openOrder("T01");

        assertThrows(RestaurantException.class, () -> orderService.openOrder("T01"));
    }

    @Test
    @DisplayName("Verify if adding an unavailable item -> not possible")
    void impossibleToAddUnavailableItem() throws RestaurantException {
        MenuItem soldOut = new FoodItem("M010", "Sold out soup", new BigDecimal("6.00"), Category.MAIN, false, 5, DietaryTag.NONE);
        menu.add(soldOut);

        RestaurantOrder order = orderService.openOrder("T01");

        assertThrows(RestaurantException.class, () -> orderService.addItem(order.getId(), "M010", 1));
        assertTrue(order.getLines().isEmpty());
    }

    @Test
    @DisplayName("Verify if pay before READY -> not possible")
    void impossibleToPayBeforeReady() throws RestaurantException {
        MenuItem soup = new FoodItem("M011", "Soup", new BigDecimal("6.00"), Category.MAIN, true, 5, DietaryTag.NONE);
        menu.add(soup);

        RestaurantOrder order = orderService.openOrder("T01");
        orderService.addItem(order.getId(), "M011", 1);

        PaymentMethod cash = new CashPayment(new BigDecimal("100.00"));

        assertThrows(RestaurantException.class, () -> orderService.pay(order.getId(), TipRate.NO_TIP, cash));
        assertEquals(OrderStatus.OPEN, order.getStatus());
    }

    @Test
    @DisplayName("Verify if pay twice -> second payment not possible")
    void impossibleToPayTwice() throws RestaurantException {
        MenuItem soup = new FoodItem("M012", "Soup", new BigDecimal("6.00"), Category.MAIN, true, 5, DietaryTag.NONE);
        menu.add(soup);

        RestaurantOrder order = orderService.openOrder("T01");
        orderService.addItem(order.getId(), "M012", 1);
        order.moveTo(OrderStatus.CONFIRMED);
        order.moveTo(OrderStatus.PREPARING);
        order.moveTo(OrderStatus.READY);

        PaymentMethod cash = new CashPayment(new BigDecimal("100.00"));

        orderService.pay(order.getId(), TipRate.NO_TIP, cash);

        assertThrows(RestaurantException.class, () -> orderService.pay(order.getId(), TipRate.NO_TIP, cash));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }
}
