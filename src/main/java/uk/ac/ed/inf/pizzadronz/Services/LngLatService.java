package uk.ac.ed.inf.pizzadronz.Services;

import org.springframework.stereotype.Service;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Data.NamedRegion;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
public class LngLatService implements LngLatHandling {

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
            double positionLng = position.lng();
            double positionLat = position.lat();
            double vertex1Lng;
            double vertex1Lat;
            double vertex2Lng;
            double vertex2Lat;
            for (i = 1, j = 0; i < regionSize; j = i++) {
                vertex1Lng = region.vertices().get(i).lng();
                vertex1Lat = region.vertices().get(i).lat();
                vertex2Lng = region.vertices().get(j).lng();
                vertex2Lat = region.vertices().get(j).lat();
                if ((positionLng==vertex2Lng&& positionLat==vertex2Lat)||

                        ((vertex1Lng==vertex2Lng && vertex1Lng==positionLng)&&
                        (Math.abs((positionLat-vertex1Lat))<=Math.abs(vertex2Lat-vertex1Lat)+SystemConstants.EPSILON_ERROR)&&
                                (Math.abs((positionLat-vertex2Lat))<=Math.abs(vertex2Lat-vertex1Lat)+SystemConstants.EPSILON_ERROR))||
                        ((Math.abs((positionLat-vertex2Lat)/(positionLng-vertex2Lng)-
                                (vertex1Lat-vertex2Lat)/(vertex1Lng-vertex2Lng))<=SystemConstants.EPSILON_ERROR)&&
                        (Math.abs((positionLng-vertex2Lng))<=Math.abs(vertex1Lng-vertex2Lat)+SystemConstants.EPSILON_ERROR)&&
                            (Math.abs((positionLng-vertex1Lng))<=Math.abs(vertex2Lng-vertex1Lng)+SystemConstants.EPSILON_ERROR))){
                    return true;
                }
                if ((vertex1Lat > positionLat) != (vertex2Lat > positionLat) &&
                        (positionLng-SystemConstants.EPSILON_ERROR < vertex1Lng+
                                (vertex2Lng - vertex1Lng) * (positionLat - vertex1Lat) / (vertex2Lat -vertex1Lat) )) {
                    inside = !inside;
                }
            }
            return inside;
        }
        throw new RuntimeException("Invalid region");

    }

    @Override
    public boolean validRegion(@NotNull NamedRegion region){
        int regionSize=region.vertices().size();
        if (regionSize < 4) {
            return false;
        }
        if (!Objects.equals(region.vertices().get(0),region.vertices().get(regionSize-1))){
            return false;
        }
        int i;
        int j;
        for (i = 1, j=0; i < regionSize-1; j = i++) {
            int k = i + 1;
            double vertex1Lng = region.vertices().get(i).lng();
            double vertex1Lat = region.vertices().get(i).lat();
            double vertex2Lng = region.vertices().get(j).lng();
            double vertex2Lat = region.vertices().get(j).lat();
            double vertex3Lng = region.vertices().get(k).lng();
            double vertex3Lat = region.vertices().get(k).lat();
            if ((vertex1Lng == vertex2Lng && vertex1Lat == vertex2Lat) ||
                    (vertex1Lng == vertex3Lng && vertex1Lat == vertex3Lat)) {
                region.vertices().remove(i);
                regionSize = regionSize - 1;
                i = j;
            } else if ((vertex3Lat - vertex1Lat) / (vertex3Lng - vertex1Lng) ==
                    (vertex3Lat - vertex2Lat) / (vertex3Lng - vertex2Lng)) {
                region.vertices().remove(i);
                regionSize = regionSize - 1;
                i = j;
            }
        }
        return regionSize >= 4;
    }

    @Override
    public LngLat nextPosition(@NotNull LngLat start,@NotNull Double angle) {

        if (!validAngle(angle)){
            throw new RuntimeException("Invalid angle");
        }
        if (angle==SystemConstants.DRONE_HOVERING_ANGLE){
            return start;
        }
        double lng=start.lng()+(Math.cos(Math.toRadians(angle))* SystemConstants.DRONE_MOVE_DISTANCE);

        double lat=start.lat()+Math.sin(Math.toRadians(angle))* SystemConstants.DRONE_MOVE_DISTANCE;
        return new LngLat(lng,lat);
    }
    @Override
    public boolean validAngle(double angle){
        return (angle==SystemConstants.DRONE_HOVERING_ANGLE || (angle>=0&&angle<360));
    }
}

