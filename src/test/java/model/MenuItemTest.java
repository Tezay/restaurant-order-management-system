package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;

class MenuItemTest {
    @Test
    @DisplayName("Two items with the same id are equal for compareTo.")
    void compareToReturnsZeroForSameId(){
        MenuItem poutine = new FoodItem("M001","Classic Poutine",
            new BigDecimal("12.95"),Category.MAIN, true, 8,
            DietaryTag.NONE);
        MenuItem spruceBeer = new Beverage("M001", "beer",
            new BigDecimal("3.95"), Category.MAIN, true, 33,true);

        assertEquals(0,poutine.compareTo(spruceBeer));
    }
}
