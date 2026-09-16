package model;

import contract.Identifiable;

public class DiningTable implements Identifiable {

    public DiningTable(String id, int capacity) {
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    public int getCapacity() {
        throw new UnsupportedOperationException();
    }
}
