package model;

import java.math.BigDecimal;

public class Beverage extends MenuItem {
    private int volumeMl;
    private boolean alcoholic;

    public Beverage(String id, String name, BigDecimal price, Category category, boolean available,
                    int volumeMl, boolean alcoholic) {
        super(id, name, price, category, available);
        this.volumeMl = volumeMl;
        this.alcoholic = alcoholic;
    }

    public int getVolumeMl() {
        return this.volumeMl;
    }

    /**
     * Beverage has no preparation time.
     * @return always returns {@code 0}
     */
    @Override
    public int getPreparationMinutes() {
        return 0;
    }

    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    @Override
    public String describe() {
        return "=== Item #" + this.getId() + " ===" +
            "\n-- " + this.getName() + " --" +
            "\nPrice: " + this.getPrice() +
            "\nCategory: " + this.getCategory() +
            "\nAvailable: " + this.isAvailable() +
            "\nAlcohol: " + this.alcoholic +
            "\nVolume (ml): " + this.volumeMl +
            "\n=========";
    }
}
