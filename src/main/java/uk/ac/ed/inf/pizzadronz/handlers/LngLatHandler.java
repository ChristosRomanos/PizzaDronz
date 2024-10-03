package uk.ac.ed.inf.pizzadronz.handlers;

import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.data.NamedRegion;
import uk.ac.ed.inf.pizzadronz.interfaces.LngLatHandling;

import javax.validation.constraints.NotNull;

public class LngLatHandler implements LngLatHandling {

    @Override
    public double distanceTo (@NotNull LngLat position1,@NotNull LngLat position2){
        return Math.sqrt(
                Math.pow(position1.lng() - position2.lng(), 2)
                        + Math.pow(position1.lat() - position2.lat(), 2));
    }

    @Override
    public boolean isCloseTo(@NotNull LngLat startPosition,@NotNull LngLat otherPosition) {
        return distanceTo(startPosition,otherPosition)<SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public boolean isInRegion(@NotNull LngLat position,@NotNull NamedRegion region) {
        if (validRegion(region)){
            int regionSize = region.vertices().size();
            int i;
            int j;
            boolean inside = false;
            for (i = 0, j = regionSize - 1; i < regionSize; j = i++) {
                double positionLng = position.lng();
                double positionLat = position.lat();
                double vertice1Lng = region.vertices().get(i).lng();
                double vertice1Lat = region.vertices().get(i).lat();
                double vertice2Lng = region.vertices().get(j).lng();
                double vertice2Lat = region.vertices().get(j).lat();
                if ((positionLat==vertice1Lat && positionLng==vertice1Lng)||
                        (Math.abs((positionLat - vertice1Lat) / (positionLng - vertice1Lng)) ==
                                Math.abs((positionLat - vertice2Lat) / (positionLng - vertice2Lng)))){
                    return true;
                }
                if ((vertice1Lat > positionLat) != (vertice2Lat > positionLat) &&
                        (positionLng < vertice1Lng+
                                (vertice2Lng - vertice1Lng) * (positionLat - vertice1Lat) / (vertice2Lat -vertice1Lat) )) {
                    inside = !inside;
                }
            }
            return inside;
        }
        throw new RuntimeException();
    }
    public boolean validRegion(@NotNull NamedRegion region){
        int regionSize=region.vertices().size();
        if (regionSize < 3) {
            return false;
        }
        int i;
        int j;
        for (i = 1, j=0; i < regionSize; j = i++) {
            int k;
            if (i==regionSize-1){
                k=0;
            }
            else {
                k=i+1;
            }
            double vertice1Lng = region.vertices().get(i).lng();
            double vertice1Lat = region.vertices().get(i).lat();
            double vertice2Lng = region.vertices().get(j).lng();
            double vertice2Lat = region.vertices().get(j).lat();
            double vertice3Lng = region.vertices().get(k).lng();
            double vertice3Lat = region.vertices().get(k).lat();
            if((vertice1Lng==vertice2Lng && vertice1Lat==vertice2Lat)||
                    (vertice1Lng==vertice3Lng && vertice1Lat==vertice3Lat)){
                region.vertices().remove(i);
                regionSize = regionSize - 1;
                i = j;
            }
            else if (vertice3Lng<vertice1Lng && vertice1Lng<vertice2Lng||
                    vertice3Lng>vertice1Lng && vertice1Lng>vertice2Lng) {
                if ((vertice3Lat - vertice1Lat) / (vertice3Lng - vertice1Lng) ==
                        (vertice3Lat - vertice2Lat) / (vertice3Lng - vertice2Lng)) {
                    region.vertices().remove(i);
                    regionSize = regionSize - 1;
                    i = j;
                } else if ((vertice3Lat - vertice1Lat) / (vertice3Lng - vertice1Lng) ==
                        -(vertice3Lat - vertice2Lat) / (vertice3Lng - vertice2Lng)) {
                    region.vertices().remove(i);
                    regionSize = regionSize - 1;
                    i = j;
                }
            }
        }
        return regionSize >= 3;
    }

    @Override
    public LngLat nextPosition(@NotNull LngLat start,@NotNull Double angle) {
        if (angle!=SystemConstants.DRONE_HOVERING_ANGLE && (angle% SystemConstants.DRONE_ANGLE_MULTIPLIER)!=0){
            throw new RuntimeException();
        }
        double lng=start.lng()+Math.cos(Math.toRadians(angle))* SystemConstants.DRONE_MOVE_DISTANCE;
        double lat=start.lat()+Math.sin(Math.toRadians(angle))* SystemConstants.DRONE_MOVE_DISTANCE;
        return new LngLat(lng,lat);
    }
}

