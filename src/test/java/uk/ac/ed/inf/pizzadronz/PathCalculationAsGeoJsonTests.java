package uk.ac.ed.inf.pizzadronz;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.NamedRegion;
import uk.ac.ed.inf.pizzadronz.Data.Restaurant;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.PathFinding;
import uk.ac.ed.inf.pizzadronz.Services.AStarPathFinding;
import uk.ac.ed.inf.pizzadronz.Services.GeoJsonUtils;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Services.LngLatService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DisplayName("GeoJSON Path Calculation Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PathCalculationAsGeoJsonTests {
    GeoJsonUtils geoJsonUtils;
    PathFinding pathFinding;
    RestTemplate restTemplate ;
    NamedRegion[] regions ;
    LngLatHandling lngLatHandling;

    @BeforeAll
    public void init(){
        geoJsonUtils=new GeoJsonUtils();
        pathFinding=new AStarPathFinding();
        restTemplate = new RestTemplate();
        regions = restTemplate.getForObject(SystemConstants.NO_FLY_ZONES_URL, NamedRegion[].class);
        lngLatHandling=new LngLatService();
    }


    @Test
    public void testNullPath() {
        List<LngLat> path = null;
        assertThrows(IllegalArgumentException.class, () -> geoJsonUtils.createGeoJsonLine(path));
    }

    // Test if the path is empty
    @Test
    public void testEmptyPath() {
        List<LngLat> path = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> geoJsonUtils.createGeoJsonLine(path));
    }

    // Test if the path is not null or empty
    @Test
    public void testValidPath() {
        List<LngLat> path = new ArrayList<>();
        path.add(new LngLat(1.0, 2.0));
        path.add(new LngLat(3.0, 4.0));
        JsonObject geoJson = geoJsonUtils.createGeoJsonLine(path);
        assertNotNull(geoJson);
        assertEquals("Feature", geoJson.get("type").getAsString());
        JsonObject geometry = geoJson.getAsJsonObject("geometry");
        assertEquals("LineString", geometry.get("type").getAsString());
        JsonArray coordinates = geometry.getAsJsonArray("coordinates");
        assertEquals(2, coordinates.size());
        JsonArray coordinate1 = coordinates.get(0).getAsJsonArray();
        assertEquals(1.0, coordinate1.get(0).getAsDouble(), 0.0001);
        assertEquals(2.0, coordinate1.get(1).getAsDouble(), 0.0001);
        JsonArray coordinate2 = coordinates.get(1).getAsJsonArray();
        assertEquals(3.0, coordinate2.get(0).getAsDouble(), 0.0001);
        assertEquals(4.0, coordinate2.get(1).getAsDouble(), 0.0001);
    }
    private Stream<Restaurant> getRestaurants(){
        RestTemplate restTemplate = new RestTemplate();
        return Stream.of(Objects.requireNonNull(restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class)));
    }

    @ParameterizedTest
    @MethodSource("getRestaurants")
    public void testPathToRestaurants(Restaurant restaurant) {
        LngLat destination=new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
        LngLat start=restaurant.location();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Submit the path calculation task with a timeout
        Future<List<LngLat>> future = executor.submit(() ->
                pathFinding.findPath(start, destination, regions, lngLatHandling)
        );

        List<LngLat> path;
        try {
            path = future.get(60, TimeUnit.SECONDS); // Timeout of 60 seconds
        } catch (Exception e) {
            throw new AssertionError("Error with message: " + e.getMessage());
        }
        assertTrue("Path is empty", !path.isEmpty());
        assertTrue("Wrong start",path.get(0).equals(start));
        assertTrue("Wrong destination",lngLatHandling.distanceTo(path.get(path.size()-1),destination)<=SystemConstants.DRONE_IS_CLOSE_DISTANCE);
        JsonObject pathLine=geoJsonUtils.createGeoJsonLine(path);
        assertTrue("Path is of different size from json path",path.size()==pathLine.getAsJsonObject("geometry").getAsJsonArray("coordinates").size());

}
}
