package service;

import contract.PaymentMethod;
import exception.RestaurantException;
import model.DiningTable;
import model.RestaurantOrder;
import model.TipRate;
import repository.Menu;
import repository.Repository;

import java.math.BigDecimal;
import java.util.List;

public class OrderService {

    public OrderService(Menu menu, Repository<DiningTable> tables, Repository<RestaurantOrder> orders) {
    }

    public RestaurantOrder openOrder(String tableId) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void addItem(String orderId, String itemId, int quantity) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void updateQuantity(String orderId, String itemId, int quantity) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void removeItem(String orderId, String itemId) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void cancel(String orderId) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void confirm(String orderId) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal pay(String orderId, TipRate tipRate, PaymentMethod method) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void close(String orderId) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public RestaurantOrder findOrder(String orderId) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public List<RestaurantOrder> getActiveOrders() {
        throw new UnsupportedOperationException();
    }
}
