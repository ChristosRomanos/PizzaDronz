package uk.ac.ed.inf.pizzadronz.RequestBodies;

import uk.ac.ed.inf.pizzadronz.Data.LngLat;



public record PositionPair(LngLat position1,LngLat position2) {
    public PositionPair {
        if (position1 == null ) {
            throw new RuntimeException("Position1 is null");
        }
        if (position2 == null ) {
            throw new RuntimeException("Position2 is null");
        }
    }
}