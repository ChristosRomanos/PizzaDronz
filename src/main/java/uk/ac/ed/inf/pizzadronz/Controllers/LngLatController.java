package uk.ac.ed.inf.pizzadronz.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionAnglePair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionPair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionRegionPair;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;



@RestController
public class LngLatController {
    private final LngLatHandling lnglatHandler;

    public LngLatController(LngLatHandling lnglatHandler) {
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





    @ControllerAdvice(assignableTypes = LngLatController.class)
    public static class ExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
