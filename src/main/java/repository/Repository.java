package repository;

import contract.Identifiable;
import exception.RestaurantException;

import java.util.*;

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
        // get() would only return null if no key is absent
        // ofNullable return an empty Optional instead of null
        return Optional.ofNullable(items.get(id));
    }

    public Collection<T> getAll() {
        // read only
        return Collections.unmodifiableCollection(items.values());
    }
}
