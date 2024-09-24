package uk.ac.ed.inf.pizzadronz;

public class isCloseTo {
    private final Integer isClose;
    public isCloseTo(double distance,double threshold) {

        if (distance<0){
            this.isClose=-1;
        }
        else if (distance < threshold) {
            this.isClose = 1;
        }else {
            this.isClose = 0;
        }
    }
    public Integer isClose() {
        return isClose;
    }

}
