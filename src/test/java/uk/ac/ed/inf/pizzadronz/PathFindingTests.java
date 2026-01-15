package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.Data.*;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.PathFinding;
import uk.ac.ed.inf.pizzadronz.Services.AStarPathFinding;
import uk.ac.ed.inf.pizzadronz.Services.LngLatService;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@DisplayName("Path Finding Tests - A* Algorithm")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PathFindingTests {
    PathFinding pathFinding;
    double squareSide= SystemConstants.DRONE_MOVE_DISTANCE*2*2000;
    LngLatHandling lngLatHandling;
    RestTemplate restTemplate;
    NamedRegion[] regions ;
    NamedRegion centralRegion ;

    @BeforeAll
    public void init(){
        pathFinding=new AStarPathFinding();
        lngLatHandling=new LngLatService();
        restTemplate = new RestTemplate();
        regions = restTemplate.getForObject(SystemConstants.NO_FLY_ZONES_URL, NamedRegion[].class);
        centralRegion = restTemplate.getForObject(SystemConstants.CENTRAL_REGION_URL, NamedRegion.class);
    }


    private Stream<LngLat> getLocations(){
        LngLat[] locations=new LngLat[20];
        double lng;
        double lat;

        for (int i=0;i<20;i++) {

            lng = SystemConstants.APPLETON_LNG - squareSide /20 + Math.random() * squareSide /20 ;
            lat = SystemConstants.APPLETON_LAT - squareSide /20  + Math.random() * squareSide /20 ;
            locations[i]=new LngLat(lng, lat);

        }
        return Stream.of(locations);
    }
    @ParameterizedTest
    @MethodSource("getLocations")
    public void testRandomPathCalculation(LngLat start) {

        LngLat destination=new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
        boolean inNoFlyZone = false;
        for (NamedRegion region : regions) {
            if (lngLatHandling.isInRegion(start, region)) {
                inNoFlyZone = true;
                break;
            }
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            // Submit the path calculation task with a timeout
            Future<List<LngLat>> future = executor.submit(() ->
                    pathFinding.findPath(start, destination, regions, lngLatHandling)
            );
            List<LngLat> path;
            try {
                path = future.get(60, TimeUnit.SECONDS); // Timeout of 60 seconds

            } catch (TimeoutException e) {
                throw new AssertionError("Path calculation timed out");
            }
            assertTrue("Path smaller than distance",lngLatHandling.distanceTo(start,destination)<=path.size()*SystemConstants.DRONE_MOVE_DISTANCE+SystemConstants.DRONE_IS_CLOSE_DISTANCE);
            assertTrue("Path is empty", !path.isEmpty());
            assertTrue("Wrong start",path.get(0).equals(start));
            assertTrue("Wrong destination",lngLatHandling.distanceTo(path.get(path.size()-1),destination)<=SystemConstants.DRONE_IS_CLOSE_DISTANCE);
            boolean inCentral = false;
            assertFalse(inNoFlyZone, "Start point in no fly zone");
            for (int i=0;i<path.size()-1;i++){
                assertTrue("Path is not valid",
                        lngLatHandling.distanceTo(path.get(i),path.get(i+1))<=SystemConstants.DRONE_MOVE_DISTANCE+SystemConstants.EPSILON_ERROR);
                for (NamedRegion region : regions) {
                    assertFalse(lngLatHandling.isInRegion(path.get(i), region), "Point in no fly zone");
                    if (!inCentral){
                        inCentral=lngLatHandling.isInRegion(path.get(i), centralRegion);
                    }else {
                        assertTrue("Point not in central region", lngLatHandling.isInRegion(path.get(i), centralRegion));
                    }
                }
                if (i>0){
                    assertTrue("Not valid distance between steps",
                            lngLatHandling.distanceTo(path.get(i),path.get(i-1))<SystemConstants.DRONE_MOVE_DISTANCE+SystemConstants.EPSILON_ERROR&&
                                    lngLatHandling.distanceTo(path.get(i),path.get(i-1))>SystemConstants.DRONE_MOVE_DISTANCE-SystemConstants.EPSILON_ERROR);
                }
            }


        }catch(Exception e){

            if (e.getMessage().equals("java.lang.RuntimeException: Start point is in obstacle")){
                assertTrue("java.lang.RuntimeException: Start point not in no fly zone but rejected for that", inNoFlyZone);
            }
            else fail("Unknown error with message : " + e.getMessage());

        }

    }
    private Stream<Restaurant> getRestaurants(){
        return Stream.of(Objects.requireNonNull(restTemplate.getForObject(SystemConstants.RESTAURANTS_URL, Restaurant[].class)));
    }

    @ParameterizedTest
    @MethodSource("getRestaurants")
    public void testPathToRestaurant(Restaurant restaurant){
        LngLat destination=new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
        LngLat start=restaurant.location();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            // Submit the path calculation task with a timeout
            Future<List<LngLat>> future = executor.submit(() ->
                    pathFinding.findPath(start, destination, regions, lngLatHandling)
            );

            List<LngLat> path;
            try {
                path = future.get(60, TimeUnit.SECONDS); // Timeout of 60 seconds

            } catch (TimeoutException e) {
                throw new AssertionError("Path calculation timed out");
            }
            assertTrue("Path smaller than distance",lngLatHandling.distanceTo(start,destination)<=path.size()*SystemConstants.DRONE_MOVE_DISTANCE+SystemConstants.DRONE_IS_CLOSE_DISTANCE);
            assertTrue("Wrong start",path.get(0).equals(start));
            assertTrue("Wrong destination",lngLatHandling.distanceTo(path.get(path.size()-1),destination)<=SystemConstants.DRONE_IS_CLOSE_DISTANCE);
            assertTrue("Path is empty", !path.isEmpty());
            boolean inCentral = false;
            for (int i=0;i<path.size()-1;i++){
                assertTrue("Path is not valid",
                        lngLatHandling.distanceTo(path.get(i),path.get(i+1))<=SystemConstants.DRONE_MOVE_DISTANCE+SystemConstants.EPSILON_ERROR);
                for (NamedRegion region : regions) {
                    assertFalse(lngLatHandling.isInRegion(path.get(i), region), "Point in no fly zone");
                    if (!inCentral){
                        inCentral=lngLatHandling.isInRegion(path.get(i), centralRegion);
                    }else {
                        assertTrue("Point not in central region", lngLatHandling.isInRegion(path.get(i), centralRegion));
                    }
                }
                if (i>0){
                    assertTrue("Not valid distance between steps",
                            lngLatHandling.distanceTo(path.get(i),path.get(i-1))<SystemConstants.DRONE_MOVE_DISTANCE+SystemConstants.EPSILON_ERROR&&
                            lngLatHandling.distanceTo(path.get(i),path.get(i-1))>SystemConstants.DRONE_MOVE_DISTANCE-SystemConstants.EPSILON_ERROR);
                }
            }
        }catch(Exception e){
            fail("Error with message : " + e.getMessage());
        }
    }

}
