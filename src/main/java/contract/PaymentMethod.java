package contract;

import exception.RestaurantException;

import java.math.BigDecimal;

public interface PaymentMethod {
    BigDecimal pay(BigDecimal amountDue) throws RestaurantException;
}
