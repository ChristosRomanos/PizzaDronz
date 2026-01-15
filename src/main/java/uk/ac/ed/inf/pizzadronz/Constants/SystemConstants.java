package uk.ac.ed.inf.pizzadronz.Constants;




public final class SystemConstants {


        public static final String STUDENT_ID = "s2149970";
        /**
         *
         * the distance a drone can move in 1 iteration
         */
        public static final double DRONE_MOVE_DISTANCE = 0.00015;

        /**
         * the distance which is considered "close"
         */
        public static final double DRONE_IS_CLOSE_DISTANCE = 0.00015;


        /**
         * the angle the drone is hovering
         */
        public static final double DRONE_HOVERING_ANGLE= 999;

        /**
         * the error margin for double comparison
         */
        public static final double EPSILON_ERROR= Math.pow(10,-12);

        /**
         * the maximum value for longitude
         */
        public static final double MAX_LNG= 180;

        /**
         * the minimum value for longitude
         */
        public static final double MIN_LNG= -180;

        /**
         * the maximum value for latitude
         */
        public static final double MAX_LAT= 90;

        /**
         * the minimum value for latitude
         */
        public static final double MIN_LAT= -90;

        /**
         * the maximum value for angle
         */
        public static final double MAX_ANGLE= 360;

        /**
         * the minimum value for angle
         */
        public static final double MIN_ANGLE= 0;

        /**
         * the delivery fee for each order
         */
        public static final int DELIVERY_FEE = 100;

        /**
         * the maximum number of pizzas in an order
         */
        public static final int MAX_PIZZA_COUNT = 4;

        /**
         * the radius of a node for the A* algorithm
         */
        public static final double NODE_RADIUS = 0.000125;

        /**
         * the Appleton Tower longitude
         */
        public static final double APPLETON_LNG = -3.186874;

        /**
         * the Appleton Tower latitude
         */
        public static final double APPLETON_LAT = 55.944494;

        /**
         * the URL for the restaurants
         */
        public static final String RESTAURANTS_URL = "https://ilp-rest-2024.azurewebsites.net/restaurants";

        /**
         * the URL for the no fly zones
         */
        public static final String NO_FLY_ZONES_URL = "https://ilp-rest-2024.azurewebsites.net/noFlyZones";

        /**
         * the URL for the central area
         */
        public static final String CENTRAL_REGION_URL = "https://ilp-rest-2024.azurewebsites.net/centralArea";

        /**
         * the URL for the orders
         */
        public static final String ORDERS_URL = "https://ilp-rest-2024.azurewebsites.net/orders";

        /**
         * the valid length for credit card number
         */
        public static final int CREDIT_CARD_NUMBER_LENGTH = 16;

        /**
         * the valid length for CVV
         */
        public static final int CVV_LENGTH = 3;

        /**
         * the valid format length for expiry date (MM/YY)
         */
        public static final int EXPIRY_DATE_LENGTH = 5;

        /**
         * the position of the slash in expiry date
         */
        public static final int EXPIRY_DATE_SLASH_POSITION = 2;

}



