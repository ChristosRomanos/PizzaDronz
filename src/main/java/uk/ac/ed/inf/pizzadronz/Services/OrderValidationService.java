package uk.ac.ed.inf.pizzadronz.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.constants.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.CreditCardInformation;
import uk.ac.ed.inf.pizzadronz.data.Order;
import uk.ac.ed.inf.pizzadronz.data.Pizza;
import uk.ac.ed.inf.pizzadronz.data.Restaurant;

import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;

@Service
public class OrderValidationService {
    private final RestTemplate restTemplate = new RestTemplate();
    private Restaurant[] restaurants;
    private Restaurant restaurant;
    private Date date;
    public OrderValidationService() {
        restaurant=null;

    }

    public OrderValidationCode checkOrder(Order order) {
        date=new Date();
        if (checkOrderDate(order.orderDate()) != OrderValidationCode.NO_ERROR) {
            return checkOrderDate(order.orderDate());
        }
        retrieveRestaurants();
        if (checkPriceTotalInPenceAndPizzas(order.priceTotalInPence(), order.pizzasInOrder()) != OrderValidationCode.NO_ERROR) {
            return checkPriceTotalInPenceAndPizzas(order.priceTotalInPence(), order.pizzasInOrder());
        }

        if (checkCreditCardInformation(order.creditCardInformation()) != OrderValidationCode.NO_ERROR) {
            return checkCreditCardInformation(order.creditCardInformation());
        }
        return OrderValidationCode.NO_ERROR;
    }

    public OrderValidationCode checkOrderDate(Date orderDate) {
        if (orderDate.before(date)) {
            return OrderValidationCode.UNDEFINED;
        }
        return OrderValidationCode.NO_ERROR;
    }

    public OrderValidationCode checkPriceTotalInPenceAndPizzas(Integer priceTotalInPence, Pizza[] pizzasInOrder) {
        if (priceTotalInPence < 0) {
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        if (pizzasInOrder.length < 1) {
            return OrderValidationCode.EMPTY_ORDER;
        } else if (pizzasInOrder.length > SystemConstants.MAX_PIZZA_COUNT) {
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        } else {
            int colonIndex=pizzasInOrder[0].name().indexOf(":");
            if (colonIndex==-1){
                return OrderValidationCode.PIZZA_NOT_DEFINED;
            }
            String restaurantName=pizzasInOrder[0].name().substring(0,colonIndex);
            for (Restaurant restaurant : restaurants) {
                if (restaurant.name().equals(restaurantName)) {
                    this.restaurant = restaurant;
                    break;
                }
            }
            if (restaurant == null) {
                return OrderValidationCode.PIZZA_NOT_DEFINED;
            }
            boolean isOpen=false;
            for (DayOfWeek day : restaurant.openingDays()) {
                if (date.toInstant().get(ChronoField.DAY_OF_WEEK)==day.getValue()) {
                    isOpen=true;
                    break;
                }
            }
            if (!isOpen) {
                return OrderValidationCode.RESTAURANT_CLOSED;
            }
            int sum = SystemConstants.DELIVERY_FEE;
            for (Pizza pizza : pizzasInOrder) {
                if (checkPizza(restaurant,pizza) == OrderValidationCode.NO_ERROR) {
                    sum += pizza.priceInPence();
                } else {
                    return pizza.checkPizza();
                }
            }
            if (sum != priceTotalInPence) {
                return OrderValidationCode.TOTAL_INCORRECT;
            }
            return OrderValidationCode.NO_ERROR;
        }
    }

    public OrderValidationCode checkCreditCardInformation(CreditCardInformation creditCardInformation) {
            // check if the card number is valid
            if (creditCardInformation.cardNumber().length()!=16) {
                return OrderValidationCode.CARD_NUMBER_INVALID;
            }try {
                Long.parseLong(creditCardInformation.cardNumber());
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
            if (creditCardInformation.expiryDate().length()!=5 || creditCardInformation.expiryDate().charAt(2) != '/') {
                return OrderValidationCode.EXPIRY_DATE_INVALID;
            }
            try {
                int month=Integer.parseInt(creditCardInformation.expiryDate().substring(0,2));
                int year=Integer.parseInt(creditCardInformation.expiryDate().substring(3,5));
                if (month<1 || month>12) {
                    return OrderValidationCode.EXPIRY_DATE_INVALID;
                }
                Calendar cal = Calendar.getInstance();
                if(year< cal.get(Calendar.YEAR) -100){
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

    public OrderValidationCode checkPizza(Restaurant restaurant,Pizza pizza) {
        int colonIndex=pizza.name().indexOf(":");
        if (colonIndex==-1){
            return OrderValidationCode.PIZZA_NOT_DEFINED;
        }
        String restaurantName=pizza.name().substring(0,colonIndex);
        String pizzaName=pizza.name().substring(colonIndex+1);
        if (!restaurant.name().equals(restaurantName)) {
            if (restaurantName.isEmpty()) {
                return OrderValidationCode.PIZZA_NOT_DEFINED;
            }
            return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
        }
        for (Pizza item : restaurant.menu()) {
            if (item.name().equals(pizzaName)) {
                if (!pizza.priceInPence().equals(item.priceInPence())) {
                    return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
                }
                return OrderValidationCode.NO_ERROR;
            }
        }
        return OrderValidationCode.PIZZA_NOT_DEFINED;
    }

    public void retrieveRestaurants(){
        restaurants=restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class);
    }
}
