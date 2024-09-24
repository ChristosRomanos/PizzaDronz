package uk.ac.ed.inf.pizzadronz;

import uk.ac.ed.inf.pizzadronz.data.LongLat;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;

public class isCloseTo {
    public static Integer isCloseTo(LongLat a, LongLat b) {
        double distance = new distanceTo(a, b).calculatedistanceTo();
        if (distance<0){
            return -1;
        }
        if (distance <= new SystemConstants().IS_CLOSE_THRESHOLD) {
            return 1;
        }
        return 0;
    }

}
