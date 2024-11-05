package uk.ac.ed.inf.pizzadronz.RequestBodies;

import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.data.NamedRegion;


public record PositionRegionPair(LngLat position,NamedRegion region) {
    public PositionRegionPair {
        if (position == null ) {
            throw new RuntimeException("Position is null");
        }
        if (region == null ) {
            throw new RuntimeException("Region is null");
        }
    }
}
