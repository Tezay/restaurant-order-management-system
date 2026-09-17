package model;

import java.math.BigDecimal;

public class FoodItem extends MenuItem {
    private int preparationMinutes;
    private DietaryTag dietaryTag;

    public FoodItem(String id, String name, BigDecimal price, Category category, boolean available,
                    int preparationMinutes, DietaryTag dietaryTag) {
        super(id, name, price, category, available);
        this.preparationMinutes = preparationMinutes;
        this.dietaryTag = dietaryTag;
    }

    public DietaryTag getDietaryTag() {
        return this.dietaryTag;
    }

    public int getPreparationMinutes() {
        return this.preparationMinutes;
    }

    @Override
    public String describe() {
        return "=== Order #" + this.getId() + " ===" +
            "\n-- " + this.getName() + " --" +
            "\nPrice: " + this.getPrice() +
            "\nCategory: " + this.getCategory() +
            "\nAvailable: " + this.isAvailable() +
            "\nPreparation time: " + this.preparationMinutes +
            "\nDietary Tag: " + this.dietaryTag +
            "\n=========";
    }
}
