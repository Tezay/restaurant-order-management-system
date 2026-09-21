package repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("A valid file loads every line")
    void loadsAllItemsFromAValidFile() throws IOException {
        Path file = tempDir.resolve("menu_items.txt");
        Files.writeString(file, """
                M001;FOOD;Classic poutine;12.95;MAIN;true;8;NONE
                M002;FOOD;Green salad;7.90;STARTER;false;3;VEGAN
                M003;DRINK;Spruce beer;3.95;DRINK;true;355;false
                """);

        Menu menu = new Menu();
        List<String> problems = FileStorage.load(file, FileStorage::parseMenuItem, menu);

        assertTrue(problems.isEmpty());
        assertEquals(3, menu.getAll().size());
    }

    @Test
    @DisplayName("Each kind of bad line is skipped and reported")
    void skipsEachKindOfBadLineAndKeepsTheValidOnes() throws IOException {
        Path file = tempDir.resolve("menu_items.txt");
        Files.writeString(file, """
                M001;FOOD;Classic poutine;12.95;MAIN;true;8;NONE
                M002;FOOD;Bad price;price;MAIN;true;8;NONE
                M003;FOOD;Bad category;12.95;BOGUS;true;8;NONE
                M004;PIZZA;Unknown type;12.95;MAIN;true;8;NONE
                M005;FOOD;Too short;12.95;MAIN;true
                M006;DRINK;Spruce beer;3.95;DRINK;true;355;false
                """);

        Menu menu = new Menu();
        List<String> problems = FileStorage.load(file, FileStorage::parseMenuItem, menu);

        assertEquals(2, menu.getAll().size());
        assertEquals(4, problems.size());
        assertTrue(menu.findById("M001").isPresent());
        assertTrue(menu.findById("M006").isPresent());
    }

    @Test
    @DisplayName("Saved items are read back the same way")
    void savesAndLoadsTheSameItems() throws IOException {
        Path source = tempDir.resolve("menu_items.txt");
        Files.writeString(source, """
                M001;FOOD;Classic poutine;12.95;MAIN;true;8;NONE
                M003;DRINK;Spruce beer;3.95;DRINK;true;355;false
                """);
        Menu menu = new Menu();
        FileStorage.load(source, FileStorage::parseMenuItem, menu);

        Path copy = tempDir.resolve("runtime/menu_items.txt");
        FileStorage.save(copy, menu.getAll(), FileStorage::format);

        Menu reloaded = new Menu();
        List<String> problems = FileStorage.load(copy, FileStorage::parseMenuItem, reloaded);

        assertTrue(problems.isEmpty());
        assertEquals(Files.readString(source), Files.readString(copy));
        assertEquals(2, reloaded.getAll().size());
    }
}
