package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.Services.OrderValidationService;
import uk.ac.ed.inf.pizzadronz.constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constants.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.Order;
import uk.ac.ed.inf.pizzadronz.data.Restaurant;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestTemplate;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderValidationTests {

    RestTemplate restTemplate = new RestTemplate();
    OrderValidationService orderValidationService = new OrderValidationService();
    Restaurant[] restaurants = restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class);

    // Method to fetch orders and provide them as a stream for parameterized tests
    private Stream<Order> provideOrders() {
        Order[] orders = restTemplate.getForObject("https://ilp-rest-2024.azurewebsites.net/orders", Order[].class);
        return Stream.of(orders);
    }

    @ParameterizedTest
    @MethodSource("provideOrders")
    public void testOrderValidation(Order order) {
        OrderValidationCode expectedCode = order.getOrderValidationCode();
        OrderStatus expectedStatus = order.getOrderStatus();
        order.setOrderStatus(null);
        order.setOrderValidationCode(null);
        Order validatedOrder = orderValidationService.validateOrder(order, restaurants);

        assertSame(expectedCode, validatedOrder.getOrderValidationCode(), "OrderValidationCode mismatch for order: " + order.getOrderNo());
        assertSame(expectedStatus, validatedOrder.getOrderStatus(), "OrderStatus mismatch for order: " + order.getOrderNo());
        System.out.println("Actual OrderStatus: " + validatedOrder.getOrderStatus());
        System.out.println("Actual OrderValidationCode: " + validatedOrder.getOrderValidationCode());
        System.out.println("Expected OrderStatus: " + expectedStatus);
        System.out.println("Expected OrderValidationCode: " + expectedCode);
    }
}

