package app;

import repository.Menu;
import report.ReportService;
import service.KitchenService;
import service.OrderService;

import java.lang.reflect.Method;
import java.util.List;

public class ConsoleMenu {

    public ConsoleMenu(OrderService orderService, KitchenService kitchen, ReportService reports, Menu menu) {
    }

    public void run() {
        throw new UnsupportedOperationException();
    }

    private List<Method> findOptions() {
        throw new UnsupportedOperationException();
    }

    private int readInt(String prompt, int min, int max) {
        throw new UnsupportedOperationException();
    }

    private String readLine(String prompt) {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 1, label = "Show the menu")
    public void showMenu() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 2, label = "Open an order")
    public void openOrder() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 3, label = "Add an item")
    public void addItem() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 4, label = "Change a quantity")
    public void updateQuantity() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 5, label = "Remove an item")
    public void removeItem() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 6, label = "Cancel an order")
    public void cancelOrder() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 7, label = "Confirm an order")
    public void confirmOrder() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 8, label = "Send an order to the kitchen")
    public void startKitchen() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 9, label = "Wait for the kitchen")
    public void waitForKitchen() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 10, label = "Close the kitchen")
    public void closeKitchen() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 11, label = "Pay an order")
    public void payOrder() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 12, label = "Close an order")
    public void closeOrder() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 13, label = "Show the reports")
    public void showReports() {
        throw new UnsupportedOperationException();
    }

    @MenuOption(order = 14, label = "Exit")
    public void exit() {
        throw new UnsupportedOperationException();
    }
}
