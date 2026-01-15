package uk.ac.ed.inf.pizzadronz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.ac.ed.inf.pizzadronz.Constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.Constants.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Controllers.LngLatController;
import uk.ac.ed.inf.pizzadronz.Controllers.OrderValidationController;
import uk.ac.ed.inf.pizzadronz.Data.*;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionAnglePair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionPair;
import uk.ac.ed.inf.pizzadronz.RequestBodies.PositionRegionPair;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.OrderValidation;
import uk.ac.ed.inf.pizzadronz.Services.LngLatService;
import uk.ac.ed.inf.pizzadronz.Services.OrderValidationService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Controller method tests
 * Tests controller business logic directly (Spring Boot context conflicts prevent @WebMvcTest HTTP testing)
 * This validates controller methods work correctly when called programmatically
 */
@DisplayName("Controller Method Tests")
public class ControllerMethodTests {

    private LngLatHandling lngLatHandler;
    private LngLatController lngLatController;
    private OrderValidation orderValidation;
    private OrderValidationController orderValidationController;

    @BeforeEach
    void setUp() {
        lngLatHandler = new LngLatService();
        lngLatController = new LngLatController(lngLatHandler);
        orderValidation = new OrderValidationService();
        orderValidationController = new OrderValidationController(orderValidation);
    }

    @Nested
    @DisplayName("LngLatController Method Tests")
    class LngLatControllerMethodTests {

        @Test
        @DisplayName("uuid() should return student ID")
        void uuidReturnsStudentId() {
            String result = lngLatController.uuid();

            assertNotNull(result, "Student ID should not be null");
            assertFalse(result.isEmpty(), "Student ID should not be empty");
            assertEquals(SystemConstants.STUDENT_ID, result, "Should return correct student ID");
        }

        @Test
        @DisplayName("distanceTo() should calculate correct Euclidean distance")
        void distanceToCalculatesCorrectly() {
            PositionPair positions = new PositionPair(
                    new LngLat(0.0, 0.0),
                    new LngLat(3.0, 4.0)
            );

            ResponseEntity<Double> response = lngLatController.distanceTo(positions);

            assertTrue(response.getStatusCode().is2xxSuccessful(), "Should return 200 OK");
            assertNotNull(response.getBody(), "Response body should not be null");
            assertEquals(5.0, response.getBody(), 0.0001, "Should calculate correct distance (3-4-5 triangle)");
        }

        @Test
        @DisplayName("distanceTo() should handle zero distance")
        void distanceToHandlesZeroDistance() {
            PositionPair positions = new PositionPair(
                    new LngLat(1.0, 1.0),
                    new LngLat(1.0, 1.0)
            );

            ResponseEntity<Double> response = lngLatController.distanceTo(positions);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(0.0, response.getBody(), 0.0001, "Same position should have zero distance");
        }

        @Test
        @DisplayName("isCloseTo() should return true for nearby positions")
        void isCloseToReturnsTrueWhenNear() {
            PositionPair positions = new PositionPair(
                    new LngLat(0.0, 0.0),
                    new LngLat(0.00001, 0.00001)
            );

            boolean result = lngLatController.isCloseTo(positions);

            assertTrue(result, "Positions within close distance should return true");
        }

        @Test
        @DisplayName("isCloseTo() should return false for distant positions")
        void isCloseToReturnsFalseWhenFar() {
            PositionPair positions = new PositionPair(
                    new LngLat(0.0, 0.0),
                    new LngLat(1.0, 1.0)
            );

            boolean result = lngLatController.isCloseTo(positions);

            assertFalse(result, "Distant positions should return false");
        }

        @Test
        @DisplayName("nextPosition() should calculate next position for valid angle")
        void nextPositionCalculatesCorrectly() {
            PositionAnglePair positionAngle = new PositionAnglePair(
                    new LngLat(0.0, 0.0),
                    90.0  // North
            );

            ResponseEntity<LngLat> response = lngLatController.nextPosition(positionAngle);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertNotNull(response.getBody());
            LngLat result = response.getBody();

            assertTrue(result.lat() > 0.0, "Moving north should increase latitude");
            assertEquals(0.0, result.lng(), SystemConstants.EPSILON_ERROR, "Moving north should not change longitude");
        }

        @Test
        @DisplayName("nextPosition() should handle hovering angle")
        void nextPositionHandlesHovering() {
            PositionAnglePair positionAngle = new PositionAnglePair(
                    new LngLat(5.0, 5.0),
                    SystemConstants.DRONE_HOVERING_ANGLE
            );

            ResponseEntity<LngLat> response = lngLatController.nextPosition(positionAngle);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(new LngLat(5.0, 5.0), response.getBody(), "Hovering should not change position");
        }

        @Test
        @DisplayName("isInRegion() should return true for position inside region")
        void isInRegionReturnsTrueForPositionInside() {
            NamedRegion region = new NamedRegion("test", List.of(
                    new LngLat(0.0, 0.0),
                    new LngLat(1.0, 0.0),
                    new LngLat(0.5, 1.0),
                    new LngLat(0.0, 0.0)
            ));
            PositionRegionPair pair = new PositionRegionPair(
                    new LngLat(0.5, 0.3),
                    region
            );

            ResponseEntity<Boolean> result = lngLatController.isInRegion(pair);

            assertTrue(result.getStatusCode().is2xxSuccessful());
            assertNotNull(result.getBody());
            assertTrue(result.getBody(), "Position inside triangle region should return true");
        }

