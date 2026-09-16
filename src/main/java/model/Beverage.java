package model;

import java.math.BigDecimal;

public class Beverage extends MenuItem {

    public Beverage(String id, String name, BigDecimal price, Category category, boolean available,
                    int volumeMl, boolean alcoholic) {
        super(id, name, price, category, available);
    }

    public boolean isAlcoholic() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPreparationMinutes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String describe() {
        throw new UnsupportedOperationException();
    }
}
