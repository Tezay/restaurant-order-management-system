package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuItemTest {
    @Test
    @DisplayName("Two items with the same id are equal for compareTo.")
    void compareToReturnsZeroForSameId(){
        MenuItem poutine = new FoodItem("M001","Classic Poutine",
            new BigDecimal("12.95"),Category.MAIN, true, 8, DietaryTag.NONE);
        MenuItem beer = new Beverage("M001", "Beer",
            new BigDecimal("3.95"), Category.DRINK, true, 33,true);

        assertEquals(0, poutine.compareTo(beer));
    }
}
