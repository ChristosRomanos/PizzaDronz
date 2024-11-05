package uk.ac.ed.inf.pizzadronz.RequestBodies;

import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.LngLat;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public record PositionAnglePair(LngLat start,
        Double angle) {
    public PositionAnglePair {
        if (start == null) {
            throw new RuntimeException("Start is null");
        }
        if (angle == null) {
            throw new RuntimeException("Angle is null");
        }
        if (//angle!=SystemConstants.DRONE_HOVERING_ANGLE&&
                (angle< SystemConstants.MIN_ANGLE || angle >= SystemConstants.MAX_ANGLE)) {
            throw new RuntimeException("Angle is out of bounds");
        }
    }
}
