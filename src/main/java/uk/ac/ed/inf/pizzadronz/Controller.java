package uk.ac.ed.inf.pizzadronz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionAnglePair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionPair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionRegionPair;
import uk.ac.ed.inf.pizzadronz.ServiceInterface.OrderValidation;
import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.ServiceInterface.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.Services.LngLatService;
import uk.ac.ed.inf.pizzadronz.data.Order;
import uk.ac.ed.inf.pizzadronz.data.Restaurant;


@RestController
public class Controller {
    private final OrderValidation orderValidation;
    private final LngLatHandling lnglatHandler;

    public Controller(OrderValidation orderValidation, LngLatHandling lnglatHandler) {
        this.orderValidation = orderValidation;
        this.lnglatHandler = lnglatHandler;
    }

    @GetMapping("/uuid")
    public String uuid() {
        return SystemConstants.STUDENT_ID;
    }


    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo (@RequestBody PositionPair locations )  {
        double distance= lnglatHandler.distanceTo(locations.position1(),locations.position2());
            return ResponseEntity.ok(distance);
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo( @RequestBody PositionPair locations) {
        return(lnglatHandler.isCloseTo(locations.position1(),locations.position2()));
    }


    @PostMapping("nextPosition")
    public ResponseEntity<LngLat> nextPosition( @RequestBody PositionAnglePair nextPosition) {
        return ResponseEntity.ok(lnglatHandler.nextPosition(nextPosition.start(),nextPosition.angle()));
    }

    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody PositionRegionPair positionRegion) {
            return ResponseEntity.ok(lnglatHandler.isInRegion(positionRegion.position(), positionRegion.region()));
    }

    @PostMapping("/validateOrder")
    public ResponseEntity<Order> validateOrder(@RequestBody Order order) {
        RestTemplate restTemplate = new RestTemplate();
        Restaurant[] definedRestaurants = restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class);
        return ResponseEntity.ok(orderValidation.validateOrder(order,definedRestaurants));
    }


    @ControllerAdvice
    public static class ExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
