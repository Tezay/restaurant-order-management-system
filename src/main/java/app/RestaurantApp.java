package app;

import model.DiningTable;
import model.RestaurantOrder;
import report.ReportService;
import repository.FileStorage;
import repository.Menu;
import repository.Repository;
import service.KitchenService;
import service.OrderService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class RestaurantApp {

    private static final Path SEED_DIR = Path.of("data", "seed");
    private static final Path RUNTIME_DIR = Path.of("data", "runtime");

    public static void main(String[] args) {
        Menu menu = new Menu();
        Repository<DiningTable> tables = new Repository<>();
        Repository<RestaurantOrder> orders = new Repository<>();

        try {
            load(menu, tables, orders);

            OrderService orderService = new OrderService(menu, tables, orders);
            KitchenService kitchen = new KitchenService();
            ReportService reports = new ReportService(menu, orders);

            new ConsoleMenu(orderService, kitchen, reports, menu).run();

            save(menu, tables, orders);
        } catch (IOException e) {
            System.out.println("Could not read or write the data files: " + e.getMessage());
        }
    }

    private static void load(Menu menu, Repository<DiningTable> tables, Repository<RestaurantOrder> orders) throws IOException {

        report(FileStorage.load(SEED_DIR.resolve("menu_items.txt"), FileStorage::parseMenuItem, menu));
        report(FileStorage.load(SEED_DIR.resolve("tables.txt"), FileStorage::parseTable, tables));
        report(FileStorage.load(SEED_DIR.resolve("orders.txt"), fields -> FileStorage.parseOrder(fields, menu), orders));
    }

    private static void report(List<String> problems) {
        if (problems.isEmpty()) {
            return;
        }
        System.out.println(problems.size() + " line(s) were skipped:");
        for (String problem : problems) {
            System.out.println("  " + problem);
        }
    }

    private static void save(Menu menu, Repository<DiningTable> tables, Repository<RestaurantOrder> orders)
            throws IOException {

        FileStorage.save(RUNTIME_DIR.resolve("menu_items.txt"), menu.getAll(), FileStorage::format);
        FileStorage.save(RUNTIME_DIR.resolve("tables.txt"), tables.getAll(), FileStorage::format);
        FileStorage.save(RUNTIME_DIR.resolve("orders.txt"), orders.getAll(), FileStorage::format);
        System.out.println("Data saved in " + RUNTIME_DIR);
    }
}
