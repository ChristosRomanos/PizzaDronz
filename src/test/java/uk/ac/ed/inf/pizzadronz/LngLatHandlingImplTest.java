package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.*;
import uk.ac.ed.inf.pizzadronz.constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.data.LngLat;
import uk.ac.ed.inf.pizzadronz.data.NamedRegion;
import uk.ac.ed.inf.pizzadronz.Services.LngLatService;
import uk.ac.ed.inf.pizzadronz.ServiceInterface.LngLatHandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class LngLatHandlingImplTest {
    private static final double epsilon = SystemConstants.EPSILON_ERROR;
    private static LngLatHandling lngLatHandler;

    @BeforeAll
    public static void beforeAll() {
        lngLatHandler = new LngLatService();
    }


    @Nested
    class DistanceToTests {
        @Test
        public void testWithZeroDistance() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(0.0, 0.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(distance, 0.0);
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
        public void testWithHorizontalDistance() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(0.0, 1.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(distance, 1.0);
        }

        @Test
        public void testWithVerticalDistance() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(1.0, 0.0);
            double distance = lngLatHandler.distanceTo(start, end);
            assertEquals(distance, 1.0);
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
        public void testAtBelowThreshold() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(SystemConstants.DRONE_IS_CLOSE_DISTANCE / 1.000001, 0.0);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertTrue(isClose);
        }

        @RepeatedTest(20)
        public void testAtBelowThresholdDiagonalRandom() {
            double longStart = Math.random();
            double latStart = Math.random();
            LngLat start = new LngLat(longStart, latStart);
            double magnitude = SystemConstants.DRONE_IS_CLOSE_DISTANCE / 1.000001;
            double angle = Math.PI / (Math.random() * 10);
            double dx = magnitude * Math.cos(angle);
            double dy = magnitude * Math.sin(angle);
            LngLat end = new LngLat(longStart + dx, latStart + dy);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertTrue(isClose);
        }

        @Test
        public void testAtAboveThreshold() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat end = new LngLat(SystemConstants.DRONE_IS_CLOSE_DISTANCE, 0.0);
            boolean isClose = lngLatHandler.isCloseTo(start, end);
            assertFalse(isClose);
        }
    }

    @Nested
    class NextPositionTests {

        @Test
        public void testHovering() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat next = lngLatHandler.nextPosition(start, SystemConstants.DRONE_HOVERING_ANGLE);
            assertEquals(next, start);
        }

        @Test
        public void testEast() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat next = lngLatHandler.nextPosition(start, 0.0);

            assertAll("final position is correct",
                    () -> assertEquals(next.lng(), start.lng() + SystemConstants.DRONE_MOVE_DISTANCE, epsilon),
                    () -> assertEquals(next.lat(), start.lat(), epsilon));
        }

        @Test
        public void testWest() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat next = lngLatHandler.nextPosition(start, 180.0);

            assertAll("final position is correct",
                    () -> assertEquals(next.lng(), start.lng() - SystemConstants.DRONE_MOVE_DISTANCE, epsilon),
                    () -> assertEquals(next.lat(), start.lat(), epsilon));
        }

        @Test
        public void testNorth() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat next = lngLatHandler.nextPosition(start, 90.0);

            assertAll("final position is correct",
                    () -> assertEquals(next.lng(), start.lng(), epsilon),
                    () -> assertEquals(next.lat(), start.lat() + SystemConstants.DRONE_MOVE_DISTANCE, epsilon));
        }

        @Test
        public void testSouth() {
            LngLat start = new LngLat(0.0, 0.0);
            LngLat next = lngLatHandler.nextPosition(start, 270.0);

            assertAll("final position is correct",
                    () -> assertEquals(next.lng(), start.lng(), epsilon),
                    () -> assertEquals(next.lat(), start.lat() - SystemConstants.DRONE_MOVE_DISTANCE, epsilon));
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
            boolean isValid = lngLatHandler.validAngle(-1.0);
            assertFalse(isValid);
        }

        @Test
        @Disabled
        public void testWithIncorrectCardinalDirection() {
            boolean isValid = lngLatHandler.validAngle(2);
            assertFalse(isValid);
        }

//        @Test
        @Disabled
//        public void testWithCorrectCardinalDirections() {
//            double multiplier = 360.0 / SystemConstants.DRONE_COMPASS_DIRECTIONS;
//            for (int i = 0; i <= SystemConstants.DRONE_COMPASS_DIRECTIONS; i++) {
//                boolean isValid = lngLatHandler.validAngle(i * multiplier);
//                assertTrue(isValid);
//            }
//        }

        @Test
        public void testOverBoundary() {
            boolean isValid = lngLatHandler.validAngle(361.0);
            assertFalse(isValid);
        }

        @Test
        @Disabled
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