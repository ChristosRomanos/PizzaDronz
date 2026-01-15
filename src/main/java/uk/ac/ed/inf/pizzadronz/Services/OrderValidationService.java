package uk.ac.ed.inf.pizzadronz.Services;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.OrderValidation;
import uk.ac.ed.inf.pizzadronz.Constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.Constants.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;

@Service
public class OrderValidationService implements OrderValidation {
    private LocalDate date;
    private Restaurant restaurant;

    @Override
    public OrderValidationResult validateOrder(@NotNull Order order, Restaurant[] definedRestaurants) {
        date= LocalDate.now();
        if (definedRestaurants==null || definedRestaurants.length==0){
            throw new IllegalArgumentException("No restaurants defined");
        }

        OrderValidationCode validationCode=checkOrderDate(order.getOrderDate());
        if(invalidCode(validationCode, order)){
            return new OrderValidationResult(order.getOrderStatus(),order.getOrderValidationCode());
        }

        validationCode=checkCreditCardInformation(order.getCreditCardInformation());
        if(invalidCode(validationCode, order)){
            return new OrderValidationResult(order.getOrderStatus(),order.getOrderValidationCode());
        }

       validationCode=checkPizzasInOrder(order.getPizzasInOrder(),definedRestaurants);
        if(invalidCode(validationCode, order)){
            return new OrderValidationResult(order.getOrderStatus(),order.getOrderValidationCode());
        }

        validationCode=checkIfRestaurantIsOpen(restaurant,order.getOrderDate());
        if(invalidCode(validationCode, order)){
            return new OrderValidationResult(order.getOrderStatus(),order.getOrderValidationCode());
        }

        validationCode=checkPriceTotalInPenceAndPizzas(order.getPriceTotalInPence(), order.getPizzasInOrder(),definedRestaurants);
        if(invalidCode(validationCode, order)){
            return new OrderValidationResult(order.getOrderStatus(),order.getOrderValidationCode());
        }

        order.setOrderStatus(OrderStatus.VALID);
        order.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return new OrderValidationResult(order.getOrderStatus(),order.getOrderValidationCode());
    }


    /**
     * Check if the order date is before the current date
     * @param orderDate the date of the order
     * @return OrderValidationCode
     */
    private OrderValidationCode checkOrderDate(@NotNull LocalDate orderDate) {
        if (orderDate.isBefore(date)) {
            return OrderValidationCode.UNDEFINED;
        }
        return OrderValidationCode.NO_ERROR;
    }

    /**
     * Check if the restaurant is open on the given date
     * @param restaurant the restaurant
     * @param date the date
     * @return OrderValidationCode
     */
    private OrderValidationCode checkIfRestaurantIsOpen(@NotNull Restaurant restaurant, LocalDate date) {
        for (DayOfWeek day : restaurant.openingDays()) {
            if (date.get(ChronoField.DAY_OF_WEEK)==day.getValue()) {
                return OrderValidationCode.NO_ERROR;
            }
        }
        return OrderValidationCode.RESTAURANT_CLOSED;
    }

    /**
     * Check if the number of pizzas in the order are valid and if the restaurant exists
     * @param pizzas the pizzas in the order
     * @param definedRestaurants the defined restaurants
     * @return OrderValidationCode
     */
    private OrderValidationCode checkPizzasInOrder(@NotNull Pizza[] pizzas, Restaurant[] definedRestaurants) {
        if(pizzas.length==0){
            return OrderValidationCode.EMPTY_ORDER;
        }
        if (pizzas.length > SystemConstants.MAX_PIZZA_COUNT) {
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        }else {
            return checkIfRestaurantExists(pizzas[0], definedRestaurants);
        }
    }

    /**
     * Check if the restaurant exists and sets the restaurant of the order
     * @param pizza the pizza
     * @param definedRestaurants the defined restaurants
     * @return OrderValidationCode
     */
    private OrderValidationCode checkIfRestaurantExists(@NotNull Pizza pizza, Restaurant[] definedRestaurants) {
        for (Restaurant restaurant : definedRestaurants) {
            for (Pizza rPizza : restaurant.menu()) {
                if (pizza.name().equals(rPizza.name())) {
                    this.restaurant = restaurant;
                    return OrderValidationCode.NO_ERROR;
                }
            }
        }
        return OrderValidationCode.PIZZA_NOT_DEFINED;
    }

