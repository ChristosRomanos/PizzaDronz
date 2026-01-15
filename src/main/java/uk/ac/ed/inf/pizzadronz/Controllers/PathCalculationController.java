package uk.ac.ed.inf.pizzadronz.Controllers;

import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.GeoJson;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.OrderValidation;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.PathFinding;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Data.NamedRegion;
import uk.ac.ed.inf.pizzadronz.Data.Order;
import uk.ac.ed.inf.pizzadronz.Data.Restaurant;

import java.io.IOException;
import java.util.List;

@RestController
public class PathCalculationController {
    private final LngLatHandling lnglatHandler;
    private final PathFinding pathFinding;
    private final GeoJson geoJson;
    private final OrderValidation orderValidation;

    public PathCalculationController(LngLatHandling lnglatHandler , PathFinding pathFinding, GeoJson geoJson, OrderValidation orderValidation) {
        this.lnglatHandler = lnglatHandler;
        this.pathFinding =pathFinding ;
        this.geoJson = geoJson;
        this.orderValidation = orderValidation;
    }

    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<LngLat[]> dronePath(@RequestBody Order order) {
        LngLat destination= new LngLat(SystemConstants.APPLETON_LNG,SystemConstants.APPLETON_LAT);
        RestTemplate restTemplate = new RestTemplate();
        NamedRegion[] noFlyZones= restTemplate.getForObject(SystemConstants.NO_FLY_ZONES_URL, NamedRegion[].class);
        Restaurant[] restaurants = restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class);
        Restaurant restaurant = orderValidation.validateAndGetRestaurant(order,restaurants);
        List<LngLat> path = pathFinding.findPath(restaurant.location(),destination,noFlyZones,lnglatHandler);
        return ResponseEntity.ok(path.toArray(LngLat[]::new));
    }

    @PostMapping("/calcDeliveryPathAsGeoJson")
    public ResponseEntity<String> dronePathGeoJson(@RequestBody Order order) {
        LngLat destination= new LngLat(SystemConstants.APPLETON_LNG,SystemConstants.APPLETON_LAT);
        RestTemplate restTemplate = new RestTemplate();
        NamedRegion[] noFlyZones= restTemplate.getForObject(SystemConstants.NO_FLY_ZONES_URL, NamedRegion[].class);
        Restaurant[] restaurants = restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class);
        Restaurant restaurant = orderValidation.validateAndGetRestaurant(order,restaurants);
        List<LngLat> path = pathFinding.findPath(restaurant.location(),destination,noFlyZones,lnglatHandler);
        JsonObject geoJsonResponse = geoJson.createGeoJsonLine(path);

        return ResponseEntity.ok(geoJsonResponse.toString());
    }

    @ControllerAdvice(assignableTypes =  PathCalculationController.class)
    public static class ExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
