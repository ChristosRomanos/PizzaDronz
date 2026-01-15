package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.ac.ed.inf.pizzadronz.Constants.Directions;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Data.NamedRegion;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionAnglePair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionPair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionRegionPair;
import uk.ac.ed.inf.pizzadronz.Services.LngLatService;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("LngLat Handling Tests")
public class LngLatHandlingTests {
    private static final double epsilon = SystemConstants.EPSILON_ERROR;
    private static LngLatHandling lngLatHandler;

    @BeforeAll
    public static void beforeAll() {
        lngLatHandler = new LngLatService();
    }

    @Nested
    @DisplayName("Data Values Validation Tests")
    class DataValuesTest{
        @Test
        public void testLngLatNullValues(){
            assertThrows(Exception.class, () -> new LngLat(null, 0.0));
            assertThrows(Exception.class, () -> new LngLat(0.0, null));
        }
        @Test
        public void testLngLatAtLowerBoundaries(){
            assertThrows(Exception.class, () -> new LngLat(SystemConstants.MIN_LNG-epsilon, 0.0));
            assertThrows(Exception.class, () -> new LngLat(0.0, SystemConstants.MIN_LAT-epsilon));
            LngLat loc=new LngLat(SystemConstants.MIN_LNG, SystemConstants.MIN_LAT);
        }
        @Test
        public void testLngLatAtUpperBoundaries(){
            assertThrows(Exception.class, () -> new LngLat(SystemConstants.MAX_LNG+epsilon, 0.0));
            assertThrows(Exception.class, () -> new LngLat(0.0, SystemConstants.MAX_LAT+epsilon));
            LngLat loc=new LngLat(SystemConstants.MAX_LNG, SystemConstants.MAX_LAT);
        }
        @Test
        public void testNamedRegionNullValues(){
            assertThrows(Exception.class, () -> new NamedRegion(null, List.of(new LngLat[]{new LngLat(0.0, 0.0)})));
            assertThrows(Exception.class, () -> new NamedRegion("na", null));
        }
        @Test
        public void testPositionAngleNullValues(){
            assertThrows(Exception.class, () -> new PositionAnglePair(new LngLat(0.0, 0.0), null));
            assertThrows(Exception.class, () -> new PositionAnglePair(null, 0.0));
        }
        @Test
        public void testPositionAngleInvalidValues(){
            assertThrows(Exception.class, () -> new PositionAnglePair(new LngLat(0.0, 0.0), SystemConstants.MAX_ANGLE));
            assertThrows(Exception.class, () -> new PositionAnglePair(new LngLat(0.0, 0.0), -Math.random()-epsilon));
        }

        @Test
        public void testValidAngles(){
            for(int i=0;i<50;i++){
                new PositionAnglePair(new LngLat(0.0, 0.0), Math.random()*360);
            }
        }

        @Test
        public void testAtValidBoundaries(){
            new PositionAnglePair(new LngLat(0.0, 0.0), SystemConstants.MIN_ANGLE);
            new PositionAnglePair(new LngLat(0.0, 0.0), SystemConstants.MAX_ANGLE-epsilon);
        }

        @Test
        public void testPositionPairNullValues(){
            assertThrows(Exception.class, () -> new PositionPair(null, new LngLat(null,0.0)));
            assertThrows(Exception.class, () -> new PositionPair(new LngLat(0.0, 0.0), null));
        }
        @Test
        public void testPositionRegionNullValues(){
            assertThrows(Exception.class,()->new PositionRegionPair(null,new NamedRegion("na", List.of(new LngLat[]{new LngLat(0.0, 0.0)}))));
            assertThrows(Exception.class,()->new PositionRegionPair(new LngLat(0.0, 0.0),null));}

    }

    @Nested
    class DistanceToTests {
        @Test
        public void testWithZeroDistance() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(0.0, 0.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(0.0, distance);
        }

        @Test
        public void testWithPositives() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(1.0, 1.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(distance, Math.sqrt(2));
        }

        @Test
        public void testWithNegatives() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(-1.0, -1.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(distance, Math.sqrt(2));
        }
        @Test
        public void testWithPositiveAndNegatives() {
            LngLat start = new LngLat(5.0, -2.0);
            LngLat end = new LngLat(-1.0, 3.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(distance, Math.sqrt(61));
        }

        @Test
        public void testWithHorizontalDistance() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(0.0, 1.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(1.0, distance);
        }

        @Test
        public void testWithVerticalDistance() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(1.0, 0.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(1.0, distance);
        }

    }

    @Nested
    class IsCloseToTests {
        @Test
        public void testWithZeroDistance() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(0.0, 0.0);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertTrue(isClose);
        }