    /**
     * Check if the price total of the order is correct
     * @param priceTotalInPence the total price in pence
     * @param pizzasInOrder the pizzas in the order
     * @param definedRestaurants the defined restaurants
     * @return OrderValidationCode
     */
    private OrderValidationCode checkPriceTotalInPenceAndPizzas(Integer priceTotalInPence, Pizza[] pizzasInOrder,Restaurant[] definedRestaurants) {
        if (priceTotalInPence < SystemConstants.DELIVERY_FEE) {
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        int sum = SystemConstants.DELIVERY_FEE;
        for (Pizza pizza : pizzasInOrder) {
            OrderValidationCode valid=checkPizza(pizza,definedRestaurants);
            if (valid == OrderValidationCode.NO_ERROR) {
                sum += pizza.priceInPence();
            } else {
                return valid;
            }
        }
        if (sum != priceTotalInPence) {
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        return OrderValidationCode.NO_ERROR;

    }

    /**
     * Check if the credit card information is valid
     * @param creditCardInformation the credit card information
     * @return OrderValidationCode
     */
    public OrderValidationCode checkCreditCardInformation(@NotNull CreditCardInformation creditCardInformation) {
            // check if the card number is valid
            if (creditCardInformation.creditCardNumber().length() != SystemConstants.CREDIT_CARD_NUMBER_LENGTH) {
                return OrderValidationCode.CARD_NUMBER_INVALID;
            }try {
                Long.parseLong(creditCardInformation.creditCardNumber());
            } catch (NumberFormatException e) {
                return OrderValidationCode.CARD_NUMBER_INVALID;
            }

            // check if the cvv is valid
            if (creditCardInformation.cvv().length() != SystemConstants.CVV_LENGTH) {
                return OrderValidationCode.CVV_INVALID;
            }
            try {
                Integer.parseInt(creditCardInformation.cvv());

            } catch (NumberFormatException e) {
                return OrderValidationCode.CVV_INVALID;
            }

            // check if the expiry date is valid
            if (creditCardInformation.creditCardExpiry().length() != SystemConstants.EXPIRY_DATE_LENGTH ||
                creditCardInformation.creditCardExpiry().charAt(SystemConstants.EXPIRY_DATE_SLASH_POSITION) != '/') {
                return OrderValidationCode.EXPIRY_DATE_INVALID;
            }
            try {
                int month=Integer.parseInt(creditCardInformation.creditCardExpiry().substring(0,2));
                int year=Integer.parseInt(creditCardInformation.creditCardExpiry().substring(3,5));
                if (month<1 || month>12) {

                    return OrderValidationCode.EXPIRY_DATE_INVALID;
                }
                Calendar cal = Calendar.getInstance();
                if(year< cal.get(Calendar.YEAR) -2000){

                    return OrderValidationCode.EXPIRY_DATE_INVALID;
                }
                if (year==cal.get(Calendar.YEAR) -100 &&
                        month<cal.get(Calendar.MONTH)+1){
                    return OrderValidationCode.EXPIRY_DATE_INVALID;
                }
            } catch (NumberFormatException e) {
                return OrderValidationCode.EXPIRY_DATE_INVALID;
            }

            return OrderValidationCode.NO_ERROR;
        }

    /**
     * Check if the pizza's price is valid and if pizza is from the same restaurant as the order's restaurant or from
     * another restaurant or if the pizza is not defined in any restaurant
     * @param pizza the pizza
     * @param definedRestaurants the defined restaurants
     * @return OrderValidationCode
     */
    public OrderValidationCode checkPizza(@NotNull Pizza pizza, Restaurant[] definedRestaurants) {
        for (Pizza item : this.restaurant.menu()) {
            if (item.name().equals(pizza.name())) {
                if (!pizza.priceInPence().equals(item.priceInPence())) {
                    return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
                }
                return OrderValidationCode.NO_ERROR;
            }
        }
        for (Restaurant r : definedRestaurants) {
            for (Pizza item : r.menu()) {
                if (item.name().equals(pizza.name())) {
                    return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
                }
            }
        }
        return OrderValidationCode.PIZZA_NOT_DEFINED;
    }

    /**
     * Check if the code is invalid
     * @param validationCode the validation code
     * @param order the order
     * @return boolean
     */
    private boolean invalidCode(OrderValidationCode validationCode, Order order){
        if (validationCode != OrderValidationCode.NO_ERROR) {
            if(validationCode==OrderValidationCode.UNDEFINED){
                order.setOrderStatus(OrderStatus.INVALID);
            }else {
                order.setOrderStatus(OrderStatus.INVALID);
            }
            order.setOrderValidationCode(validationCode);
            return true;
        }
        return false;
    }

    @Override
    public Restaurant validateAndGetRestaurant(Order order, Restaurant[] definedRestaurants) {
         if(validateOrder(order,definedRestaurants).orderValidationCode()==OrderValidationCode.NO_ERROR){
             return this.restaurant;
         }
         throw new IllegalArgumentException("Order not valid with error code : " + order.getOrderValidationCode());
    }
}
