package uk.ac.ed.inf.pizzadronz.ServiceInterfaces;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;

import java.util.List;

public interface GeoJson {
    /**
     * Create a GeoJSON LineString from a list of LngLat points
     * @param path List of LngLat points
     * @return GeoJSON LineString
     */
    JsonObject createGeoJsonLine(List<LngLat> path);
}
