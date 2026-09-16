package model;

import contract.Identifiable;
import exception.RestaurantException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RestaurantOrder implements Identifiable {

    public RestaurantOrder(String id, String tableId) {
    }

    public static RestaurantOrder restore(String id, String tableId, OrderStatus status, TipRate tipRate,
                                          Map<MenuItem, Integer> items) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    public String getTableId() {
        throw new UnsupportedOperationException();
    }

    public OrderStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    public List<OrderLine> getLines() {
        throw new UnsupportedOperationException();
    }

    public void addItem(MenuItem item, int quantity) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void updateQuantity(String itemId, int quantity) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void removeItem(String itemId) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void moveTo(OrderStatus next) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public void setTipRate(TipRate tipRate) {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getSubtotal() {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getTax() {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getTip() {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getTotal() {
        throw new UnsupportedOperationException();
    }

    public static class OrderLine {

        public MenuItem getItem() {
            throw new UnsupportedOperationException();
        }

        public int getQuantity() {
            throw new UnsupportedOperationException();
        }

        public BigDecimal getAmount() {
            throw new UnsupportedOperationException();
        }
    }
}
