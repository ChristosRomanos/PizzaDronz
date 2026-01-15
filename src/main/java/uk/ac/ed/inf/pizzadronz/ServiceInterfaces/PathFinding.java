package uk.ac.ed.inf.pizzadronz.ServiceInterfaces;


import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Data.NamedRegion;

import java.io.IOException;
import java.util.List;

public interface PathFinding {
    /**
     * Finds the shortest path from the start to the destination while avoiding obstacles.
     *
     * @param start The starting point as a LngLat object.
     * @param destination The destination point as a LngLat object.
     * @param obstacles A list of obstacles, each represented as a list of LngLat vertices.
     * @return A list of LngLat points representing the path from start to destination.
     */
    List<LngLat> findPath(LngLat start, LngLat destination,NamedRegion[] obstacles, LngLatHandling lngLatHandler);
}

