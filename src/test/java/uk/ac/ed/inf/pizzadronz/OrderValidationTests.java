package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind
        .ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.Data.*;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.OrderValidation;
import uk.ac.ed.inf.pizzadronz.Services.OrderValidationService;
import uk.ac.ed.inf.pizzadronz.Constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.Constants.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.time.LocalDate;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Validation Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderValidationTests {

    RestTemplate restTemplate;
    Restaurant[] restaurants;
    Order dummyOrder;
    Order validOrder;
    OrderValidation orderValidationService;

    private RestTemplate createRestTemplate() {
        RestTemplate template = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        template.getMessageConverters().add(0, converter);
        return template;
    }

    @BeforeAll
    public void initAll(){
        restTemplate = createRestTemplate();
        restaurants = restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class);
        orderValidationService=new OrderValidationService();
    }

    @BeforeEach
    public void init(){
        dummyOrder=new Order("dummyOrder", LocalDate.now(),
                OrderStatus.VALID,OrderValidationCode.NO_ERROR,
                0,new Pizza[]{}, new CreditCardInformation("dummyName", "dummyNumber", "dummyExpiryDate"));
        validOrder=null;
    }



    // Method to fetch orders and provide them as a stream for parameterized tests
    private Stream<Order> provideOrders() {
        Order[] orders = restTemplate.getForObject(SystemConstants.ORDERS_URL, Order[].class);
        assert orders != null;
        for (Order order : orders) {
            OrderValidationCode code = order.getOrderValidationCode();
            if (code == OrderValidationCode.NO_ERROR && validOrder == null) {
                validOrder = order;
            }
        }

        return Stream.of(orders);
    }
    @Test
    public void checkWithNullAndEmptyRestaurants(){
        assertThrows(Exception.class, () -> orderValidationService.validateOrder(dummyOrder,null));
        assertThrows(Exception.class, () -> orderValidationService.validateAndGetRestaurant(dummyOrder,new Restaurant[]{}));
    }

    @Test
    public void testWrongDate(){
        dummyOrder.setOrderDate(LocalDate.now().minusDays(1));
        assertSame(OrderStatus.INVALID, orderValidationService.validateOrder(dummyOrder,restaurants).orderStatus());
    }

    @ParameterizedTest
    @MethodSource("provideOrders")
    public void testOrderValidation(Order order) {
        OrderValidationCode expectedCode = order.getOrderValidationCode();
        OrderStatus expectedStatus = order.getOrderStatus();
        order.setOrderStatus(null);
        order.setOrderValidationCode(null);
        OrderValidationResult validatedOrder = orderValidationService.validateOrder(order, restaurants);

        assertSame(expectedStatus, validatedOrder.orderStatus(), "OrderValidationCode mismatch for order: " + order.getOrderNo());
        assertSame(expectedCode, validatedOrder.orderValidationCode(), "OrderStatus mismatch for order: " + order.getOrderNo());

    }
    @Test
    public void testInvalidOrder(){
        Order dummyOrder = new Order("dummy", LocalDate.now().minusDays(1),0,
                new Pizza[]{},new CreditCardInformation("dummy", "dummy", "dummy"));
        assertThrows(Exception.class, () -> orderValidationService.validateAndGetRestaurant(dummyOrder,restaurants));
    }
    @Test
    public void testValidOrder(){
        provideOrders();
        if(validOrder!=null){
            assertInstanceOf(Restaurant.class, orderValidationService.validateAndGetRestaurant(validOrder,restaurants));
        }else{
            System.out.println("No valid order found");
        }
    }
}

