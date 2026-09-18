package model;

import contract.Identifiable;

public class DiningTable implements Identifiable {
    private String id;
    private int capacity;

    public DiningTable(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public int getCapacity() {
        return this.capacity;
    }
}
