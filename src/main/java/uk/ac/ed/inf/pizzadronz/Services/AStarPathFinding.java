package uk.ac.ed.inf.pizzadronz.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.ServiceInterfaces.PathFinding;
import uk.ac.ed.inf.pizzadronz.Constants.Directions;
import uk.ac.ed.inf.pizzadronz.Constants.SystemConstants;
import uk.ac.ed.inf.pizzadronz.Data.AStarNode;
import uk.ac.ed.inf.pizzadronz.Data.LngLat;
import uk.ac.ed.inf.pizzadronz.Data.NamedRegion;

import java.util.*;

@Service
public class AStarPathFinding implements PathFinding {

NamedRegion centralRegion;


    @Override
    public List<LngLat> findPath(LngLat start, LngLat destination, NamedRegion[] obstacles, LngLatHandling lngLatHandler)  {
        RestTemplate restTemplate = new RestTemplate();
        centralRegion = restTemplate.getForObject(SystemConstants.CENTRAL_REGION_URL, NamedRegion.class);
        PriorityQueue<AStarNode> openList = new PriorityQueue<>(Comparator.comparing(AStarNode::getFCost));
        Set<AStarNode> closedList = new HashSet<>();
        AStarNode startNode = new AStarNode(start, 0, null, 0,
                lngLatHandler.distanceTo(start, destination), lngLatHandler.isInRegion(start, centralRegion));
        if(isPointInObstacle(start, obstacles, lngLatHandler)){
            throw new RuntimeException("Start point is in obstacle");
        }
        openList.add(startNode);
        while (!openList.isEmpty()) {
            AStarNode current = openList.poll();
            closedList.add(current);

            if (lngLatHandler.isCloseTo(current.getPosition(), destination)) {

                return (reconstructPath(current));
            }

            for (AStarNode neighbor : getNeighbors(current, lngLatHandler, obstacles, destination)) {
                if (checkListIfNeighborExists(closedList, neighbor, lngLatHandler)) {
                    continue;
                }
                if (checkOpenList(neighbor, openList, lngLatHandler)) {
                    continue;
                }

                openList.add(neighbor);

            }
        }
        throw new RuntimeException("No Path Found In Minimum Step Number");  // No path found
    }

    /**
     * Check if the neighbor is already in the closed list
     * @param closedList The closed list
     * @param neighbor The neighbor to check
     * @param lngLatHandler The LngLatHandling object
     * @return True if the neighbor is in the closed list, false otherwise
     */
    private boolean checkListIfNeighborExists(Set<AStarNode> closedList, AStarNode neighbor, LngLatHandling lngLatHandler) {
        return closedList.stream().anyMatch(pos ->
                lngLatHandler.distanceTo(pos.getPosition(), neighbor.getPosition()) < SystemConstants.NODE_RADIUS);
    }

    /**
     * Check if the neighbor is already in the open list
     * @param neighbor The neighbor to check
     * @param openList The open list
     * @param lngLatHandler The LngLatHandling object
     * @return True if the neighbor is in the open list, false otherwise
     */
    private boolean checkOpenList(AStarNode neighbor, PriorityQueue<AStarNode> openList, LngLatHandling lngLatHandler) {
        for (AStarNode node : openList) {
            if (lngLatHandler.distanceTo(node.getPosition(),neighbor.getPosition())<SystemConstants.NODE_RADIUS){
                if(node.getFCost()>neighbor.getFCost()){
                    openList.remove(node);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Get the valid neighbors of the current node
     * @param currentNode The current node
     * @param lngLatHandler The LngLatHandling object
     * @param obstacles The obstacles
     * @param destination The destination
     * @return The neighbors of the current node
     */
    private List<AStarNode> getNeighbors(AStarNode currentNode, LngLatHandling lngLatHandler, NamedRegion[] obstacles, LngLat destination) {
        List<AStarNode> neighbors = new ArrayList<>();
        for (Directions direction : Directions.values()) {
            LngLat neighborPosition = lngLatHandler.nextPosition(currentNode.getPosition(), direction.getAngle());
            if (!isPointInObstacle(neighborPosition, obstacles, lngLatHandler)) {
                boolean inCentral = lngLatHandler.isInRegion(neighborPosition, centralRegion);
                if(currentNode.isInCentral()){
                    if(!inCentral){
                        continue;
                    }
                }
                double gCost = currentNode.getGCost() + SystemConstants.DRONE_MOVE_DISTANCE;
                double hCost = lngLatHandler.distanceTo(neighborPosition, destination);
                neighbors.add(new AStarNode(neighborPosition,currentNode.getStepCount() + 1, currentNode,
                        gCost, hCost, inCentral, direction));
            }
        }
        return neighbors;
    }

    /**
     * Check if a point is in an obstacle
     * @param point The point to check
     * @param obstacles The obstacles
     * @param lngLatHandler The LngLatHandling object
     * @return True if the point is in an obstacle, false otherwise
     */
    private boolean isPointInObstacle(LngLat point, NamedRegion[] obstacles, LngLatHandling lngLatHandler) {
        for (NamedRegion obstacle : obstacles) {
            if (lngLatHandler.isInRegion(point, obstacle)) return true;
        }
        return false;
    }

    /**
     * Reconstruct the path from the current node to the start node
     * @param current The current node
     * @return The path from the start node to the current node
     */
    private List<LngLat> reconstructPath(AStarNode current) {
        List<LngLat> path = new ArrayList<>();

        while (current != null) {
            path.add(current.getPosition());
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

}
