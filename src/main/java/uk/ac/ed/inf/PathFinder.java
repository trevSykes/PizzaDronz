package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class PathFinder {
    private final LngLat AT = new LngLat(-3.186874,55.944494);
    private final LngLatHandler lngLatHandler;
    private String currentOrderID;
    private Node currentPathEnd;

    public PathFinder(){
        this.lngLatHandler = new LngLatHandler();
    }

    /**
     * Uses A* search to find an optimal path from the start LngLat to the end LngLat while avoiding No-Fly Zones and
     * not leaving the Central Area once it enters when the goal is Appleton Tower
     * @param orderID String of the orderID that is being currently delivered
     * @param start LngLat of the start position
     * @param goal LngLat of the end position
     * @param noFlyZones Array of NamedRegion representing No-Fly Zones
     * @param centralArea NamedRegion of the Central Area
     * @return List of DroneMoves representing the flightpath
     * @throws RuntimeException Pathfinding failed when open list no longer contains any nodes
     */
    public List<DroneMove> findPath(String orderID, LngLat start, LngLat goal,NamedRegion[] noFlyZones,
                                    NamedRegion centralArea) throws RuntimeException{
        //Initialise start node
        //Used for a start node's prevAngleField
        double NULL_ANGLE = -1;
        Node startNode = new Node(null, NULL_ANGLE, start, lngLatHandler.isInCentralArea(start, centralArea));
        startNode.g = 0;
        startNode.f = lngLatHandler.distanceTo(start,goal);

        //Initialise openList data structures
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Map<LngLat, Double> bestInOpen = new HashMap<>();
        //Initialise closed list data structure
        Map<LngLat, Double> bestInClosed = new HashMap<>();


        openList.add(startNode);

        while (!openList.isEmpty()){
            //Take node with the lowest estimated total path cost
            Node q = openList.poll();
            for (Node successor : generateSuccessors(q,goal,centralArea,noFlyZones,
                    bestInOpen.keySet(),bestInClosed.keySet())){
                if (successor == null) break;
                //If successor is close to the goal then a path is found
                if(lngLatHandler.isCloseTo(successor.pos,goal)){
                    currentOrderID=orderID;
                    currentPathEnd=successor;
                    return getCurrentPathMoves();
                }
                //Update successor node f and g score
                successor.g = q.g + SystemConstants.DRONE_MOVE_DISTANCE;
                successor.f = successor.g + lngLatHandler.distanceTo(successor.pos,goal);
                //Skip successor if a node in the closed list has the same position and has a better f score
                if(bestInClosed.containsKey(successor.pos) && bestInClosed.get(successor.pos) < successor.f){
                    continue;
                }
                //Skip successor if a node in the open list has the same position and has a better f score
                if(bestInOpen.containsKey(successor.pos) && bestInOpen.get(successor.pos) < successor.f){
                    continue;
                }
                //Add node to open list to be explored
                openList.add(successor);
                //Add to or update open list map
                if (bestInOpen.containsKey(successor.pos)){
                    bestInOpen.replace(successor.pos, successor.f);
                } else{
                    bestInOpen.put(successor.pos, successor.f);
                }
            }
            //Add to or update closed list
            if (bestInClosed.containsKey(q.pos)){
                bestInClosed.replace(q.pos, q.f);
            } else{
                bestInClosed.put(q.pos, q.f);
            }
        }
        throw new RuntimeException(String.format("Pathfinder failed for Order: %s",orderID));
    }


    /**
     * Clears the currentPathEnd and currentOrderID fields
     */
    public void clearCurrentPath(){
        currentPathEnd = null;
        currentOrderID = null;
    }

    /**
     * Reconstructs path using the current path's end node
     * @return List of DroneMove objects
     */
    public List<DroneMove> getCurrentPathMoves(){
        List<DroneMove> moves = new ArrayList<>();
        Node current = currentPathEnd;
        while(current.previous.previous != null){
            moves.add(current.convertToMove(currentOrderID));
            current = current.previous;
        }
        Collections.reverse(moves);
        return moves;
    }

    /**
     *
     * @return A DroneMove that represents the drone hovering over its currentPath's end location
     */
    public DroneMove makeHoverMove(){
        double HOVER_ANGLE = 999;
        LngLat hoverPos = lngLatHandler.nextPosition(currentPathEnd.pos, HOVER_ANGLE);
        return new DroneMove(currentOrderID, currentPathEnd.pos.lng(), currentPathEnd.pos.lat(), HOVER_ANGLE,
                hoverPos.lng(), hoverPos.lat());
    }


    /**
     * Generates a list of nodes that are the legal successors to q
     * @param q Node that has successors generated from
     * @param goal LngLat of goal, used for Central Area flightpath requirement
     * @param centralArea NamedRegion of the Central Ara
     * @param noFlyZones Array of NamedRegions representing the No-Fly Zones
     * @param openListPoints Set of LngLat positions of nodes in the open set
     * @param closedListPoints Set of LngLat positions of nodes in the closed set
     * @return List of Nodes that are the legal successors to q
     */
    private List<Node> generateSuccessors(Node q, LngLat goal,NamedRegion centralArea,NamedRegion[] noFlyZones,
                                      Set<LngLat> openListPoints, Set<LngLat> closedListPoints) {
        List<Node> successors = new ArrayList<>();
        //Check for all 16 possible compass directions
        for(double angle = 0; angle<360 ; angle += 22.5){
            LngLat nextPos = lngLatHandler.nextPosition(q.pos,angle);
            //Prune positions that are very close to existing nodes
            if(positionIsVeryCloseToExistingNode(nextPos,openListPoints,closedListPoints)){
                continue;
            }
            //Skip if in a no fly zone
            if(posIsInNoFlyZone(nextPos,noFlyZones)){
                continue;
            }
            //Skip if path between q and nextPos goes through a No-Fly Zone
            if(lngLatHandler.pathGoesThroughNoFlyZones(q.pos,nextPos,noFlyZones)){
                continue;
            }
            //Skip if possible path fails central area requirement
            boolean nextIsInCentral = lngLatHandler.isInCentralArea(nextPos,centralArea);
            if(goalIsAT(goal) && q.inCentral && !nextIsInCentral){
                continue;
            }
            successors.add(new Node(q, angle, nextPos, nextIsInCentral));
        }
        return successors;
    }

    /**
     * Used to prune the possible of successors of a node.
     * @param nextPos LngLat of a possible successor being generated
     * @param openListNodes Set of LngLat positions of nodes in the open list
     * @param closedListNodes Set of LngLat positions of nodes in the closed list
     * @return True if nextPos is <(0.75*DRONE_IS_CLOSE_DISTANCE) from existing nodes
     */
    private boolean positionIsVeryCloseToExistingNode(LngLat nextPos, Set<LngLat> openListNodes,
                                                      Set<LngLat> closedListNodes){
        for(LngLat closedListPos : closedListNodes){
            if(lngLatHandler.isVeryCloseTo(nextPos,closedListPos)){
                return true;
            }
        }
        for(LngLat openListPos : openListNodes){
            if(lngLatHandler.isVeryCloseTo(nextPos,openListPos)){
                return true;
            }
        }
        return false;
    }

    /**
     * Used to ensure the Central Area requirement of the drone's flightpath is met.
     * @param goal LngLat of the end location of the path
     * @return True if the goal is Appleton Tower
     */
    private boolean goalIsAT(LngLat goal){
        return ((goal.lng() == AT.lng()) && (goal.lat() == AT.lat()));
    }

    /**
     *
     * @param pos LngLat position of a potential successor node
     * @param noFlyZones Array of NamedRegion objects representing No-FLy Zones
     * @return True if the position is in a No-FLy Zone
     */
    private boolean posIsInNoFlyZone(LngLat pos,NamedRegion[] noFlyZones){
        for(NamedRegion noFlyZone : noFlyZones){
            if(lngLatHandler.isInRegion(pos,noFlyZone)){
                return true;
            }
        }
        return false;
    }

    //Node class used in A* search
    private static class Node implements Comparable<Node>{
        private final Node previous;
        private final double angleFromPrev;
        private final LngLat pos;
        private double f;
        private double g;
        private final boolean inCentral;

        private Node(Node previous, double angleFromPrev, LngLat pos, boolean inCentral){
            this.previous = previous;
            this.angleFromPrev = angleFromPrev;
            this.pos = pos;
            this.inCentral = inCentral;
        }

        /**
         *
         * @param orderID String of the current orderID being delivered
         * @return DroneMove object that represents moving from the node's previous to the node
         */
        private DroneMove convertToMove(String orderID){
            return new DroneMove(orderID,
                    this.previous.pos.lng(),
                    this.previous.pos.lat(),
                    this.angleFromPrev,
                    this.pos.lng(),
                    this.pos.lat());
        }

        /**
         * Required implementation of the Comparable interface.
         * Enables PriorityQueue to sort based on descending f scores.
         * @param n the Node to be compared.
         * @return Returns 0 if the f scores of this node and Node n are equal.
         *         -1 if this Node's f score is smaller than Node n's
         *         1 if this Node's f score is greater than Node n's
         */
        @Override
        public int compareTo(Node n) {
            return Double.compare(this.f, n.f);
        }

    }
    }



