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

    /**
     * Compare the current object with another one and sort by id ascending.
     * @param other the object to be compared.
     * @return a negative integer, zero or a positive integer if the current id is
     * respectively less than, equal to or greater than the other Object's id.
     */
    @Override
    public int compareTo(MenuItem other) {
        return this.id.compareTo(other.id);
    }

    public abstract String describe();
}
