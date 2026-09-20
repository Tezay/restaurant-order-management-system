package report;

import model.Beverage;
import model.Category;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportServiceTest {

    // Small menu built in the test
    private MenuItem poutine;   // M001  MAIN     10.00  available    NONE
    private MenuItem salad;     // M002  STARTER   8.00  available    VEGAN
    private MenuItem tofu;      // M003  MAIN     12.00  available    VEGAN
    private MenuItem curry;     // M004  MAIN     14.00  UNAVAILABLE   VEGAN
    private MenuItem beer;      // M005  DRINK     4.00  available    (beverage)

    private ReportService service;

    // ---------------------------------------------------------------
    // Helpers: if the FoodItem / Beverage constructors differ from
    // these assumptions, only these two methods need to change.
    // ---------------------------------------------------------------
    private static MenuItem food(String id, String name, String price, Category category,
                                 boolean available, DietaryTag tag) {
        return new FoodItem(id, name, new BigDecimal(price), category, available, 5, tag);
    }

    private static MenuItem drink(String id, String name, String price, boolean available) {
        return new Beverage(id, name, new BigDecimal(price), Category.DRINK, available, 330, true);
    }

    private static RestaurantOrder order(String id, String tableId, OrderStatus status,
                                         Map<MenuItem, Integer> items) throws Exception {
        return RestaurantOrder.restore(id, tableId, status, TipRate.NO_TIP, items);
    }

    /** BigDecimal.equals compares the scale too (0 != 0.00), so we compare the values. */
    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
            "expected " + expected + " but was " + actual);
    }

    @BeforeEach
    void setUp() throws Exception {
        poutine = food("M001", "Poutine", "10.00", Category.MAIN, true, DietaryTag.NONE);
        salad = food("M002", "Green salad", "8.00", Category.STARTER, true, DietaryTag.VEGAN);
        tofu = food("M003", "Tofu bowl", "12.00", Category.MAIN, true, DietaryTag.VEGAN);
        curry = food("M004", "Veggie curry", "14.00", Category.MAIN, false, DietaryTag.VEGAN);
        beer = drink("M005", "Beer", "4.00", true);

        Menu menu = new Menu();
        menu.add(poutine);
        menu.add(salad);
        menu.add(tofu);
        menu.add(curry);
        menu.add(beer);

        Repository<RestaurantOrder> orders = new Repository<>();

        // O0001 PAID: poutine x1 (10.00) + beer x2 (8.00)
        //   subtotal 18.00 ; tax 18.00 * 0.14975 = 2.6955 -> 2.70 ; total 20.70
        Map<MenuItem, Integer> paidItems = new LinkedHashMap<>();
        paidItems.put(poutine, 1);
        paidItems.put(beer, 2);
        orders.add(order("O0001", "T01", OrderStatus.PAID, paidItems));

        // O0002 CLOSED: salad x1 (8.00) + tofu x1 (12.00)
        //   subtotal 20.00 ; tax 20.00 * 0.14975 = 2.995 -> 3.00 ; total 23.00
        Map<MenuItem, Integer> closedItems = new LinkedHashMap<>();
        closedItems.put(salad, 1);
        closedItems.put(tofu, 1);
        orders.add(order("O0002", "T02", OrderStatus.CLOSED, closedItems));

        // O0003 OPEN: tofu x3 -> must be ignored by the revenue reports
        Map<MenuItem, Integer> openItems = new LinkedHashMap<>();
        openItems.put(tofu, 3);
        orders.add(order("O0003", "T03", OrderStatus.OPEN, openItems));

        service = new ReportService(menu, orders);
    }

    @Test
    @DisplayName("Total revenue adds up only the PAID and CLOSED orders (tax included) and ignores the OPEN one")
    void totalRevenue_countsOnlyPaidAndClosedOrders() {
        // 20.70 (PAID) + 23.00 (CLOSED) = 43.70
        assertMoney("43.70", service.totalRevenue());
    }

    @Test
    @DisplayName("Sales by category add up the line amounts of PAID and CLOSED orders only, without tax")
    void salesByCategory_sumsLineAmountsOfPaidAndClosedOrders() {
        Map<Category, BigDecimal> sales = service.salesByCategory();

        assertEquals(3, sales.size());
        assertMoney("22.00", sales.get(Category.MAIN));    // poutine 10.00 + tofu 12.00 (OPEN tofu ignored)
        assertMoney("8.00", sales.get(Category.STARTER));  // salad
        assertMoney("8.00", sales.get(Category.DRINK));    // 2 beers
    }

    @Test
    @DisplayName("Available items with a tag returns only the available food items carrying that tag, in id order")
    void availableItemsWithTag_returnsAvailableFoodWithTheTag() {
        // M004 is vegan but unavailable, M005 is a beverage, M001 has the tag NONE
        assertEquals(List.of(salad, tofu), service.availableItemsWithTag(DietaryTag.VEGAN));
    }

    @Test
    @DisplayName("With no order and no menu item, revenue is 0.00 and both the sales map and the item list are empty")
    void emptyCase_returnsZeroAndEmptyResults() {
        ReportService emptyService = new ReportService(new Menu(), new Repository<>());

        assertAll(
            () -> assertMoney("0.00", emptyService.totalRevenue()),
            () -> assertTrue(emptyService.salesByCategory().isEmpty()),
            () -> assertTrue(emptyService.availableItemsWithTag(DietaryTag.VEGAN).isEmpty())
        );
    }
}
