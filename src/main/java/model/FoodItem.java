package model;

import java.math.BigDecimal;

public class FoodItem extends MenuItem {

    public FoodItem(String id, String name, BigDecimal price, Category category, boolean available,
                    int preparationMinutes, DietaryTag dietaryTag) {
        super(id, name, price, category, available);
    }

    public DietaryTag getDietaryTag() {
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
