package service;

import contract.PaymentMethod;

import exception.RestaurantException;

import model.*;
import model.MenuItem;
import model.OrderStatus;

import repository.Menu;
import repository.Repository;

import java.math.BigDecimal;
import java.util.*;

// OrderService in the service package.
//OKAY -> open, add, update, remove, cancel, confirm, pay and close
//OKAY -> attention pay check everything before changing anything: a failed call changes nothing
//OKAY -> one active order per table, with a Set of the table ids
//OKAY -> the next order id continues after the biggest id loaded from orders.txt
//OKAY -> pay: the tip is chosen here, and the change comes from the payment method
//tests: second order on a busy table, unavailable item, pay before READY, pay twice
//Needs "Implement the order and the tables", "Implement the repository and the menu".

public class OrderService {

    private final Menu menu;
    private final Repository<DiningTable> tables;
    private final Repository<RestaurantOrder> orders;
    private final Set<String> activeTableIds = new HashSet<>(); // id en memoire
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
        String digitsOnly = orderId.replaceAll("[^0-9]", "");// que chiffres
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
        RestaurantOrder order = new RestaurantOrder("O " + String.format("%03d", nextOrderNumber), tableId);

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
            throw new RestaurantException("Item not available");// boolean
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

        order.moveTo(OrderStatus.CANCELLED); // changement de statut
        activeTableIds.remove(order.getTableId()); // redeviens vide
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

        if (order.getStatus() != OrderStatus.READY){
            throw new RestaurantException("Order need to be READY to pay");
        }

        order.setTipRate(tipRate);
        BigDecimal total = order.getTotal();
        BigDecimal change = method.pay(total); // cash or card
        order.moveTo(OrderStatus.PAID);

        return change;
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
            throw new RestaurantException("Order is empty");
        }
        return order.get();
    }

    public MenuItem findMenuItem(String itemId) throws RestaurantException {
        for (MenuItem item : menu) {
            if (item.getId().equals(itemId)) {
                return item;
            }
        }
        throw new RestaurantException("Menu item not found");
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
