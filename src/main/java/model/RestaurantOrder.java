package model;

import contract.Identifiable;
import exception.RestaurantException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantOrder implements Identifiable {
    private String id;
    private String tableId;
    private OrderStatus status;
    private TipRate tipRate;
    private Map<MenuItem, Integer> items = new HashMap<>();

    private static final BigDecimal GST_RATE = new BigDecimal("0.05"); //TPS (federale tax) : 5%
    private static final BigDecimal QST_RATE = new BigDecimal("0.09975"); //TVQ (provincial tax) : 9.975%

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
        order.tipRate = tipRate;
        order.items = items;
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
        List<OrderLine> orderLines = new ArrayList<>();
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()){
            orderLines.add(new OrderLine(entry.getKey(),entry.getValue()));
        }
        return orderLines;
    }

    public void addItem(MenuItem item, int quantity) throws RestaurantException {
        if(item == null || quantity < 1){
            throw new RestaurantException("Invalid Item data.");
        }
        if(!item.isAvailable()){
            throw new RestaurantException("Item not available.");
        }
        items.put(item,quantity);
    }

    public MenuItem findItemById(String itemId){
        MenuItem exist = null;
        for(MenuItem item : items.keySet()){
            if(item.getId().equals(itemId)){
                exist = item;
                break;
            }
        }
        return exist;
    }

    public void updateQuantity(String itemId, int quantity) throws RestaurantException {
        MenuItem exist = findItemById(itemId);
        if(exist == null || quantity < 1){
            throw new RestaurantException("Invalid Item data.");
        }
        items.put(exist,quantity);
    }

    public void removeItem(String itemId) throws RestaurantException {
        MenuItem exist = findItemById(itemId);
        if(exist == null){
            throw new RestaurantException("Invalid Item Id.");
        }
        items.remove(exist);
    }

    public void moveTo(OrderStatus next) throws RestaurantException {
        if(!status.canMoveTo(next) || next == null){
            throw new RestaurantException("Invalid transition.");
        }
        status = next;
    }

    public void setTipRate(TipRate tipRate) { //add throws RestaurantException if null is not considered as NO_TIP
        this.tipRate = tipRate;
    }

    public BigDecimal getSubtotal() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for(Map.Entry<MenuItem,Integer> entry : items.entrySet()){
            MenuItem item = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal lineAmount = item.getPrice().multiply(BigDecimal.valueOf(quantity));
            subTotal = subTotal.add(lineAmount);
        }
        return subTotal;
    }

    public BigDecimal getTax() {
        BigDecimal subTotal = getSubtotal();
        BigDecimal gst = subTotal.multiply(GST_RATE);
        BigDecimal qst = subTotal.add(gst).multiply(QST_RATE);
        return gst.add(qst);
    }

    public BigDecimal getTip() {
        if(tipRate == null){
            return BigDecimal.ZERO;
        }
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
