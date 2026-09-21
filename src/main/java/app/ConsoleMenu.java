package app;

import contract.PaymentMethod;
import exception.RestaurantException;
import model.CardPayment;
import model.CashPayment;
import model.Category;
import model.DietaryTag;
import model.MenuItem;
import model.RestaurantOrder;
import model.TipRate;
import repository.Menu;
import report.ReportService;
import service.KitchenService;
import service.OrderService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleMenu {

    private final OrderService orderService;
    private final KitchenService kitchen;
    private final ReportService reports;
    private final Menu menu;

    private final Scanner scanner = new Scanner(System.in);
    private final List<Method> options = findOptions();

    private boolean running;

    public ConsoleMenu(OrderService orderService, KitchenService kitchen, ReportService reports, Menu menu) {
        this.orderService = orderService;
        this.kitchen = kitchen;
        this.reports = reports;
        this.menu = menu;
    }

    public void run() {
        running = true;

        try {
            while (running) {
                showOptions();
                int choice = readInt("Your choice: ", 1, options.size());
                call(options.get(choice - 1));
            }
        } catch (NoSuchElementException e) {
            System.out.println("Input closed, leaving.");
        }
    }

    private static List<Method> findOptions() {
        List<Method> found = new ArrayList<>();

        for (Method method : ConsoleMenu.class.getMethods()) {
            if (method.isAnnotationPresent(MenuOption.class)) {
                found.add(method);
            }
        }
        found.sort(Comparator.comparingInt(method -> method.getAnnotation(MenuOption.class).order()));
        return found;
    }

    private void showOptions() {
        System.out.println();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + " : " + options.get(i).getAnnotation(MenuOption.class).label());
        }
    }

    private void call(Method option) {
        try {
            option.invoke(this);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoSuchElementException) {
                throw (NoSuchElementException) cause;
            }
            System.out.println(cause instanceof RestaurantException
                ? "Refused: " + cause.getMessage() : "Unexpected error: " + cause);
        } catch (IllegalAccessException e) {
            System.out.println("This option cannot be called.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private BigDecimal readAmount(String prompt) {
        while (true) {
            String text = readLine(prompt).replace(',', '.');
            try {
                BigDecimal value = new BigDecimal(text).setScale(2);
                if (value.scale() <= 2 && value.signum() > 0) {
                    return value;
                }
                if (value.signum() <= 0) {
                    System.out.println("The amount must be greater than 0.");
                } else {
                    System.out.println("Enter an amount like 20 or 20.50.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter an amount like 20 or 20.50.");
            }
        }
    }

    private String readLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Line cannot be empty.");
        }
    }

    @MenuOption(order = 1, label = "Show the menu")
    public void showMenu() {
        System.out.println("------ Menu ------");
        for (MenuItem item : menu) {
            System.out.println(item.describe());
        }
    }

    @MenuOption(order = 2, label = "Open an order")
    public void openOrder() throws RestaurantException {
        String tableId = readLine("Table id (ex: T01): ").toUpperCase();
        RestaurantOrder order = orderService.openOrder(tableId);
        System.out.println("Order " + order.getId() + " opened on table " + tableId + ".");
    }

    @MenuOption(order = 3, label = "Add an item")
    public void addItem() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();
        String itemId = readLine("Item id (ex: M001): ").toUpperCase();
        int quantity = readInt("Quantity (1-99): ", 1, 99);
        orderService.addItem(orderId, itemId, quantity);
        System.out.println("Added " + quantity + " x " + itemId + " to " + orderId + ".");
    }

    @MenuOption(order = 4, label = "Change a quantity")
    public void updateQuantity() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();
        String itemId = readLine("Item id (ex: M001): ").toUpperCase();
        int quantity = readInt("New quantity (1-99): ", 1, 99);
        orderService.updateQuantity(orderId, itemId, quantity);
        System.out.println("Quantity of " + itemId + " in " + orderId + " is now " + quantity + ".");
    }

    @MenuOption(order = 5, label = "Remove an item")
    public void removeItem() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();
        String itemId = readLine("Item id (ex: M001): ").toUpperCase();
        orderService.removeItem(orderId, itemId);
        System.out.println(itemId + " removed from " + orderId + ".");
    }

    @MenuOption(order = 6, label = "Cancel an order")
    public void cancelOrder() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();
        orderService.cancel(orderId);
        System.out.println("Order " + orderId + " cancelled.");
    }

    @MenuOption(order = 7, label = "Confirm an order")
    public void confirmOrder() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();
        orderService.confirm(orderId);
        System.out.println("Order " + orderId + " confirmed.");
    }

    @MenuOption(order = 8, label = "Send an order to the kitchen")
    public void startKitchen() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();
        kitchen.start(orderService.findOrder(orderId));
        System.out.println("Order " + orderId + " sent to the kitchen.");
    }

    @MenuOption(order = 9, label = "Wait for the kitchen")
    public void waitForKitchen() throws RestaurantException {
        System.out.println("Waiting for the cooks...");
        kitchen.waitUntilDone();
        System.out.println("The cooks have stopped.");
    }

    @MenuOption(order = 10, label = "Close the kitchen")
    public void closeKitchen() throws RestaurantException {
        kitchen.close();
        System.out.println("Kitchen closed. An unfinished order is back to CONFIRMED.");
    }

    @MenuOption(order = 11, label = "Pay an order")
    public void payOrder() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();

        TipRate[] tips = TipRate.values();
        for (int i = 0; i < tips.length; i++) {
            System.out.println((i + 1) + " = " + tips[i]);
        }
        TipRate tip = tips[readInt("Tip choice: ", 1, tips.length) - 1];

        PaymentMethod method;
        if (readInt("Payment: 1 = cash, 2 = card: ", 1, 2) == 1) {
            method = new CashPayment(readAmount("Amount given: "));
        } else {
            method = new CardPayment(readLine("Card number (16 digits): "));
        }

        BigDecimal change = orderService.pay(orderId, tip, method);
        System.out.println("Order " + orderId + " paid. Change: " + change);
    }

    @MenuOption(order = 12, label = "Close an order")
    public void closeOrder() throws RestaurantException {
        String orderId = readLine("Order id (ex: O0001): ").toUpperCase();
        orderService.close(orderId);
        System.out.println("Order " + orderId + " closed, the table is free.");
    }

    @MenuOption(order = 13, label = "Show the reports")
    public void showReports() {
        System.out.println("--- Total revenue ---");
        System.out.println(reports.totalRevenue());

        System.out.println("--- Sales by category ---");
        for (Map.Entry<Category, BigDecimal> entry : reports.salesByCategory().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        DietaryTag[] tags = DietaryTag.values();
        for (int i = 0; i < tags.length; i++) {
            System.out.println((i + 1) + " = " + tags[i]);
        }
        DietaryTag tag = tags[readInt("Dietary tag: ", 1, tags.length) - 1];

        System.out.println("--- Available items with tag " + tag + " ---");
        for (MenuItem item : reports.availableItemsWithTag(tag)) {
            System.out.println(item.describe());
        }
    }

    @MenuOption(order = 14, label = "Exit")
    public void exit() {
        running = false;
    }
}
