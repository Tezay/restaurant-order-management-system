package service;

import concurrent.Cook;
import concurrent.KitchenBoard;
import exception.RestaurantException;
import model.OrderStatus;
import model.RestaurantOrder;

import java.util.ArrayList;
import java.util.List;

public class KitchenService {

    private static final int COOK_COUNT = 2;
    private final List<Thread> cooks = new ArrayList<>();
    private KitchenBoard board;
    private RestaurantOrder order;

    public void start(RestaurantOrder order) throws RestaurantException {
        if (this.order != null) {
            throw new RestaurantException("The kitchen is already preparing an order.");
        }
        order.moveTo(OrderStatus.PREPARING);

        this.order = order;
        this.board = new KitchenBoard(order.getLines());
        this.cooks.clear();

        for (int i = 1; i <= COOK_COUNT; i++) {
            Thread cook = new Thread(new Cook(this.board), "Cook " + i);
            this.cooks.add(cook);
            cook.start();
        }
    }

    public void waitUntilDone() throws RestaurantException {
        if (this.order == null) {
            return;
        }

        try {
            for (Thread cook : cooks) {
                cook.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (board.getFinishedCount() == order.getLines().size()) {
            order.moveTo(OrderStatus.READY);
        } else {
            order.moveTo(OrderStatus.CONFIRMED);
        }
        cooks.clear();
        order = null;
        board = null;
    }

    public void close() throws RestaurantException {
        for (Thread cook : cooks) {
            cook.interrupt();
        }
        waitUntilDone();
    }
}
