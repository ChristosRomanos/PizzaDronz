package uk.ac.ed.inf.pizzadronz;

import uk.ac.ed.inf.pizzadronz.data.LongLat;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;

public class isCloseTo {
    private distanceTo distanceTo;
    private final Integer isClose;
    public isCloseTo(double distance,double threshold) {

        if (distance<0){
            this.isClose=-1;
        }
        else if (distance < threshold) {
            this.isClose = 1;
        }else {
            this.isClose = 0;
        }
    }
    public Integer isClose() {
        return isClose;
    }

}
