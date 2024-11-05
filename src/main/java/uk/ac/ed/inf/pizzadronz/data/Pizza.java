package uk.ac.ed.inf.pizzadronz.data;

import uk.ac.ed.inf.pizzadronz.constants.OrderValidationCode;

public record Pizza(
        String name,
        Integer priceInPence
){
    public OrderValidationCode checkPizza() {
        if (priceInPence < 0) {
            return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
        }
        return OrderValidationCode.NO_ERROR;

    }
}
