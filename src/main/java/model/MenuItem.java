package model;

import contract.Identifiable;

import java.math.BigDecimal;

public abstract class MenuItem implements Identifiable, Comparable<MenuItem> {
    private String id;
    private String name;
    private BigDecimal price;
    private Category category;
    private boolean available;

    protected MenuItem(String id, String name, BigDecimal price, Category category, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public int compareTo(MenuItem other) {
        return this.id.compareTo(other.id);
    }

    public abstract int getPreparationMinutes();

    /**
     * @return one line of the menu: id, name, price, category, then the part that
     *         depends on the type of item
     */
    public String describe() {
        String line = String.format("%-5s %-30s %8s %-9s %s",
                this.id, this.name, this.price, this.category, details());

        if (this.available) {
            return line;
        }
        return line + "   (not available)";
    }

    protected abstract String details();
}
