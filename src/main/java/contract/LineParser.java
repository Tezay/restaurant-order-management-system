package contract;

import exception.RestaurantException;

public interface LineParser<T> {

    T parse(String[] fields) throws RestaurantException;
}
