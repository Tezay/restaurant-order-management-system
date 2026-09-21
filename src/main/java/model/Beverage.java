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

    @Override
    public int getPreparationMinutes() {
        return 0;
    }

    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    @Override
    protected String details() {
        if (this.alcoholic) {
            return String.format("%3d ml  alcohol", this.volumeMl);
        }
        return String.format("%3d ml", this.volumeMl);
    }
}
