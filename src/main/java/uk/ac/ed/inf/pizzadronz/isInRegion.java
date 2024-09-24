package uk.ac.ed.inf.pizzadronz;


public class isInRegion {
    private PositionRegion positionRegion;
    private Integer regionSize;
    public isInRegion(PositionRegion positionRegion){
        this.positionRegion = positionRegion;
        this.regionSize = positionRegion.getRegion().getVertices().size();
    }
    public boolean validRegion(){
         if (regionSize < 3) {
             return false;
         }
         int i;
         int j;
        for (i = 1, j=0; i < regionSize-1; j = i++) {
            double vertice1Lng = positionRegion.getRegion().getVertices().get(i).lng();
            double vertice1Lat = positionRegion.getRegion().getVertices().get(i).lat();
            double vertice2Lng = positionRegion.getRegion().getVertices().get(j).lng();
            double vertice2Lat = positionRegion.getRegion().getVertices().get(j).lat();
            double vertice3Lng = positionRegion.getRegion().getVertices().get(i + 1).lng();
            double vertice3Lat = positionRegion.getRegion().getVertices().get(i + 1).lat();
            if (((vertice3Lat==vertice1Lat)&&(vertice3Lng==vertice1Lng))||
                    ((vertice3Lat==vertice2Lat)&&(vertice3Lng==vertice2Lng))||
                    ((vertice3Lat - vertice1Lat) / (vertice3Lng - vertice1Lng) ==
                    (vertice3Lat - vertice2Lat) / (vertice3Lng - vertice2Lng))){
                positionRegion.getRegion().getVertices().remove(i);
                regionSize = regionSize - 1;
                i=j;
            }
        }
        return (regionSize >= 3);
    }
    public boolean isInside(){
        int i;
        int j;
        boolean inside = false;

        for (i = 0, j = regionSize - 1; i < regionSize; j = i++) {
            double positionLng = positionRegion.getPosition().lng();
            double positionLat = positionRegion.getPosition().lat();
            double vertice1Lng = positionRegion.getRegion().getVertices().get(i).lng();
            double vertice1Lat = positionRegion.getRegion().getVertices().get(i).lat();
            double vertice2Lng = positionRegion.getRegion().getVertices().get(j).lng();
            double vertice2Lat = positionRegion.getRegion().getVertices().get(j).lat();
            if ((vertice1Lat > positionLat) != (vertice2Lat > positionLat) &&
                    (positionLng < vertice1Lng+
                            (vertice2Lng - vertice1Lng) * (positionLat - vertice1Lat) / (vertice2Lat -vertice1Lat) )) {
                inside = !inside;
            }
        }
        return inside;
    }

}
