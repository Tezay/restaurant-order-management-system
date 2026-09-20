package model;

import exception.RestaurantException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantOrderTest {

    private RestaurantOrder order;
    private MenuItem poutine;
    private MenuItem spruceBeer;

    @BeforeEach
    void setUp() {
        order = new RestaurantOrder("O001", "T01");

        poutine = new FoodItem("M001", "Classic poutine", new BigDecimal("12.95"),
            Category.MAIN, true, 8, DietaryTag.NONE);

        spruceBeer = new Beverage("M023", "Spruce beer", new BigDecimal("3.95"),
            Category.DRINK, true, 355, false);
    }

    @Test
    @DisplayName("2 poutines + 1 spruce beer, tip 18% : sous-total, taxe, tip et total exacts")
    void validOrder_matchesHandComputedValues() throws RestaurantException {
        order.addItem(poutine, 2);        // 2 * 12.95 = 25.90
        order.addItem(spruceBeer, 1);     // 1 * 3.95  =  3.95
        order.setTipRate(TipRate.PERCENT_18);

        assertEquals(new BigDecimal("29.85"), order.getSubtotal());
        assertEquals(new BigDecimal("4.47"), order.getTax());
        assertEquals(new BigDecimal("5.37"), order.getTip());
        assertEquals(new BigDecimal("39.69"), order.getTotal());
    }

    @Test
    @DisplayName("Ajouter le même item deux fois fusionne la quantité sur une seule ligne")
    void addItem_sameItemTwice_mergesIntoOneLine() throws RestaurantException {
        order.addItem(poutine, 1);
        order.addItem(poutine, 1);

        List<RestaurantOrder.OrderLine> lines = order.getLines();
        assertEquals(1, lines.size());
        assertEquals(2, lines.get(0).getQuantity());
    }

    @Test
    @DisplayName("Une commande CONFIRMED refuse addItem, et les lignes restent inchangées")
    void addItem_afterMoveToConfirmed_throwsAndLinesUnchanged() throws RestaurantException {
        order.addItem(poutine, 1);
        order.moveTo(OrderStatus.CONFIRMED);

        assertThrows(RestaurantException.class, () -> order.addItem(spruceBeer, 1));

        List<RestaurantOrder.OrderLine> lines = order.getLines();
        assertEquals(1, lines.size());
        assertEquals(poutine, lines.get(0).getItem());
        assertEquals(1, lines.get(0).getQuantity());
    }

    @Test
    @DisplayName("restore ne partage pas la map de l'appelant : mutation ultérieure sans effet")
    void restore_makesACopy_notSharingCallersMap() throws RestaurantException {
        Map<MenuItem, Integer> items = new HashMap<>();
        items.put(poutine, 2);

        RestaurantOrder restored = RestaurantOrder.restore(
            "O003", "T03", OrderStatus.OPEN, TipRate.NO_TIP, items);

        items.put(spruceBeer, 5); // mutation après coup : ne doit pas affecter la commande restaurée

        assertEquals(1, restored.getLines().size());
        assertEquals(new BigDecimal("25.90"), restored.getSubtotal());
    }
}
