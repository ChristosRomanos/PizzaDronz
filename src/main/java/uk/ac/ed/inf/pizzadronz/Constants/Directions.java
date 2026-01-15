package uk.ac.ed.inf.pizzadronz.Constants;

public enum Directions {
    E(0),
    ENE(22.5),
    NE(45),
    NNE(67.5),
    N(90),
    NNW(112.5),
    NW(135),
    WNW(157.5),
    W(180),
    WSW(202.5),
    SW(225),
    SSW(247.5),
    S(270),
    SSE(292.5),
    SE(315),
    ESE(337.5);
    private final double angle;

    Directions(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }
}
