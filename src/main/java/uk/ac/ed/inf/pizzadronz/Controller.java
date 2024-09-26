package uk.ac.ed.inf.pizzadronz;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionAnglePair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionPair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionRegionPair;
import uk.ac.ed.inf.pizzadronz.data.LngLat;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.interfaces.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.handlers.LngLatHandler;
import javax.validation.Valid;

@RestController
public class Controller {
    LngLatHandling lnglatHandler = new LngLatHandler();

    @PostMapping("/uuid")
    public String uuid() {
        return SystemConstants.STUDENT_ID;
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo (@Valid @RequestBody PositionPair locations )  {
        double distance= lnglatHandler.distanceTo(locations.position1(),locations.position2());
            return ResponseEntity.ok(distance);
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@Valid @RequestBody PositionPair locations) {
        return(lnglatHandler.isCloseTo(locations.position1(),locations.position2()));
    }


    @PostMapping("nextPosition")
    public ResponseEntity<LngLat> nextPosition(@Valid @RequestBody PositionAnglePair nextPosition) {
        return ResponseEntity.ok(lnglatHandler.nextPosition(nextPosition.start(),nextPosition.angle()));
    }

    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@Valid @RequestBody PositionRegionPair positionRegion) {
            return ResponseEntity.ok(lnglatHandler.isInRegion(positionRegion.position(), positionRegion.region()));
    }
    @ControllerAdvice
    public static class ExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
