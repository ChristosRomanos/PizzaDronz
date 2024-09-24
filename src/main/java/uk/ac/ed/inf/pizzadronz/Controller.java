package uk.ac.ed.inf.pizzadronz;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;

@RestController
public class Controller {
    SystemConstants systemConstants = new SystemConstants();

    @PostMapping("/uuid")
    public String uuid() {
        return "s2149970";
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<String> distanceTo(@RequestBody distanceTo locations ) {
        double distance=locations.calculateDistanceTo();
        if (distance>0){
            return ResponseEntity.ok("Distance is "+distance);
        }
        else{

            return ResponseEntity.badRequest().body("Not valid input");
        }
    }
    @PostMapping("/isCloseTo")
    public ResponseEntity<String> isCloseTo(@RequestBody distanceTo locations) {
        isCloseTo close = new isCloseTo(locations.calculateDistanceTo(),systemConstants.IS_CLOSE_THRESHOLD);
        if (close.isClose()==-1){
            return ResponseEntity.badRequest().body("Not valid input");
        }
        else{
            if (close.isClose()==1){
                return ResponseEntity.ok("True");
            }
            else{
                return ResponseEntity.ok("False");
            }
        }
    }
    @PostMapping("/isInRegion")
    public ResponseEntity<String> isInRegion(@RequestBody PositionRegion region) {
        isInRegion isInRegion = new isInRegion(region);
        if (!isInRegion.validRegion()){
            return ResponseEntity.badRequest().body("Not valid region");
        }
        if ((isInRegion.isInside())){
            return ResponseEntity.ok("True");
        }
        else{
            return ResponseEntity.ok("False");
        }
    }
}
