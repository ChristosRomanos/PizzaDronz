package uk.ac.ed.inf.pizzadronz.Data;


import uk.ac.ed.inf.pizzadronz.Constants.Directions;

public class AStarNode {
    private LngLat position;
    private double gCost, hCost;
    private AStarNode parent;
    private int stepCount;
    private double FCost;
    boolean inCentral;
    public Directions Angle;

    public AStarNode(LngLat position,int stepCount, AStarNode parent, double gCost, double hCost, boolean inCentral) {
        this.position = position;
        this.gCost =gCost;  // Initialized to max to be minimized during A*
        this.hCost = hCost;
        this.stepCount= stepCount;
        this.parent = parent;
        this.FCost =  gCost+hCost;
        this.inCentral = inCentral;

    }
    public AStarNode(LngLat position,int stepCount, AStarNode parent, double gCost, double hCost, boolean inCentral, Directions angle) {
        this.position = position;
        this.gCost =gCost;  // Initialized to max to be minimized during A*
        this.hCost = hCost;
        this.parent = parent;
        this.FCost =  gCost+hCost;
        this.inCentral = inCentral;
        this.Angle = angle;
        this.stepCount= stepCount;
    }

    public AStarNode(LngLat position, AStarNode parent) {
        this.position = position;
        this.parent = parent;
    }

    public boolean isInCentral() {
        return inCentral;
    }

    public void setGCost(double gCost) {
        this.gCost = gCost;
    }

    public void setInCentral(boolean inCentral) {
        this.inCentral = inCentral;
    }

    public void setHCost(double hCost) {
        this.hCost = hCost;
    }

    public void setParent(AStarNode parent) {
        this.parent = parent;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public LngLat getPosition() {
        return position;
    }

    public AStarNode getParent() {
        return parent;
    }

    public double getGCost() {
        return gCost;
    }

    public double getHCost() {
        return hCost;
    }

    public int getStepCount() {
        return stepCount;
    }

    public double getFCost() {
        return FCost;
    }

}
