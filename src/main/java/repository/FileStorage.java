package repository;

import contract.Identifiable;
import contract.LineParser;
import exception.RestaurantException;
import model.*;
import model.RestaurantOrder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class FileStorage {

    private FileStorage() {
    }

    public static <T extends Identifiable> List<String> load(Path file, LineParser<T> parser, Repository<T> target)
            throws IOException {

        List<String> problems = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file)){
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",");

                try {
                    T item = parser.parse(fields);
                    target.add(item);
                } catch (RestaurantException e) {
                    problems.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            problems.add("Could not read file "+file+": "+ e.getMessage());
        }
        return problems;
    }

    public static <T> void save(Path file, Collection<? extends T> items, Function<T, String> formatter)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (T item : items) {
                String line = formatter.apply(item);
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static MenuItem parseMenuItem(String[] fields) throws RestaurantException {
        if (fields.length < 6) {
            throw new RestaurantException("Malformed menu item line: expected at least 6 fields");
        }

        try {
            String type = fields[0];
            String id = fields[1];
            String name = fields[2];
            BigDecimal price = new BigDecimal(fields[3]);
            Category category = Category.valueOf(fields[4]);
            boolean available = Boolean.parseBoolean(fields[5]);

            switch (type) {
                case "FOOD" -> {
                    int preparationMinutes = Integer.parseInt(fields[6]);
                    DietaryTag dietaryTag = DietaryTag.valueOf(fields[7]);
                    return new FoodItem(id, name, price, category, available,
                        preparationMinutes, dietaryTag);
                }
                case "BEVERAGE" -> {
                    int volumeMl = Integer.parseInt(fields[6]);
                    boolean alcoholic = Boolean.parseBoolean(fields[7]);
                    return new Beverage(id, name, price, category, available,
                        volumeMl, alcoholic);
                }
                default -> throw new RestaurantException("Unknown menu item type: " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new RestaurantException("Malformed menu item line: " + e.getMessage());
        }
    }

    public static DiningTable parseTable(String[] fields) throws RestaurantException {
        if (fields.length < 2) {
            throw new RestaurantException("Malformed table line: expected 2 fields");
        }

        String id = fields[0];

        try {
            int capacity = Integer.parseInt(fields[1]);
            return new DiningTable(id, capacity);
        } catch (NumberFormatException e) {
            throw new RestaurantException("Malformed table line: invalid capacity '" + fields[1] + "'");
        }
    }

    public static RestaurantOrder parseOrder(String[] fields, Menu menu) throws RestaurantException {
        if (fields.length < 4 || (fields.length - 4) % 2 != 0) {
            throw new RestaurantException("Malformed order line: expected 4 fields plus item/quantity pairs");
        }

        try {
            String id = fields[0];
            String tableId = fields[1];
            OrderStatus status = OrderStatus.valueOf(fields[2]);
            TipRate tipRate = TipRate.valueOf(fields[3]);

            Map<MenuItem, Integer> items = new LinkedHashMap<>();
            for (int i = 4; i < fields.length; i += 2) {
                String itemId = fields[i];
                int quantity = Integer.parseInt(fields[i + 1]);

                MenuItem item = menu.findById(itemId)
                    .orElseThrow(() -> new RestaurantException("Unknown menu item id: " + itemId));

                items.put(item, quantity);
            }

            return RestaurantOrder.restore(id, tableId, status, tipRate, items);
        } catch (IllegalArgumentException e) {
            throw new RestaurantException("Malformed order line: " + e.getMessage());
        }
    }

    public static String format(MenuItem item) {

        String shared = String.join(",",
            item.getId(),
            item.getName(),
            item.getPrice().toPlainString(),
            item.getCategory().name(),
            String.valueOf(item.isAvailable()));

        if (item instanceof FoodItem food) {
            return "FOOD," + shared + "," + food.getPreparationMinutes() + "," + food.getDietaryTag();
        } else if (item instanceof Beverage beverage) {
            return "BEVERAGE," + shared + "," + beverage.getVolumeMl() + "," + beverage.isAlcoholic();
        } else {
            throw new IllegalStateException("Unknown MenuItem subtype: " + item.getClass());
        }
    }

    public static String format(DiningTable table) {

        return table.getId() + "," + table.getCapacity();

    }

    public static String format(RestaurantOrder order) {
        StringBuilder sb = new StringBuilder();
        sb.append(order.getId()).append(",")
            .append(order.getTableId()).append(",")
            .append(order.getStatus().name()).append(",")
            .append(order.getTipRate().name());

        for (RestaurantOrder.OrderLine line : order.getLines()) {
            sb.append(",").append(line.getItem().getId())
                .append(",").append(line.getQuantity());
        }

        return sb.toString();
    }
}