        @Test
        public void testJustBelowThreshold() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(SystemConstants.DRONE_IS_CLOSE_DISTANCE / 1.000000000001, 0.0);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertTrue(isClose);
        }
        @Test
        public void testAtThreshold() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(SystemConstants.DRONE_IS_CLOSE_DISTANCE, 0.0);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertFalse(isClose);
        }

        @RepeatedTest(20)
        public void testAtBelowThresholdDiagonalRandom() {
            double lng = Math.random();
            double lat = Math.random();
            LngLat start = new LngLat(lng, lat);
            double distance = SystemConstants.DRONE_IS_CLOSE_DISTANCE / 1.000001;
            double angle = Math.PI / (Math.random() * 20);
            double x = distance * Math.cos(angle);
            double y = distance * Math.sin(angle);
            LngLat end = new LngLat(lng + x, lat + y);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertTrue(isClose);
        }

        @Test
        public void testAtAboveThresholdDiagonalRandom() {
            double lng = Math.random();
            double lat = Math.random();
            LngLat start = new LngLat(lng, lat);
            double distance = SystemConstants.DRONE_IS_CLOSE_DISTANCE*1.00000001;
            double angle = Math.PI / (Math.random() * 20);
            double x = distance * Math.cos(angle);
            double y = distance * Math.sin(angle);
            LngLat end = new LngLat(lng + x, lat + y);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertFalse(isClose);
        }
    }

    @Nested
    class NextPositionTests {
        @Test
        public void testNullValues(){
            LngLat start = new LngLat(0.0, 0.0);
            assertThrows(Exception.class, () -> lngLatHandler.nextPosition(null, 0.0));
            assertThrows(Exception.class, () -> lngLatHandler.nextPosition(start, null));
        }
        @Test
        public void testInvalidAngles() {
            LngLat start = new LngLat(0.0, 0.0);
            Double angle= Math.random()*360+360;
            double angle2;
            do {
                angle2 = -Math.random() * 360;
            }while (angle2 >= 0);
            assertThrows(Exception.class, () -> lngLatHandler.nextPosition(start, angle));
            Double finalAngle = angle2;
            assertThrows(Exception.class, () -> lngLatHandler.nextPosition(start, finalAngle));
        }
        @Test
        public void testAtUpperThreshold(){
            LngLat start = new LngLat(0.0, 0.0);
            assertThrows(Exception.class, () -> lngLatHandler.nextPosition(start, SystemConstants.MAX_ANGLE));
        }

        @Test
        public void testHoveringAngle() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat next = lngLatHandler.nextPosition(start, SystemConstants.DRONE_HOVERING_ANGLE);
            assertEquals(next, start);
        }

        private static Stream<Arguments> provideDirectionsAndExpectedResults() {
            return Stream.of(
                    Arguments.of(Directions.N, Math.PI/2),
                    Arguments.of(Directions.NNE,3*Math.PI/8),
                    Arguments.of(Directions.NE,Math.PI/4),
                    Arguments.of(Directions.ENE,Math.PI/8),
                    Arguments.of(Directions.ESE, 15*Math.PI/8),
                    Arguments.of(Directions.SE, 7*Math.PI/4),
                    Arguments.of(Directions.SSE,13*Math.PI/8),
                    Arguments.of(Directions.S,3*Math.PI/2),
                    Arguments.of(Directions.SSW, 11*Math.PI/8),
                    Arguments.of(Directions.SW, 5*Math.PI/4),
                    Arguments.of(Directions.WSW, 9*Math.PI/8),
                    Arguments.of(Directions.W, Math.PI),
                    Arguments.of(Directions.WNW,7*Math.PI/8),
                    Arguments.of(Directions.NW, 3*Math.PI/4),
                    Arguments.of(Directions.NNW, 5*Math.PI/8)
            );
        }

        @ParameterizedTest
        @MethodSource("provideDirectionsAndExpectedResults")
        public void testNextPositionForEachDirection(Directions direction, double angleInRadians) {
            // Arrange
            LngLat initialPosition = new LngLat(0.0, 0.0);

            // Act
            LngLat newPosition = lngLatHandler.nextPosition(initialPosition, direction.getAngle());

            // Assert
            assertEquals(initialPosition.lng()+SystemConstants.DRONE_MOVE_DISTANCE*Math.cos(angleInRadians),
                    newPosition.lng(),epsilon,"Longitude mismatch for direction: " + direction);
            assertEquals(initialPosition.lat()+SystemConstants.DRONE_MOVE_DISTANCE*Math.sin(angleInRadians),
                    newPosition.lat(), epsilon, "Latitude mismatch for direction: " + direction);
        }


    }

    @Nested
    class IsValidAngleTests {

        @RepeatedTest(20)
        public void testWithRandomAngles() {
            double angle = Math.random() * 360;
            boolean isValid = lngLatHandler.validAngle(angle);
            assertTrue(isValid);
        }

        @Test
        public void testWithNegative() {
            double angle=Math.random()*359-360;
            while(angle >= 0){
                boolean isValid = lngLatHandler.validAngle(angle);
                assertTrue(isValid);
                angle = Math.random()*359-360;
            }
            boolean isValid = lngLatHandler.validAngle(angle);
            assertFalse(isValid);
        }

        @Test
        public void testOverBoundary() {
            double angle=Math.random()*1000;
            while(angle < 360){

                boolean isValid = lngLatHandler.validAngle(angle);
                assertTrue(isValid);
                angle = Math.random()*1000;
            }
            boolean isValid = lngLatHandler.validAngle(angle);
            assertFalse(isValid);
        }

        @Test
        public void testHoveringAngle() {
            boolean isValid = lngLatHandler.validAngle(SystemConstants.DRONE_HOVERING_ANGLE);
            assertTrue(isValid);
        }
    }

    @Nested
    class IsInRegionTests {
        @Test
        public void testOutsideRectangle() {
            LngLat position = new LngLat(0.0, 0.0);
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{new LngLat(-3.192473, 55.946233), new LngLat(-3.192473, 55.942617), new LngLat(-3.184319, 55.942617), new LngLat(-3.184319, 55.946233), new LngLat(-3.192473, 55.946233)}));
            boolean inRegion = lngLatHandler.isInRegion(position, region);
            assertFalse(inRegion);
        }

        @TestFactory
        public Stream<DynamicTest> testInsideRectangle() {
            double minX = 1.;
            double maxX = 2.;
            double minY = 1.;
            double maxY = 2.;
            NamedRegion triangle = new NamedRegion("na", List.of(new LngLat[]{new LngLat(minX, minY), new LngLat(maxX, minY), new LngLat(maxX, maxY), new LngLat(minX, maxY), new LngLat(minX, minY)}));

            ArrayList<LngLat> vertices = new ArrayList<>();
            vertices.add(new LngLat(minX, minY));
            vertices.add(new LngLat(maxX, minY));
            vertices.add(new LngLat(maxX, maxY));
            vertices.add(new LngLat(minX, maxY));

            for (int i = 0; i < 100; i++) {
                double dx = Math.random();
                double dy = Math.random();
                vertices.add(new LngLat(minX + dx, minY + dy));
            }


            return vertices.stream().map(
                    entry -> dynamicTest(entry.toString(), () -> {
                        boolean inRegion = lngLatHandler.isInRegion(entry, triangle);
                        assertTrue(inRegion);
                    })
            );
        }

        @TestFactory
        public Stream<DynamicTest> testOutsideTriangle() {
            LngLat[] vertices = new LngLat[]{
                    new LngLat(0.0, 1.0),
                    new LngLat(2.01, 2.0),
                    new LngLat(3.01, 1.0)};
            NamedRegion triangle = new NamedRegion("na", List.of(new LngLat[]{new LngLat(1.0, 1.0), new LngLat(2.0, 2.0), new LngLat(3.0, 1.0), new LngLat(1.0, 1.0)}));

            return Arrays.stream(vertices).map(
                    entry -> dynamicTest(entry.toString(), () -> {
                        boolean inRegion = lngLatHandler.isInRegion(entry, triangle);
                        assertFalse(inRegion);
                    })
            );
        }

        @TestFactory
        public Stream<DynamicTest> testInsideTriangle() {
            LngLat[] vertices = new LngLat[]{
                    new LngLat(1.0, 1.0),
                    new LngLat(2.0, 2.0),
                    new LngLat(3.0, 1.0),
                    new LngLat(1.5, 1.5)};
            NamedRegion triangle = new NamedRegion("na", List.of(new LngLat[]{new LngLat(1.0, 1.0), new LngLat(2.0, 2.0), new LngLat(3.0, 1.0), new LngLat(1.0, 1.0)}));
            return Arrays.stream(vertices).map(
                    entry -> dynamicTest(entry.toString(), () -> {
                        boolean inRegion = lngLatHandler.isInRegion(entry, triangle);
                        assertTrue(inRegion);
                    })
            );
        }


        @Test
        public void testPointInPolygonIntersectionOnEdge() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{
                    new LngLat(1.0, 2.0),
                    new LngLat(0.0, 1.0),
                    new LngLat(3.0, 0.0),
                    new LngLat(3.0, 1.5),
                    new LngLat(1.0, 2.0),
            }));
            LngLat position = new LngLat(1.25,1.5);
            boolean inRegion = lngLatHandler.isInRegion(position, region);
            assertTrue(inRegion);
        }

        @Test
        public void testPointOutsidePolygonIntersectionOnEdge() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{
                    new LngLat(1.0, 2.0),
                    new LngLat(0.0, 1.0),
                    new LngLat(3.0, 0.0),
                    new LngLat(3.0, 1.5),
                    new LngLat(1.0, 2.0),
            }));
            LngLat position = new LngLat(0.49,1.5);
            boolean inRegion = lngLatHandler.isInRegion(position, region);
            assertFalse(inRegion);
        }
        @Test
        public void testInvalidRegion() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{new LngLat(1.0, 1.0), new LngLat(2.0, 2.0), new LngLat(1.0, 1.0)}));
            assertThrows(RuntimeException.class, () -> lngLatHandler.isInRegion(new LngLat(1.5, 1.5), region));
        }

    }

    @Nested
    class IsValidRegionTests {
        @Test
        public void testWithLessThanFourVertices() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{new LngLat(1.0, 1.0), new LngLat(2.0, 2.0), new LngLat(1.0, 1.0)}));
            boolean isValid = lngLatHandler.validRegion(region);
            assertFalse(isValid);
        }

        @Test
        public void testWithoutClosingVertex() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{new LngLat(1.0, 1.0), new LngLat(1.0, 0.0), new LngLat(2.0, 0.0), new LngLat(2.0, 1.0)}));
            boolean isValid = lngLatHandler.validRegion(region);
            assertFalse(isValid);
        }

        @Test
        public void testWithThreeVerticesOnSameLine() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{new LngLat(1.0, 1.0), new LngLat(2.0, 2.0), new LngLat(3.0, 3.0)}));
            boolean isValid = lngLatHandler.validRegion(region);
            assertFalse(isValid);
        }

        @Test
        public void testValidTriangle() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{new LngLat(0.0, 0.0), new LngLat(1.0, 0.0), new LngLat(0.5, 1.0), new LngLat(0.0, 0.0)}));
            boolean isValid = lngLatHandler.validRegion(region);
            assertTrue(isValid);
        }

        @Test
        public void testValidSquare() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{new LngLat(0.0, 0.0), new LngLat(1.0, 0.0), new LngLat(1.0, 1.0), new LngLat(0.0, 1.0), new LngLat(0.0, 0.0)}));
            boolean isValid = lngLatHandler.validRegion(region);
            assertTrue(isValid);
        }

        @Test
        public void testValidPentagon() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{
                    new LngLat(0.0, 0.0), new LngLat(1.0, 0.0), new LngLat(1.5, 1.0), new LngLat(0.5, 1.5), new LngLat(-0.5, 1.0), new LngLat(0.0, 0.0)
            }));
            boolean isValid = lngLatHandler.validRegion(region);
            assertTrue(isValid);
        }

        @Test
        public void testPolygonWithDuplicatePoints() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{
                    new LngLat(0.0, 0.0), new LngLat(1.0, 0.0), new LngLat(1.0, 1.0), new LngLat(1.0, 1.0), new LngLat(0.0, 1.0), new LngLat(0.0, 0.0)
            }));
            boolean isValid = lngLatHandler.validRegion(region);
            assertTrue(isValid);
        }

        @Test
        public void testConcavePolygon() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{
                    new LngLat(0.0, 0.0), new LngLat(2.0, 0.0), new LngLat(2.0, 2.0), new LngLat(1.0, 1.0), new LngLat(0.0, 2.0), new LngLat(0.0, 0.0)
            }));
            assertTrue(lngLatHandler.validRegion(region));
        }

        @Test
        public void testPolygonWithVeryClosePoints() {
            NamedRegion region = new NamedRegion("na", List.of(new LngLat[]{
                    new LngLat(0.0, 0.0), new LngLat(1.0, 0.0), new LngLat(1.0, 1.0), new LngLat(1.0, 1.0000000001), new LngLat(0.0, 1.0), new LngLat(0.0, 0.0)
            }));
            assertTrue(lngLatHandler.validRegion(region));
        }

        @Test
        public void testPolygonWithManyPoints() {
            LngLat[] vertices = new LngLat[101];
            for (int i = 0; i < 100; i++) {
                double angle = 2 * Math.PI * i / 100;
                vertices[i] = new LngLat(Math.cos(angle), Math.sin(angle));
            }
            vertices[100] = vertices[0];
            NamedRegion region = new NamedRegion("na", List.of(vertices));
            assertTrue(lngLatHandler.validRegion(region));
        }


    }
}