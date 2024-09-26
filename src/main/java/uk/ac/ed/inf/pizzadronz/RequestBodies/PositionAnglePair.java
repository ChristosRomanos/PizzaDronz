package uk.ac.ed.inf.pizzadronz.RequestBodies;

import uk.ac.ed.inf.pizzadronz.data.LngLat;

import javax.validation.constraints.NotNull;

public record PositionAnglePair(@NotNull LngLat start,@NotNull Double angle) {

}
