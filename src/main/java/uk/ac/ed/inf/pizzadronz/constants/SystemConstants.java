package uk.ac.ed.inf.pizzadronz.constants;


import java.net.URI;

public final class SystemConstants {

        /**
         * the distance a drone can move in 1 iteration
         */
        public static final double DRONE_MOVE_DISTANCE = 0.00015;

        /**
         * the distance which is considered "close"
         */
        public static final double DRONE_IS_CLOSE_DISTANCE = 0.00015;


        public static final String STUDENT_ID = "s2149970";


        public static final double DRONE_HOVERING_ANGLE= 999;

        public static final double DRONE_ANGLE_MULTIPLIER=22.5;

        public static final double EPSILON_ERROR= Math.pow(10,-12);

        public static final double MAX_LNG= 180;

        public static final double MIN_LNG= -180;

        public static final double MAX_LAT= 90;

        public static final double MIN_LAT= -90;

        public static final double MAX_ANGLE= 360;

        public static final double MIN_ANGLE= 0;

        public static final int DELIVERY_FEE = 100;
        public static final int MAX_PIZZA_COUNT = 4;
        public static final String RESTAURANTS_URL = "https://ilp-rest-2024.azurewebsites.net/restaurants";
}
