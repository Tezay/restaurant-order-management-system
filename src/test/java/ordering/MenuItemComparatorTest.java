package ordering;

import model.Category;
import model.FoodItem;
import model.MenuItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.DietaryTag;
import model.Category;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuItemComparatorTest {

    @Test
    @DisplayName("Verify if price equals -> sort by id")
    void byPriceUsesIdsWhenPricesAreEquals() {
        MenuItem classic = new FoodItem("M003", "Classic poutine", new BigDecimal("9.50"), Category.MAIN, true, 5, DietaryTag.NONE);
        MenuItem yesterday = new FoodItem("M001", "Yesterday's poutine", new BigDecimal("9.50"), Category.MAIN, true, 5, DietaryTag.NONE);
        MenuItem pie = new FoodItem("M002", "Sugar pie", new BigDecimal("3.95"), Category.MAIN, true, 5, DietaryTag.NONE);

        List<MenuItem> items = new ArrayList<>(List.of(classic, yesterday, pie));

        Collections.sort(items, MenuItemComparators.byPrice());

        assertEquals(List.of(pie,yesterday, classic), items);
    }

    @Test
    @DisplayName("Verify if name equals -> sort by id")
    void byNameUsesIdsWhenNamesAreEquals(){
        MenuItem cheap = new FoodItem("M003", "Poutine", new BigDecimal("9.50"), Category.MAIN, true, 5, DietaryTag.NONE);
        MenuItem expensive = new FoodItem("M001", "Poutine", new BigDecimal("12.95"), Category.MAIN, true, 5, DietaryTag.NONE);
        MenuItem maple = new FoodItem("M002", "Maple poutine", new BigDecimal("9.50"), Category.MAIN, true, 5, DietaryTag.NONE);

        List<MenuItem> items = new ArrayList<>(List.of(cheap, expensive, maple));

        Collections.sort(items, MenuItemComparators.byName());

        assertEquals(List.of(maple,expensive, cheap), items);
    }

}
