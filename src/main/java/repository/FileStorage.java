package repository;

import contract.Identifiable;
import contract.LineParser;
import exception.RestaurantException;
import model.Beverage;
import model.Category;
import model.DietaryTag;
import model.DiningTable;
import model.FoodItem;
import model.MenuItem;
import model.OrderStatus;
import model.RestaurantOrder;
import model.RestaurantOrder.OrderLine;
import model.TipRate;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

public class FileStorage {

    private FileStorage() {
    }

    public static <T extends Identifiable> List<String> load(Path file, LineParser<T> parser, Repository<T> target)
            throws IOException {

        List<String> problems = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }
                // -1 keeps the last field even when it is empty, like an order with no item
                String[] fields = line.split(";", -1);

                try {
                    target.add(parser.parse(fields));
                } catch (RestaurantException e) {
                    problems.add(file.getFileName() + ", line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        return problems;
    }

    public static <T> void save(Path file, Collection<? extends T> items, Function<T, String> formatter)
            throws IOException {

        Files.createDirectories(file.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (T item : items) {
                writer.write(formatter.apply(item));
                writer.newLine();
            }
        }
    }

    public static MenuItem parseMenuItem(String[] fields) throws RestaurantException {
        if (fields.length != 8) {
            throw new RestaurantException("Malformed menu item line: expected 8 fields");
        }

        try {
            String id = fields[0];
            String type = fields[1];
            String name = fields[2];
            BigDecimal price = new BigDecimal(fields[3]);
            Category category = Category.valueOf(fields[4]);
            boolean available = Boolean.parseBoolean(fields[5]);

            switch (type) {
                case "FOOD" -> {
                    int preparationMinutes = Integer.parseInt(fields[6]);
                    DietaryTag dietaryTag = DietaryTag.valueOf(fields[7]);
                    return new FoodItem(id, name, price, category, available, preparationMinutes, dietaryTag);
                }
                case "DRINK" -> {
                    int volumeMl = Integer.parseInt(fields[6]);
                    boolean alcoholic = Boolean.parseBoolean(fields[7]);
                    return new Beverage(id, name, price, category, available, volumeMl, alcoholic);
                }
                default -> throw new RestaurantException("Unknown menu item type: " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new RestaurantException("Malformed menu item line: " + e.getMessage());
        }
    }

    public static DiningTable parseTable(String[] fields) throws RestaurantException {
        if (fields.length != 2) {
            throw new RestaurantException("Malformed table line: expected 2 fields");
        }

        try {
            return new DiningTable(fields[0], Integer.parseInt(fields[1]));
        } catch (NumberFormatException e) {
            throw new RestaurantException("Malformed table line: invalid capacity '" + fields[1] + "'");
        }
    }

    public static RestaurantOrder parseOrder(String[] fields, Menu menu) throws RestaurantException {
        if (fields.length != 5) {
            throw new RestaurantException("Malformed order line: expected 5 fields");
        }

        try {
            String id = fields[0];
            String tableId = fields[1];
            OrderStatus status = OrderStatus.valueOf(fields[2]);
            TipRate tipRate = TipRate.fromPercent(Integer.parseInt(fields[3]));

            return RestaurantOrder.restore(id, tableId, status, tipRate, parseLines(fields[4], menu));
        } catch (IllegalArgumentException e) {
            throw new RestaurantException("Malformed order line: " + e.getMessage());
        }
    }

    private static Map<MenuItem, Integer> parseLines(String lines, Menu menu) throws RestaurantException {
        Map<MenuItem, Integer> items = new LinkedHashMap<>();

        if (lines.isBlank()) {
            return items;
        }

        for (String line : lines.split("\\|")) {
            String[] parts = line.split("\\*");

            if (parts.length != 2) {
                throw new RestaurantException("Malformed order line: expected an item and a quantity in '" + line + "'");
            }
            Optional<MenuItem> item = menu.findById(parts[0]);

            if (item.isEmpty()) {
                throw new RestaurantException("Unknown menu item id: " + parts[0]);
            }
            items.put(item.get(), Integer.parseInt(parts[1]));
        }
        return items;
    }

    public static String format(MenuItem item) {
        if (item instanceof FoodItem food) {
            return sharedFields(item, "FOOD") + ";" + food.getPreparationMinutes() + ";" + food.getDietaryTag();
        }
        if (item instanceof Beverage beverage) {
            return sharedFields(item, "DRINK") + ";" + beverage.getVolumeMl() + ";" + beverage.isAlcoholic();
        }
        throw new IllegalStateException("Unknown menu item type: " + item.getClass());
    }

    private static String sharedFields(MenuItem item, String type) {
        return item.getId() + ";" + type + ";" + item.getName() + ";" + item.getPrice().toPlainString()
                + ";" + item.getCategory() + ";" + item.isAvailable();
    }

    public static String format(DiningTable table) {
        return table.getId() + ";" + table.getCapacity();
    }

    public static String format(RestaurantOrder order) {
        StringBuilder lines = new StringBuilder();

        for (OrderLine line : order.getLines()) {
            if (!lines.isEmpty()) {
                lines.append("|");
            }
            lines.append(line.getItem().getId()).append("*").append(line.getQuantity());
        }

        return order.getId() + ";" + order.getTableId() + ";" + order.getStatus() + ";"
                + order.getTipRate().getPercent() + ";" + lines;
    }
}
