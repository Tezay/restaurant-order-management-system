package repository;

import exception.RestaurantException;
import model.Category;
import model.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryMenuTest {
    private Menu menu;

    private static class TestMenuItem extends MenuItem {
        public TestMenuItem(String id, String name, BigDecimal price, Category category, boolean available) {
            super(id, name, price, category, available);
        }

        @Override
        public int getPreparationMinutes() {
            return 10;
        }

        @Override
        public String describe() {
            return getName();
        }
    }

    @BeforeEach
    void setUp() {
        menu = new Menu();
    }

    private MenuItem createItem(String id, String name) {
        return new TestMenuItem(id, name, new BigDecimal("12.50"), Category.MAIN, true);
    }

    @Test
    @DisplayName("Verify and refuse an already existing Id.")
    void testAddDuplicateIdThrowsException() throws RestaurantException {
        MenuItem item1 = createItem("M001", "Classic poutine");
        MenuItem item2 = createItem("M001", "Double poutine");

        menu.add(item1);

        RestaurantException exception = assertThrows(RestaurantException.class, () -> menu.add(item2));

        assertEquals("Item's id already exists.", exception.getMessage());
    }

    @Test
    @DisplayName("Use explicit iterator to traverse the list with the Ids in the right order.")
    void testIteratorExplicitLoopInIdOrder() throws RestaurantException {
        // adding with no order
        MenuItem item3 = createItem("M003", "Maple poutine");
        MenuItem item1 = createItem("M001", "Classic poutine");
        MenuItem item2 = createItem("M002", "Double poutine");

        menu.add(item3);
        menu.add(item1);
        menu.add(item2);


        Iterator<MenuItem> iterator = menu.iterator();
        List<String> iteratedIds = new ArrayList<>();

        while (iterator.hasNext()) {
            MenuItem item = iterator.next();
            iteratedIds.add(item.getId());
        }

        assertEquals(3, iteratedIds.size());
        assertEquals("M001", iteratedIds.get(0));
        assertEquals("M002", iteratedIds.get(1));
        assertEquals("M003", iteratedIds.get(2));
    }

    @Test
    @DisplayName("getAll() should return a non modifiable list (read-only).")
    void testGetAllIsReadOnly() throws RestaurantException {
        MenuItem item = createItem("M001", "Sweet potato fries");
        menu.add(item);

        assertThrows(UnsupportedOperationException.class, () -> menu.getAll().clear());
    }
}
