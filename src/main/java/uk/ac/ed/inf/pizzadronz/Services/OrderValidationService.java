package uk.ac.ed.inf.pizzadronz.Services;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.ServiceInterface.OrderValidation;
import uk.ac.ed.inf.pizzadronz.constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constants.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;

@Service
public class OrderValidationService implements OrderValidation {
    private Restaurant restaurant;
    private LocalDate date;


    public Order validateOrder(@NotNull Order order, Restaurant[] definedRestaurants) {
        date= LocalDate.now();
        if (definedRestaurants==null || definedRestaurants.length==0){
            throw new IllegalArgumentException("No restaurants defined");
        }

        OrderValidationCode validationCode=checkOrderDate(order.getOrderDate());
        if (validationCode != OrderValidationCode.NO_ERROR) {
            if(validationCode==OrderValidationCode.UNDEFINED){
                order.setOrderStatus(OrderStatus.UNDEFINED);
            }else {
                order.setOrderStatus(OrderStatus.INVALID);
            }
            order.setOrderValidationCode(validationCode);
            return order;
        }

        validationCode=checkCreditCardInformation(order.getCreditCardInformation());
        if (validationCode != OrderValidationCode.NO_ERROR) {
            if (validationCode==OrderValidationCode.UNDEFINED){
                order.setOrderStatus(OrderStatus.UNDEFINED);
            }else {
                order.setOrderStatus(OrderStatus.INVALID);
            }
            order.setOrderValidationCode(validationCode);
            return order;
        }
        if(order.getPizzasInOrder().length==0){
            validationCode=OrderValidationCode.EMPTY_ORDER;
        }else {
            validationCode = checkIfRestaurantExists(order.getPizzasInOrder()[0], definedRestaurants);
        }

        if (validationCode != OrderValidationCode.NO_ERROR) {
            if (validationCode==OrderValidationCode.UNDEFINED){
                order.setOrderStatus(OrderStatus.UNDEFINED);
            }else {
                order.setOrderStatus(OrderStatus.INVALID);
            }
            order.setOrderValidationCode(validationCode);
            return order;
        }

        validationCode=checkIfRestaurantIsOpen(restaurant,order.getOrderDate());
        if (validationCode != OrderValidationCode.NO_ERROR) {
            if (validationCode==OrderValidationCode.UNDEFINED){
                order.setOrderStatus(OrderStatus.UNDEFINED);
            }else {
                order.setOrderStatus(OrderStatus.INVALID);
            }
            order.setOrderValidationCode(validationCode);
            return order;
        }

        validationCode=checkPriceTotalInPenceAndPizzas(order.getPriceTotalInPence(), order.getPizzasInOrder(),definedRestaurants);
        if (validationCode != OrderValidationCode.NO_ERROR) {
            if (validationCode==OrderValidationCode.UNDEFINED){
                order.setOrderStatus(OrderStatus.UNDEFINED);
            }else {
                order.setOrderStatus(OrderStatus.INVALID);
            }
            order.setOrderValidationCode(validationCode);
            return order;
        }

        order.setOrderStatus(OrderStatus.VALID);
        order.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return order;
    }

    public OrderValidationCode checkOrderDate(@NotNull LocalDate orderDate) {
        if (orderDate.isBefore(date)) {
            return OrderValidationCode.UNDEFINED;
        }
        return OrderValidationCode.NO_ERROR;
    }

    public OrderValidationCode checkIfRestaurantIsOpen(@NotNull Restaurant restaurant, LocalDate date) {
        for (DayOfWeek day : restaurant.openingDays()) {
            if (date.get(ChronoField.DAY_OF_WEEK)==day.getValue()) {
                return OrderValidationCode.NO_ERROR;
            }
        }
        return OrderValidationCode.RESTAURANT_CLOSED;
    }

    public OrderValidationCode checkIfRestaurantExists(@NotNull Pizza pizza, Restaurant[] definedRestaurants) {
        int colonIndex=pizza.name().indexOf(":");
        if (colonIndex==-1){
            return OrderValidationCode.PIZZA_NOT_DEFINED;
        }
        int restaurantNo=Integer.parseInt(pizza.name().substring(1,colonIndex));
        if (restaurantNo<1 || restaurantNo>definedRestaurants.length){
            return OrderValidationCode.PIZZA_NOT_DEFINED;
        }
        restaurant=definedRestaurants[restaurantNo-1];
        return OrderValidationCode.NO_ERROR;
    }

    public OrderValidationCode checkPriceTotalInPenceAndPizzas(Integer priceTotalInPence, Pizza[] pizzasInOrder,Restaurant[] definedRestaurants) {
        if (priceTotalInPence < SystemConstants.DELIVERY_FEE) {
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        if (pizzasInOrder.length > SystemConstants.MAX_PIZZA_COUNT) {
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        } else {
            int sum = SystemConstants.DELIVERY_FEE;
            for (Pizza pizza : pizzasInOrder) {
                OrderValidationCode valid=checkPizza(restaurant,pizza,definedRestaurants);
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
    }

    public OrderValidationCode checkCreditCardInformation(@NotNull CreditCardInformation creditCardInformation) {
            // check if the card number is valid
            if (creditCardInformation.creditCardNumber().length()!=16) {
                return OrderValidationCode.CARD_NUMBER_INVALID;
            }try {
                Long.parseLong(creditCardInformation.creditCardNumber());
            } catch (NumberFormatException e) {
                return OrderValidationCode.CARD_NUMBER_INVALID;
            }

            // check if the cvv is valid
            if (creditCardInformation.cvv().length()!=3) {
                return OrderValidationCode.CVV_INVALID;
            }
            try {
                Integer.parseInt(creditCardInformation.cvv());

            } catch (NumberFormatException e) {
                return OrderValidationCode.CVV_INVALID;
            }

            // check if the expiry date is valid
            if (creditCardInformation.creditCardExpiry().length()!=5 || creditCardInformation.creditCardExpiry().charAt(2) != '/') {
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

    public OrderValidationCode checkPizza(Restaurant restaurant, @NotNull Pizza pizza, Restaurant[] definedRestaurants) {
        int colonIndex=pizza.name().indexOf(":");
        if (colonIndex==-1){
            return OrderValidationCode.PIZZA_NOT_DEFINED;
        }
        int restaurantNumber=Integer.parseInt(pizza.name().substring(1,colonIndex));
        if (definedRestaurants[restaurantNumber-1]!=restaurant){
            return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
        }
        restaurant=definedRestaurants[restaurantNumber-1];
        for (Pizza item : restaurant.menu()) {
            if (item.name().equals(pizza.name())) {
                if (!pizza.priceInPence().equals(item.priceInPence())) {
                    return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
                }
                return OrderValidationCode.NO_ERROR;
            }
        }
        return OrderValidationCode.PIZZA_NOT_DEFINED;
    }

}
