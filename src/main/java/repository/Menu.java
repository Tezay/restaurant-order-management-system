package repository;

import model.MenuItem;

import java.util.Iterator;

public class Menu extends Repository<MenuItem> implements Iterable<MenuItem> {

    @Override
    public Iterator<MenuItem> iterator() {
        throw new UnsupportedOperationException();
    }
}
