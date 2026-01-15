package uk.ac.ed.inf.pizzadronz.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.Constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.Constants.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.OrderValidation;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.*;


@RestController
public class OrderValidationController {
    private final OrderValidation orderValidation;


    public OrderValidationController(OrderValidation orderValidation) {
        this.orderValidation = orderValidation;
    }


    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody Order order) {
        RestTemplate restTemplate = new RestTemplate();
        Restaurant[] definedRestaurants = restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class);
        return ResponseEntity.ok(orderValidation.validateOrder(order,definedRestaurants));
    }


    @ControllerAdvice (assignableTypes = OrderValidationController.class)
    public static class ExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
        public ResponseEntity<OrderValidationResult> handleException(Exception e) {
            return ResponseEntity.ok(new OrderValidationResult(OrderStatus.INVALID, OrderValidationCode.UNDEFINED));
        }
    }
}
