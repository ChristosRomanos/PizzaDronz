package uk.ac.ed.inf.pizzadronz.Services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.GeoJson;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import java.util.List;

@Service
public class GeoJsonUtils implements GeoJson {
    @Override
    public JsonObject createGeoJsonLine(List<LngLat> path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        JsonObject geoJson = new JsonObject();
        geoJson.addProperty("type", "Feature");

        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString");

        JsonArray coordinates = new JsonArray();

        for (LngLat point : path) {
            JsonArray coordinate = new JsonArray();
            coordinate.add(point.lng());
            coordinate.add(point.lat());
            coordinates.add(coordinate);
        }

        geometry.add("coordinates", coordinates);
        geoJson.add("geometry", geometry);
        geoJson.add("properties", new JsonObject());
        return geoJson;
    }
}
