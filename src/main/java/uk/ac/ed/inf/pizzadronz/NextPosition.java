package uk.ac.ed.inf.pizzadronz;

import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.LngLat;

public class NextPosition {
    private final LngLat start;
    private final double angle;

    public NextPosition(LngLat start, double angle) {
        this.start = start;
        this.angle = Math.toRadians(angle);

    }
    public LngLat calculateNextPosition(){
        double distanceMoved = SystemConstants.DRONE_MOVE_DISTANCE;
        double lat=start.lat()+Math.cos(angle)* distanceMoved;
        double lng=start.lng()+Math.sin(angle)* distanceMoved;
        return new LngLat(lng,lat);
    }

}
