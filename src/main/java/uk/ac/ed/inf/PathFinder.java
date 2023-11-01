package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class PathFinder {

    private static final LngLat AT = new LngLat(-3.186874,55.944494);
    private static final double NULL_ANGLE = 5000;
    private static LngLatHandler lngLatHandler;

    private static NamedRegion[] noFlyZones;

    private static NamedRegion centralArea;

    private static LngLat currentGoal;

    public PathFinder(NamedRegion[] noFlyZones, NamedRegion centralArea){
        lngLatHandler = new LngLatHandler();
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;

    }

    public String findPath(String orderID, LngLat start, LngLat goal){
        currentGoal = goal;
        Node startNode = new Node(null, NULL_ANGLE, start);
        startNode.g = 0;
        startNode.f = startNode.g + lngLatHandler.distanceTo(start,goal);

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Map<LngLat, Double> bestCostInOpen = new HashMap<>();
        Map<LngLat, Double> bestCostInClosed = new HashMap<>();

        long timeForSuccessorGeneration = 0;
        long timeSpentCheckingOpenClosedList =0;

        openList.add(startNode);
        while (!openList.isEmpty()){

            Node q = openList.poll();
            long startGen = System.currentTimeMillis();
            List<Node> qSuccessors = generateLegalSuccessors(q);
            long endGen = System.currentTimeMillis();
            timeForSuccessorGeneration += (endGen-startGen);

            System.out.println("Current pos exploring ["+q.pos.lng()+","+q.pos.lat()+"]");

            for (Node successor : qSuccessors){
                //Return the path if the successor is the goal state
                if (lngLatHandler.isCloseTo(successor.pos,currentGoal)){
                    System.out.println("Path found!");
                    System.out.println("Time taken to generate successors in ms: "+timeForSuccessorGeneration);
                    System.out.println("Time taken to search open and closed list is in ms: "+timeSpentCheckingOpenClosedList);

//                    return reconstructPath(orderID, successor);
                    return reconstructPathForGeojson(successor);
//                    return successor.toString();
                }
                successor.g = q.g + SystemConstants.DRONE_MOVE_DISTANCE;
                successor.f = successor.g + lngLatHandler.distanceTo(successor.pos,goal);

//                long startTime = System.currentTimeMillis();
//                long endTime;
//                if (openListContainsBetterPos(successor,openList)
//                || closedListContainsBetterPos(successor,closedList)){
//                    endTime = System.currentTimeMillis();
//                    continue;
//                }
//                endTime = System.currentTimeMillis();
//                timeSpentCheckingOpenClosedList += (endTime-startTime);
                long startTime = System.currentTimeMillis();
                long endTime;
                if (bestCostInOpen.containsKey(successor.pos) && bestCostInOpen.get(successor.pos) < successor.f) {
                    endTime = System.currentTimeMillis();
                    timeSpentCheckingOpenClosedList += (endTime-startTime);
                    successor = null;
                    continue;
                }
                if (bestCostInClosed.containsKey(successor.pos) && bestCostInClosed.get(successor.pos) < successor.f){
                    endTime = System.currentTimeMillis();
                    timeSpentCheckingOpenClosedList += (endTime-startTime);
                    successor = null;
                    continue;
                }
                endTime = System.currentTimeMillis();
                timeSpentCheckingOpenClosedList += (endTime-startTime);

                openList.add(successor);
                if (bestCostInOpen.containsKey(successor.pos)){
                    bestCostInOpen.replace(successor.pos, successor.f);
                } else{
                    bestCostInOpen.put(successor.pos, successor.f);
                }
            }
            if (bestCostInClosed.containsKey(q.pos)){
                bestCostInClosed.replace(q.pos, q.f);
            } else{
                bestCostInClosed.put(q.pos, q.f);
            }
            q = null;
        }

        return null;
    }

//    private List<DroneMove> reconstructPath(String orderID, Node goalNode){
//        List<DroneMove> path = new ArrayList<>();
//        Node current = goalNode;
//        int totalTicks = (int) (goalNode.g() / SystemConstants.DRONE_MOVE_DISTANCE);
//        while (current.previous().previous() != null){
//            path.add(current.convertToMove(orderID, totalTicks));
//            totalTicks--;
//        }
//        return path;
//    }

    private static String reconstructPathForGeojson(Node goalNode){
        long startTime = System.currentTimeMillis();
        String path = "";
        Node current = goalNode;
        while (current.previous != null){
            path += ("["+current.pos.lng()+","+current.pos.lat()+"],");
            current = current.previous;
        }
        path += ("["+current.pos.lng()+","+current.pos.lat()+"],");
        long endTime = System.currentTimeMillis();
        System.out.println("Getting coordinates took in ms:"+(endTime-startTime));
        return path;
    }

    private static List<Node> generateLegalSuccessors(Node q){
        List<Node> successors = new ArrayList<>();
        for (double angle = 0; angle<360; angle += 22.5){
            LngLat nextPos = lngLatHandler.nextPosition(q.pos,angle);
            if (posInNoFlyZones(nextPos,noFlyZones)){
                continue;
            }
//            boolean isInCentral = lngLatHandler.isInCentralArea(nextPos,centralArea);
//            if (q.previous != null && goalIsAppleton() && q.previous.inCentral && !isInCentral){
//                continue;
//            }
            Node sucessorNode = new Node(q,angle,nextPos);
            successors.add(sucessorNode);
        }
        return successors;
    }

    private static boolean posInNoFlyZones(LngLat pos, NamedRegion[] noFlyZones){
        for (NamedRegion noFlyZone : noFlyZones){
            if (lngLatHandler.isInRegion(pos,noFlyZone)){
                return true;
            }
        }
        return false;
    }

    private static boolean goalIsAppleton(LngLat goal){
        return (goal.lng() == AT.lng()) && (goal.lat() == AT.lat());
    }

    //Deprecated?
//    private List<DroneMove> convertNodesToMoves(List<Node> path,String orderID){
//        int tick = 1;
//        List<DroneMove> moves = new ArrayList<>();
//        for (Node node : path){
//            if (node.previous == null){
//                continue;
//            }
//            moves.add(new DroneMove(orderID,
//                                    node.previous.pos.lng(),
//                                    node.previous.pos.lat(),
//                                    node.angleFromPrev,
//                                    node.pos.lng(),
//                                    node.pos.lat(),
//                                    tick));
//            tick++;
//        }
//        return moves;
//    }

    public double[] TestArrayForNodeComparable(){
        Node n1 = new Node(null, 1000,null);
        n1.f = 1;
        Node n2 = new Node(null, 1000,null);
        n2.f = 2;
        Node n3 = new Node(null, 1000,null);
        n3.f = 3;
        Node n4 = new Node(null, 1000,null);
        n4.f = 4;

        Node[] unSortedArray = new Node[]{n3,n4,n2,n1};
        Arrays.sort(unSortedArray);
        double[] fVals = new double[]{unSortedArray[0].f,
                        unSortedArray[1].f,
                        unSortedArray[2].f,
                        unSortedArray[3].f};
        return fVals;
    }
    }



