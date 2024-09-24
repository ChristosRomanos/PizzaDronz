package uk.ac.ed.inf.pizzadronz;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/uuid")
    public String uuid() {
        return "s2149970";
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<String> distanceTo(@RequestBody distanceTo locations ) {
        double distance=locations.calculatedistanceTo();
        if (distance>0){
            return ResponseEntity.ok("Distance is "+distance);
        }
        else{

            return ResponseEntity.badRequest().body(locations.getLocation1().toString()+"  "+locations.getPosition2().toString());
        }
    }
    @PostMapping("/isCloseTo")
    public ResponseEntity<String> isCloseTo(@RequestBody isCloseTo locations ) {
        if (locations.isClose()==-1){
            return ResponseEntity.badRequest("Not valid input");
        }
        else{
            if (locations.isClose()==1){
                return ResponseEntity.ok("True");
            }
            else{
                return ResponseEntity.ok("False");
            }
        }
    }
}
