package uk.ac.ed.inf.pizzadronz.ServiceInterfaces;
import uk.ac.ed.inf.pizzadronz.Data.Order;
import uk.ac.ed.inf.pizzadronz.Data.OrderValidationResult;
import uk.ac.ed.inf.pizzadronz.Data.Restaurant;

import java.time.LocalDate;


public interface OrderValidation {
    Restaurant restaurant = null;
    LocalDate date = null;
    /**
     * interface to validate an order
     */
    /**
     * validate an order and deliver a validated version where the
     * OrderStatus and OrderValidationCode are set accordingly.
     *
     * The order validation code is defined in the enum @link uk.ac.ed.inf.ilp.constant.OrderValidationStatus
     *
     * <p>
     * Fields to validate include (among others - for details please see the OrderValidationStatus):
     * <p>
     * number (16 digit numeric)
     * CVV
     * expiration date
     * the menu items selected in the order
     * the involved restaurants
     * if the maximum count is exceeded
     * if the order is valid on the given date for the involved restaurants (opening days)
     *
     * @param orderToValidate    is the order which needs validation
     * @param definedRestaurants is the vector of defined restaurants with their according menu structure
     * @return the validated order
     */
    OrderValidationResult validateOrder(Order orderToValidate, Restaurant[] definedRestaurants);

    /**
     * Validates an order and if it has no errors ,returns restaurant for the order
     *
     * @param order       is the order to be validated and find the restaurant for
     * @param restaurants is the list of restaurants to search in
     * @return the restaurant for the order
     */
    Restaurant validateAndGetRestaurant(Order order, Restaurant[] restaurants);
}
