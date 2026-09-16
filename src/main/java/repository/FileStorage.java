package repository;

import contract.Identifiable;
import contract.LineParser;
import exception.RestaurantException;
import model.DiningTable;
import model.MenuItem;
import model.RestaurantOrder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class FileStorage {

    private FileStorage() {
    }

    public static <T extends Identifiable> List<String> load(Path file, LineParser<T> parser, Repository<T> target)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    public static <T> void save(Path file, Collection<? extends T> items, Function<T, String> formatter)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    public static MenuItem parseMenuItem(String[] fields) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public static DiningTable parseTable(String[] fields) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public static RestaurantOrder parseOrder(String[] fields, Menu menu) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public static String format(MenuItem item) {
        throw new UnsupportedOperationException();
    }

    public static String format(DiningTable table) {
        throw new UnsupportedOperationException();
    }

    public static String format(RestaurantOrder order) {
        throw new UnsupportedOperationException();
    }
}
