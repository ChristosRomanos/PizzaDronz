package uk.ac.ed.inf.pizzadronz;

import uk.ac.ed.inf.pizzadronz.data.LongLat;

public class PositionRegion {
    private final LongLat position;
    private final Region region;

    public Region getRegion() {
        return region;
    }
    public LongLat getPosition() {
        return position;
    }
    public PositionRegion(LongLat position, Region region) {
        this.position = position;
        this.region = region;
    }
}
