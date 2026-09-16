package repository;

import contract.Identifiable;
import exception.RestaurantException;

import java.util.Collection;
import java.util.Optional;

public class Repository<T extends Identifiable> {

    public void add(T item) throws RestaurantException {
        throw new UnsupportedOperationException();
    }

    public Optional<T> findById(String id) {
        throw new UnsupportedOperationException();
    }

    public Collection<T> getAll() {
        throw new UnsupportedOperationException();
    }
}
