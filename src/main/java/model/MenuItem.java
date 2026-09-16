package model;

import contract.Identifiable;

import java.math.BigDecimal;

public abstract class MenuItem implements Identifiable, Comparable<MenuItem> {

    protected MenuItem(String id, String name, BigDecimal price, Category category, boolean available) {
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getPrice() {
        throw new UnsupportedOperationException();
    }

    public Category getCategory() {
        throw new UnsupportedOperationException();
    }

    public boolean isAvailable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(MenuItem other) {
        throw new UnsupportedOperationException();
    }

    public abstract int getPreparationMinutes();

    public abstract String describe();
}
