package model;

import contract.Identifiable;
import exception.RestaurantException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantOrder implements Identifiable {
    private String id;
    private String tableId;
    private OrderStatus status;
    private TipRate tipRate;
    private List<OrderLine> lines = new ArrayList<>();

    private static final BigDecimal TAX_RATE = new BigDecimal("0.14975");

    public RestaurantOrder(String id, String tableId) {
        this.id = id;
        this.tableId = tableId;
        this.status = OrderStatus.OPEN;
        this.tipRate = TipRate.NO_TIP;
    }

    /**
     * Allow to rebuild a {@code RestaurantOrder} object from existing data instead of recreating
     * a new one.
     * @param id the order's unique id.
     * @param tableId the table's unique id.
     * @param status the current status of the order.
     * @param tipRate the tipRate applied to the order.
     * @param items the map of menu items and a quantity given.
     * @return a {@code RestaurantOrder} object with all the attributes given.
     * @throws RestaurantException if the id or the tableId are null or blank.
     */
    public static RestaurantOrder restore(String id, String tableId, OrderStatus status, TipRate tipRate,
                                          Map<MenuItem, Integer> items) throws RestaurantException {
        if(id == null || id.isBlank() || tableId == null || tableId.isBlank() || status == null || items == null){
            throw new RestaurantException("Invalid data for given order.");
        }
        RestaurantOrder order = new RestaurantOrder(id,tableId);
        order.status = status;
        order.setTipRate(tipRate);
        for(Map.Entry<MenuItem, Integer> entry : items.entrySet()){
            order.lines.add(new OrderLine(entry.getKey(), entry.getValue()));
        }
        return order;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTableId() {
        return tableId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return new ArrayList<>(lines);
    }

    public void addItem(MenuItem item, int quantity) throws RestaurantException {
        if(item == null || quantity < 1){
            throw new RestaurantException("Invalid Item data.");
        }
        if(status != OrderStatus.OPEN){
            throw new RestaurantException("Order must be OPEN to add items.");
        }
        if(!item.isAvailable()){
            throw new RestaurantException("Item not available.");
        }
        OrderLine exist = findItemById(item.getId());
        if(exist != null){
            exist.quantity += quantity;
        } else {
            lines.add(new OrderLine(item, quantity));
        }
    }

    private OrderLine findItemById(String itemId){
        OrderLine exist = null;
        for(OrderLine line : lines){
            if(line.getItem().getId().equals(itemId)){
                exist = line;
                break;
            }
        }
        return exist;
    }

    public void updateQuantity(String itemId, int quantity) throws RestaurantException {
        if(status != OrderStatus.OPEN){
            throw new RestaurantException("Order must be OPEN to update items.");
        }
        OrderLine exist = findItemById(itemId);
        if(exist == null || quantity < 1){
            throw new RestaurantException("Invalid Item data.");
        }
        exist.quantity = quantity;
    }

    public void removeItem(String itemId) throws RestaurantException {
        if(status != OrderStatus.OPEN){
            throw new RestaurantException("Order must be OPEN to remove items.");
        }
        OrderLine exist = findItemById(itemId);
        if(exist == null){
            throw new RestaurantException("Invalid Item Id.");
        }
        lines.remove(exist);
    }

    public void moveTo(OrderStatus next) throws RestaurantException {
        if(!status.canMoveTo(next) || next == null){
            throw new RestaurantException("Invalid transition.");
        }
        status = next;
    }

    public TipRate getTipRate(){return tipRate;}

    public void setTipRate(TipRate tipRate) {
        this.tipRate = tipRate == null ? TipRate.NO_TIP : tipRate;
    }

    public BigDecimal getSubtotal() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for(OrderLine line : lines){
            subTotal = subTotal.add(line.getAmount());
        }
        return subTotal;
    }

    public BigDecimal getTax() {
        return getSubtotal().multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTip() {
        return tipRate.applyTo(getSubtotal());
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getTax()).add(getTip());
    }

    public static class OrderLine {
        private MenuItem item;
        private int quantity;

        public OrderLine(MenuItem item, int quantity){
            this.item = item;
            this.quantity = quantity;
        }

        public MenuItem getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getAmount() {
            return item.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }
}