        @Test
        @DisplayName("isInRegion() should return false for position outside region")
        void isInRegionReturnsFalseForPositionOutside() {
            NamedRegion region = new NamedRegion("test", List.of(
                    new LngLat(0.0, 0.0),
                    new LngLat(1.0, 0.0),
                    new LngLat(0.5, 1.0),
                    new LngLat(0.0, 0.0)
            ));
            PositionRegionPair pair = new PositionRegionPair(
                    new LngLat(2.0, 2.0),
                    region
            );

            ResponseEntity<Boolean> result = lngLatController.isInRegion(pair);

            assertTrue(result.getStatusCode().is2xxSuccessful());
            assertNotNull(result.getBody());
            assertFalse(result.getBody(), "Position outside triangle region should return false");
        }

        @Test
        @DisplayName("All controller methods should handle null positions gracefully")
        void shouldHandleNullPositionsGracefully() {
            // These should throw exceptions due to validation
            assertThrows(Exception.class, () -> new PositionPair(null, new LngLat(0.0, 0.0)),
                    "PositionPair should reject null position1");
            assertThrows(Exception.class, () -> new PositionPair(new LngLat(0.0, 0.0), null),
                    "PositionPair should reject null position2");
            assertThrows(Exception.class, () -> new PositionAnglePair(null, 45.0),
                    "PositionAnglePair should reject null position");
            assertThrows(Exception.class, () -> new PositionRegionPair(null,
                    new NamedRegion("test", List.of(new LngLat(0.0, 0.0)))),
                    "PositionRegionPair should reject null position");
        }
    }

    @Nested
    @DisplayName("OrderValidationController Method Tests")
    class OrderValidationControllerMethodTests {

        @Test
        @DisplayName("validateOrder() should handle valid order")
        void validateOrderHandlesValidOrder() {
            // Note: This test requires external REST endpoints to be available
            // In a production environment, we would mock the REST template
            Order order = new Order(
                    "TEST001",
                    LocalDate.now(),
                    2400,
                    new Pizza[]{
                            new Pizza("Margherita", 1400),
                            new Pizza("Calzone", 1000)
                    },
                    new CreditCardInformation(
                            "1234567812345670",
                            "12/28",
                            "123"
                    )
            );

            ResponseEntity<OrderValidationResult> response = orderValidationController.validateOrder(order);

            assertNotNull(response, "Response should not be null");
            assertTrue(response.getStatusCode().is2xxSuccessful(), "Should return 200 OK");
            assertNotNull(response.getBody(), "Response body should not be null");
            assertNotNull(response.getBody().orderStatus(), "Order status should not be null");
            assertNotNull(response.getBody().orderValidationCode(), "Validation code should not be null");
        }

        @Test
        @DisplayName("validateOrder() should reject order with invalid credit card number")
        void validateOrderRejectsInvalidCardNumber() {
            Order order = new Order(
                    "TEST002",
                    LocalDate.now(),
                    2400,
                    new Pizza[]{new Pizza("Margherita", 1400)},
                    new CreditCardInformation(
                            "1234",  // Too short
                            "12/28",
                            "123"
                    )
            );

            ResponseEntity<OrderValidationResult> response = orderValidationController.validateOrder(order);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            OrderValidationResult result = response.getBody();
            assertNotNull(result);
            assertEquals(OrderStatus.INVALID, result.orderStatus(), "Should be INVALID");
            assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, result.orderValidationCode(),
                    "Should indicate card number is invalid");
        }

        @Test
        @DisplayName("validateOrder() should reject order with invalid CVV")
        void validateOrderRejectsInvalidCVV() {
            Order order = new Order(
                    "TEST003",
                    LocalDate.now(),
                    2400,
                    new Pizza[]{new Pizza("Margherita", 1400)},
                    new CreditCardInformation(
                            "1234567812345670",
                            "12/28",
                            "12"  // Too short
                    )
            );

            ResponseEntity<OrderValidationResult> response = orderValidationController.validateOrder(order);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            OrderValidationResult result = response.getBody();
            assertNotNull(result);
            assertEquals(OrderStatus.INVALID, result.orderStatus());
            assertEquals(OrderValidationCode.CVV_INVALID, result.orderValidationCode());
        }

        @Test
        @DisplayName("validateOrder() should reject order with expired credit card")
        void validateOrderRejectsExpiredCard() {
            Order order = new Order(
                    "TEST004",
                    LocalDate.now(),
                    2400,
                    new Pizza[]{new Pizza("Margherita", 1400)},
                    new CreditCardInformation(
                            "1234567812345670",
                            "01/20",  // Expired
                            "123"
                    )
            );

            ResponseEntity<OrderValidationResult> response = orderValidationController.validateOrder(order);

            assertTrue(response.getStatusCode().is2xxSuccessful());
            OrderValidationResult result = response.getBody();
            assertNotNull(result);
            assertEquals(OrderStatus.INVALID, result.orderStatus());
            assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, result.orderValidationCode());
        }

        @Test
        @DisplayName("Exception handler should return UNDEFINED for unexpected errors")
        void exceptionHandlerReturnsUndefined() {
            OrderValidationController.ExceptionHandler handler =
                    new OrderValidationController.ExceptionHandler();

            ResponseEntity<OrderValidationResult> response =
                    handler.handleException(new RuntimeException("Test error"));

            assertTrue(response.getStatusCode().is2xxSuccessful());
            OrderValidationResult result = response.getBody();
            assertNotNull(result);
            assertEquals(OrderStatus.INVALID, result.orderStatus());
            assertEquals(OrderValidationCode.UNDEFINED, result.orderValidationCode());
        }
    }
}
