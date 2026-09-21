package repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Valid tests.")
    void loadsAllItemsFromAValidFile() throws IOException {
        Path file = tempDir.resolve("menu-valid.csv");
        Files.writeString(file, """
                FOOD,M1,Soup,8.50,STARTER,true,10,VEGAN
                BEVERAGE,M2,Cola,3.00,DRINK,true,0,false
                FOOD,M3,Steak,22.00,MAIN,true,20,NONE
                """);

        Menu menu = new Menu();
        List<String> problems = FileStorage.load(file, FileStorage::parseMenuItem, menu);

        assertTrue(problems.isEmpty(), "Valid file should produce no problems");
        assertEquals(3, menu.getAll().size());
        assertTrue(menu.findById("M1").isPresent());
        assertTrue(menu.findById("M2").isPresent());
        assertTrue(menu.findById("M3").isPresent());
    }

    @Test
    @DisplayName("try to catch each kind of bad lines and keep the valid ones.")
    void skipsEachKindOfBadLineAndKeepsTheValidOnes() throws IOException {
        Path file = tempDir.resolve("menu-with-errors.csv");
        Files.writeString(file, """
                FOOD,M1,Soup,8.50,STARTER,true,10,VEGAN
                FOOD,M2,BadPrice,notanumber,STARTER,true,10,VEGAN
                BEVERAGE,M3,Cola,3.00,BOGUS_CATEGORY,true,0,false
                UNKNOWNTYPE,M4,Mystery,5.00,MAIN,true,0,NONE
                FOOD,M5,TooShort
                BEVERAGE,M6,Water,2.00,DRINK,true,0,false
                """);

        Menu menu = new Menu();
        List<String> problems = FileStorage.load(file, FileStorage::parseMenuItem, menu);

        assertEquals(2, menu.getAll().size(), "Only the 2 valid lines should be loaded");
        assertTrue(menu.findById("M1").isPresent());
        assertTrue(menu.findById("M6").isPresent());

        assertEquals(4, problems.size(), "Exactly the 4 bad lines should be reported");
    }
}
