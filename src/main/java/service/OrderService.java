package service;

import contract.PaymentMethod;
import exception.RestaurantException;
import model.DiningTable;
import model.MenuItem;
import model.OrderStatus;
import model.RestaurantOrder;
import model.TipRate;
import repository.Menu;
import repository.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OrderService {

    private final Menu menu;
    private final Repository<DiningTable> tables;
    private final Repository<RestaurantOrder> orders;
    private final Set<String> activeTableIds = new HashSet<>();
    private int nextOrderNumber;

    public OrderService(Menu menu, Repository<DiningTable> tables, Repository<RestaurantOrder> orders) {
        this.menu = menu;
        this.tables = tables;
        this.orders = orders;
        this.nextOrderNumber = computeNextOrderNumber();
    }

    private int computeNextOrderNumber() {
        int highest = 0;
        for (RestaurantOrder order : orders.getAll()) {
            int number = extractNumber(order.getId());
            if (number > highest) {
                highest = number;
            }
        }
        return highest + 1;
    }

    private int extractNumber(String orderId) {
        String digitsOnly = orderId.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(digitsOnly);
    }

    public RestaurantOrder openOrder(String tableId) throws RestaurantException {
        if (tables.findById(tableId).isEmpty()) {
            throw new RestaurantException("Table not found");
        }
        if (activeTableIds.contains(tableId)) {
            throw new RestaurantException("Table busy");
        }
        RestaurantOrder order = new RestaurantOrder(String.format("O%04d", nextOrderNumber), tableId);

        orders.add(order);
        activeTableIds.add(tableId);
        nextOrderNumber++;
        return order;
    }

    public void addItem(String orderId, String itemId, int quantity) throws RestaurantException {
        RestaurantOrder order = findOrder(orderId);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new RestaurantException("Order not OPEN");
        }
        if (quantity <= 0) {
            throw new RestaurantException("You can not have zero item");
        }
        MenuItem item = findMenuItem(itemId);
        if (!item.isAvailable()) {
            throw new RestaurantException("Item not available");
        }
        order.addItem(item, quantity);
    }

    public void updateQuantity(String orderId, String itemId, int quantity) throws RestaurantException {
        RestaurantOrder order = findOrder(orderId);

        if (order.getStatus()!= OrderStatus.OPEN){
            throw new RestaurantException("Order not OPEN");
        }
        if (quantity<=0){
            throw new RestaurantException("You can not have zero item");
        }
        order.updateQuantity(itemId,quantity);
    }

    public void removeItem(String orderId, String itemId) throws RestaurantException {
        RestaurantOrder order = findOrder(orderId);

        if (order.getStatus()!= OrderStatus.OPEN){
            throw new RestaurantException("Order not OPEN");
        }
        order.removeItem(itemId);
    }

    public void cancel(String orderId) throws RestaurantException {
        RestaurantOrder order = findOrder(orderId);

        if (order.getStatus()!= OrderStatus.OPEN) {
            throw new RestaurantException("Only OPEN order can be cancelled");
        }

        order.moveTo(OrderStatus.CANCELLED);
        activeTableIds.remove(order.getTableId());
    }

    public void confirm(String orderId) throws RestaurantException {
        RestaurantOrder order = findOrder(orderId);

        if (order.getStatus() != OrderStatus.OPEN){
            throw new RestaurantException("Only an OPEN order can be confirmed");
        }
        if (order.getLines().isEmpty()){
            throw new RestaurantException("We can not confirm an order without items");
        }
        order.moveTo(OrderStatus.CONFIRMED);
    }

    public BigDecimal pay(String orderId, TipRate tipRate, PaymentMethod method) throws RestaurantException {
        RestaurantOrder order = findOrder(orderId);

        if (order.getStatus() != OrderStatus.READY) {
            throw new RestaurantException("Order need to be READY to pay");
        }

        order.setTipRate(tipRate);
        BigDecimal total = order.getTotal();

        try {
            BigDecimal change = method.pay(total);
            order.moveTo(OrderStatus.PAID);
            return change;

        } catch (RestaurantException e) {
            order.setTipRate(TipRate.NO_TIP);
            throw e;
        }
    }

    public void close(String orderId) throws RestaurantException {
        RestaurantOrder order = findOrder(orderId);

        if (order.getStatus() != OrderStatus.PAID){
            throw new RestaurantException("Order need to be PAID to close");
        }
        order.moveTo(OrderStatus.CLOSED);
        activeTableIds.remove(order.getTableId());
    }

    public RestaurantOrder findOrder(String orderId) throws RestaurantException {
        Optional<RestaurantOrder> order = orders.findById(orderId);

        if (order.isEmpty()) {
            throw new RestaurantException("Order not found");
        }
        return order.get();
    }

    private MenuItem findMenuItem(String itemId) throws RestaurantException {
        Optional<MenuItem> item = menu.findById(itemId);

        if (item.isEmpty()) {
            throw new RestaurantException("Menu item not found");
        }
        return item.get();
    }

    public List<RestaurantOrder> getActiveOrders() {
        List<RestaurantOrder> active = new ArrayList<>();
        for (RestaurantOrder order : orders.getAll()){
            if(order.getStatus().isActive()){
                active.add(order);
            }
        }
        return active;
    }
}
