package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Data.NamedRegion;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionAnglePair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionPair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionRegionPair;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.Services.LngLatService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for controller-level logic without Spring Boot context
 * These tests validate the business logic that controllers use
 */
@DisplayName("Controller Logic Tests")
public class ControllerTests {

    private final LngLatHandling lngLatHandler = new LngLatService();

    @Test
    @DisplayName("UUID returns student ID")
    public void testUuidLogic() {
        String uuid = SystemConstants.STUDENT_ID;
        assertNotNull(uuid);
        assertFalse(uuid.isEmpty());
    }

    @Test
    @DisplayName("Distance calculation for controller endpoint")
    public void testDistanceToLogic() {
        LngLat pos1 = new LngLat(0.0, 0.0);
        LngLat pos2 = new LngLat(3.0, 4.0);

        double distance = lngLatHandler.distanceTo(pos1, pos2);

        assertEquals(5.0, distance, 0.0001);
    }

    @Test
    @DisplayName("Is close to calculation for controller endpoint")
    public void testIsCloseToLogic() {
        LngLat pos1 = new LngLat(0.0, 0.0);
        LngLat pos2 = new LngLat(0.00001, 0.00001);

        boolean isClose = lngLatHandler.isCloseTo(pos1, pos2);

        assertTrue(isClose);
    }

    @Test
    @DisplayName("Is close to returns false when far")
    public void testIsNotCloseToLogic() {
        LngLat pos1 = new LngLat(0.0, 0.0);
        LngLat pos2 = new LngLat(1.0, 1.0);

        boolean isClose = lngLatHandler.isCloseTo(pos1, pos2);

        assertFalse(isClose);
    }

    @Test
    @DisplayName("Next position calculation for controller endpoint")
    public void testNextPositionLogic() {
        LngLat start = new LngLat(0.0, 0.0);
        double angle = 90.0; // North

        LngLat next = lngLatHandler.nextPosition(start, angle);

        assertNotNull(next);
        assertTrue(next.lat() > start.lat()); // Moved north
        assertEquals(start.lng(), next.lng(), SystemConstants.EPSILON_ERROR); // Same longitude
    }

    @Test
    @DisplayName("Next position with hovering angle stays in place")
    public void testNextPositionHovering() {
        LngLat start = new LngLat(5.0, 5.0);

        LngLat next = lngLatHandler.nextPosition(start, SystemConstants.DRONE_HOVERING_ANGLE);

        assertEquals(start, next);
    }

    @Test
    @DisplayName("Is in region calculation for controller endpoint - inside")
    public void testIsInRegionLogicInside() {
        NamedRegion region = new NamedRegion("test", List.of(
            new LngLat(0.0, 0.0),
            new LngLat(1.0, 0.0),
            new LngLat(0.5, 1.0),
            new LngLat(0.0, 0.0)
        ));
        LngLat position = new LngLat(0.5, 0.3);

        boolean inRegion = lngLatHandler.isInRegion(position, region);

        assertTrue(inRegion);
    }

    @Test
    @DisplayName("Is in region calculation for controller endpoint - outside")
    public void testIsInRegionLogicOutside() {
        NamedRegion region = new NamedRegion("test", List.of(
            new LngLat(0.0, 0.0),
            new LngLat(1.0, 0.0),
            new LngLat(0.5, 1.0),
            new LngLat(0.0, 0.0)
        ));
        LngLat position = new LngLat(2.0, 2.0);

        boolean inRegion = lngLatHandler.isInRegion(position, region);

        assertFalse(inRegion);
    }

    @Test
    @DisplayName("Position pair creation with valid positions")
    public void testPositionPairCreation() {
        LngLat pos1 = new LngLat(0.0, 0.0);
        LngLat pos2 = new LngLat(1.0, 1.0);

        PositionPair pair = new PositionPair(pos1, pos2);

        assertNotNull(pair);
        assertEquals(pos1, pair.position1());
        assertEquals(pos2, pair.position2());
    }

    @Test
    @DisplayName("Position angle pair creation with valid values")
    public void testPositionAnglePairCreation() {
        LngLat pos = new LngLat(0.0, 0.0);
        double angle = 45.0;

        PositionAnglePair pair = new PositionAnglePair(pos, angle);

        assertNotNull(pair);
        assertEquals(pos, pair.start());
        assertEquals(angle, pair.angle());
    }

    @Test
    @DisplayName("Position region pair creation with valid values")
    public void testPositionRegionPairCreation() {
        LngLat pos = new LngLat(0.0, 0.0);
        NamedRegion region = new NamedRegion("test", List.of(
            new LngLat(0.0, 0.0),
            new LngLat(1.0, 0.0),
            new LngLat(0.5, 1.0),
            new LngLat(0.0, 0.0)
        ));

        PositionRegionPair pair = new PositionRegionPair(pos, region);

        assertNotNull(pair);
        assertEquals(pos, pair.position());
        assertEquals(region, pair.region());
    }

    @Test
    @DisplayName("Controller logic handles null validation in position pair")
    public void testPositionPairNullValidation() {
        LngLat pos = new LngLat(0.0, 0.0);

        assertThrows(Exception.class, () -> new PositionPair(null, pos));
        assertThrows(Exception.class, () -> new PositionPair(pos, null));
    }

    @Test
    @DisplayName("Controller logic handles null validation in position angle pair")
    public void testPositionAnglePairNullValidation() {
        LngLat pos = new LngLat(0.0, 0.0);

        assertThrows(Exception.class, () -> new PositionAnglePair(null, 45.0));
        assertThrows(Exception.class, () -> new PositionAnglePair(pos, null));
    }

    @Test
    @DisplayName("Controller logic handles invalid angle values")
    public void testPositionAnglePairInvalidAngle() {
        LngLat pos = new LngLat(0.0, 0.0);

        assertThrows(Exception.class, () -> new PositionAnglePair(pos, -1.0));
        assertThrows(Exception.class, () -> new PositionAnglePair(pos, 360.0));
    }

    @Test
    @DisplayName("Controller logic handles null validation in position region pair")
    public void testPositionRegionPairNullValidation() {
        LngLat pos = new LngLat(0.0, 0.0);
        NamedRegion region = new NamedRegion("test", List.of(
            new LngLat(0.0, 0.0),
            new LngLat(1.0, 0.0),
            new LngLat(0.5, 1.0),
            new LngLat(0.0, 0.0)
        ));

        assertThrows(Exception.class, () -> new PositionRegionPair(null, region));
        assertThrows(Exception.class, () -> new PositionRegionPair(pos, null));
    }
}
