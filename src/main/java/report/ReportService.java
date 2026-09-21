package report;

import model.Category;
import model.DietaryTag;
import model.FoodItem;
import model.MenuItem;
import model.OrderStatus;
import model.RestaurantOrder;
import model.RestaurantOrder.OrderLine;
import repository.Menu;
import repository.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ReportService {
    private final Menu menu;
    private final Repository<RestaurantOrder> orders;

    private final Predicate<RestaurantOrder> countAsRevenue =
        o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.CLOSED;

    public ReportService(Menu menu, Repository<RestaurantOrder> orders) {
        this.menu = menu;
        this.orders = orders;
    }

    public BigDecimal totalRevenue() {
        Function<RestaurantOrder, BigDecimal> totalOf = RestaurantOrder::getTotal;
        return orders.getAll().stream().filter(countAsRevenue).map(totalOf)
            .reduce(BigDecimal.ZERO.setScale(2), BigDecimal::add);
    }

    public Map<Category, BigDecimal> salesByCategory() {
        Map<Category, BigDecimal> sales = new HashMap<>();

        Consumer<OrderLine> addToCategory = line -> {
            Category category = line.getItem().getCategory();
            sales.put(category, sales.getOrDefault(category, BigDecimal.ZERO).add(line.getAmount()));
        };

        orders.getAll().stream().filter(countAsRevenue).flatMap(o -> o.getLines().stream())
            .forEach(addToCategory);

        return sales;
    }

    public List<MenuItem> availableItemsWithTag(DietaryTag tag) {
        Predicate<MenuItem> matches = item -> item.isAvailable() && item instanceof FoodItem food
            && food.getDietaryTag() == tag;

        return menu.getAll().stream().filter(matches).toList();
    }
}
