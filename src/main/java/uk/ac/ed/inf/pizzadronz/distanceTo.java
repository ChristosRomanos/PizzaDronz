package uk.ac.ed.inf.pizzadronz;

import uk.ac.ed.inf.pizzadronz.data.LongLat;

public class distanceTo {
    private LongLat position1;
    private LongLat position2;

    public distanceTo(LongLat position1, LongLat position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    public LongLat getLocation1() {
        return position1;

    }
    public LongLat getPosition2() {
        return position2;
    }
    public void setLocation1(LongLat location1) {
        this.position1 = location1;
    }
    public void setPosition2(LongLat position2) {
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
