package uk.ac.ed.inf.pizzadronz.RequestBodies;

import uk.ac.ed.inf.pizzadronz.data.LngLat;

import javax.validation.constraints.NotNull;

public record PositionPair(@NotNull LngLat position1 , @NotNull LngLat position2) {
}
