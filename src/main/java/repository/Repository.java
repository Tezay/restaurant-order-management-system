package repository;

import contract.Identifiable;
import exception.RestaurantException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class Repository<T extends Identifiable> {
    private final Map<String, T> items = new TreeMap<>();

    public void add(T item) throws RestaurantException {
        if (item == null){
            throw new RestaurantException("Item cannot be null.");
        }
        String id = item.getId();
        if(id == null || id.isEmpty()){
            throw new RestaurantException("Item's id cannot be empty.");
        }
        if(items.containsKey(item.getId())){
            throw new RestaurantException("Item's id already exists.");
        }
        items.put(item.getId(),item);
    }

    public Optional<T> findById(String id) {
        if(id == null){
            return Optional.empty();
        }
        return Optional.ofNullable(items.get(id));
    }

    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(items.values());
    }
}
