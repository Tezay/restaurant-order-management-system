package report;

import model.Category;
import model.DietaryTag;
import model.MenuItem;
import model.RestaurantOrder;
import repository.Menu;
import repository.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ReportService {

    public ReportService(Menu menu, Repository<RestaurantOrder> orders) {
    }

    public BigDecimal totalRevenue() {
        throw new UnsupportedOperationException();
    }

    public Map<Category, BigDecimal> salesByCategory() {
        throw new UnsupportedOperationException();
    }

    public List<MenuItem> availableItemsWithTag(DietaryTag tag) {
        throw new UnsupportedOperationException();
    }
}
