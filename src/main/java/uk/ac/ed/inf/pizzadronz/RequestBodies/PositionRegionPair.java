package uk.ac.ed.inf.pizzadronz.RequestBodies;

import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.data.NamedRegion;

import javax.validation.constraints.NotNull;

public record PositionRegionPair(@NotNull LngLat position,@NotNull NamedRegion region) {
}
