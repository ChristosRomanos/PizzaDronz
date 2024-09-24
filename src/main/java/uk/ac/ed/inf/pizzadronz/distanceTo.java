package uk.ac.ed.inf.pizzadronz;

import uk.ac.ed.inf.pizzadronz.data.LngLat;

public class distanceTo {
    private final LngLat position1;
    private final LngLat position2;

    public distanceTo(LngLat position1, LngLat position2) {
        this.position1 = position1;
        this.position2 = position2;
    }


    public double calculateDistanceTo() {
        if (position1 == null || position2 == null) {
            return -1;
        }
        return Math.sqrt(
                Math.pow(position1.lng() - position2.lng(), 2)
                        + Math.pow(position1.lat() - position2.lat(), 2));
    }
}
