package ordering;

import model.MenuItem;
import java.util.Comparator;

public class MenuItemComparators {

    private MenuItemComparators() {
    }

    public static Comparator<MenuItem> byPrice() {
        return new Comparator<MenuItem>() {
            @Override
            public int compare(MenuItem first, MenuItem second) {
                int result = first.getPrice().compareTo(second.getPrice());
                if (result == 0) {
                    result = first.getId().compareTo(second.getId());
                }
                return result;
            }
        };
    }

    public static Comparator<MenuItem> byName() {
        return new Comparator<MenuItem>() {
            @Override
            public int compare(MenuItem first, MenuItem second) {
                int result = first.getName().compareTo(second.getName());
                if (result == 0) {
                    result = first.getId().compareTo(second.getId());
                }
                return result;
            }
        };
    }
}
