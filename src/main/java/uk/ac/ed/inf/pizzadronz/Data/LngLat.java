package uk.ac.ed.inf.pizzadronz.Data;

import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;


public record LngLat(
        Double lng,
        Double lat
) {
        public LngLat {
                if (lng == null || lat == null) {
                        throw new RuntimeException("Longitude or Latitude is null");
                }
                if (lng < SystemConstants.MIN_LNG || lng > SystemConstants.MAX_LNG) {
                        throw new RuntimeException("Longitude is out of bounds");
                }
                if (lat < SystemConstants.MIN_LAT || lat > SystemConstants.MAX_LAT) {
                        throw new RuntimeException("Latitude is out of bounds");
                }
        }

}