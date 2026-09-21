package app;

import contract.PaymentMethod;
import exception.RestaurantException;
import model.Category;
import model.DietaryTag;
import model.DiningTable;
import model.FoodItem;
import model.MenuItem;
import model.RestaurantOrder;
import model.TipRate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.Menu;
import repository.Repository;
import report.ReportService;
import service.KitchenService;
import service.OrderService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleMenuTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private final List<String> calls = new ArrayList<>();
    private final Menu menu = new Menu();
    private RestaurantException refusal;

    @BeforeEach
    void setUp() throws RestaurantException {
        menu.add(new FoodItem("M001", "Poutine", new BigDecimal("12.50"), Category.MAIN, true, 8, DietaryTag.NONE));
    }

    @AfterEach
    void restoreConsole() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("The public @MenuOption methods are shown, numbered and sorted by order")
    void optionsAreShownSortedByOrder() {
        String expected = """
                [1] Show the menu
                [2] Open an order
                [3] Add an item
                [4] Change a quantity
                [5] Remove an item
                [6] Cancel an order
                [7] Confirm an order
                [8] Send an order to the kitchen
                [9] Wait for the kitchen
                [10] Close the kitchen
                [11] Pay an order
                [12] Close an order
                [13] Show the reports
                [14] Exit
                """;

        String shown = run("14\n");

        assertTrue(shown.contains(expected));
    }

    @Test
    @DisplayName("A wrong number or a wrong text asks again and never crashes")
    void wrongInputAsksAgain() {
        // choice: text, too big, too small, empty, then 3 (add); quantity: text, 0, 100, then 2;
        // then 11 (pay), tip 2, cash, amount: text, negative, then 20
        String shown = run("abc\n99\n0\n\n3\nO0001\nM001\nabc\n0\n100\n2\n11\nO0001\n2\n1\nabc\n-5\n20\n14\n");

        assertEquals(List.of("add:O0001:M001:2", "pay:O0001:PERCENT_15"), calls);
        assertTrue(shown.contains("Change: 5.00"));

        // a closed input ends the console without a crash
        assertDoesNotThrow(() -> run(""));
        assertDoesNotThrow(() -> run("2\n"));
    }

    @Test
    @DisplayName("Each option calls its service")
    void eachOptionCallsItsService() {
        // 1 menu, 2 open, 3 add, 4 change, 5 remove, 6 cancel, 7 confirm, 8 send to kitchen, 9 wait,
        // 10 close kitchen, 11 pay (no tip, cash), 12 close, 13 reports (tag 3 = VEGAN), 14 exit
        String shown = run("1\n2\nt01\n3\nO0001\nM001\n1\n4\nO0001\nM001\n3\n5\nO0001\nM001\n6\nO0001\n7\nO0001\n"
            + "8\nO0001\n9\n10\n11\nO0001\n1\n1\n20\n12\nO0001\n13\n3\n14\n");

        assertEquals(List.of("open:T01", "add:O0001:M001:1", "update:O0001:M001:3", "remove:O0001:M001",
            "cancel:O0001", "confirm:O0001", "find:O0001", "kitchen-start:O0001", "kitchen-wait",
            "kitchen-close", "pay:O0001:NO_TIP", "close:O0001", "tag:VEGAN"), calls);
        assertTrue(shown.contains("Poutine") && shown.contains("Order O0001 opened on table T01."));
        assertTrue(shown.contains("100.00") && shown.contains("MAIN : 60.00") && shown.contains("Green salad"));
    }

    @Test
    @DisplayName("A refused operation prints its message, the loop goes on, and Exit stops it")
    void refusedOperationPrintsTheMessageAndExitStops() {
        refusal = new RestaurantException("Table T01 already has an active order.");

        // 2 = refused, 1 = the loop goes on, 14 = exit: "2, T02" after it must not be read
        String shown = run("2\nT01\n1\n14\n2\nT02\n");

        assertTrue(shown.contains("Refused: Table T01 already has an active order.") && shown.contains("Poutine"));
        assertEquals(List.of("open:T01"), calls);
    }

    // The console reads System.in when it is created, so the input is set first.
    private String run(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        new ConsoleMenu(new FakeOrderService(), new FakeKitchen(), new FakeReports(), menu).run();

        return output.toString(StandardCharsets.UTF_8);
    }

    private class FakeOrderService extends OrderService {
        FakeOrderService() {
            super(new Menu(), new Repository<DiningTable>(), new Repository<RestaurantOrder>());
        }

        @Override
        public RestaurantOrder openOrder(String tableId) throws RestaurantException {
            calls.add("open:" + tableId);
            if (refusal != null) {
                throw refusal;
            }
            return new RestaurantOrder("O0001", tableId);
        }

        @Override
        public void addItem(String orderId, String itemId, int quantity) {
            calls.add("add:" + orderId + ":" + itemId + ":" + quantity);
        }

        @Override
        public void updateQuantity(String orderId, String itemId, int quantity) {
            calls.add("update:" + orderId + ":" + itemId + ":" + quantity);
        }

        @Override
        public void removeItem(String orderId, String itemId) {
            calls.add("remove:" + orderId + ":" + itemId);
        }

        @Override
        public void cancel(String orderId) {
            calls.add("cancel:" + orderId);
        }

        @Override
        public void confirm(String orderId) {
            calls.add("confirm:" + orderId);
        }

        @Override
        public BigDecimal pay(String orderId, TipRate tipRate, PaymentMethod method) throws RestaurantException {
            calls.add("pay:" + orderId + ":" + tipRate);
            return method.pay(new BigDecimal("15.00"));
        }

        @Override
        public void close(String orderId) {
            calls.add("close:" + orderId);
        }

        @Override
        public RestaurantOrder findOrder(String orderId) {
            calls.add("find:" + orderId);
            return new RestaurantOrder(orderId, "T01");
        }
    }

    private class FakeKitchen extends KitchenService {
        @Override
        public void start(RestaurantOrder order) {
            calls.add("kitchen-start:" + order.getId());
        }

        @Override
        public void waitUntilDone() {
            calls.add("kitchen-wait");
        }

        @Override
        public void close() {
            calls.add("kitchen-close");
        }
    }

    private class FakeReports extends ReportService {
        FakeReports() {
            super(new Menu(), new Repository<RestaurantOrder>());
        }

        @Override
        public BigDecimal totalRevenue() {
            return new BigDecimal("100.00");
        }

        @Override
        public Map<Category, BigDecimal> salesByCategory() {
            return Map.of(Category.MAIN, new BigDecimal("60.00"));
        }

        @Override
        public List<MenuItem> availableItemsWithTag(DietaryTag tag) {
            calls.add("tag:" + tag);
            return List.of(new FoodItem("M002", "Green salad", new BigDecimal("7.90"),
                Category.STARTER, true, 3, DietaryTag.VEGAN));
        }
    }
}
