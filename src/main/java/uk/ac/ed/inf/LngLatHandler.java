package uk.ac.ed.inf;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.toRadians;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class LngLatHandler implements LngLatHandling {

    //Constants used in defining orientation of 3 ordered points.
    // Used to determine if rays intersect
    static final int COLLINEAR = 0;
    static final int CLOCKWISE = 1;
    static final int ANTICLOCKWISE = 2;

    /**
     *
     * @param startPosition LngLat object
     * @param endPosition LngLat object
     * @return The Euclidean distance between startPosition and endPosition
     */
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double lngDiff = startPosition.lng()-endPosition.lng();
        double latDiff = startPosition.lat()-endPosition.lat();
        return sqrt(pow(lngDiff,2)+pow(latDiff,2));
    }


    /**
     *
     * @param startPosition LngLat object
     * @param otherPosition LngLat object
     * @return True if distance between startPosition and otherPosition
     * is within the DRONE_IS_CLOSE_DISTANCE
     */
    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition,otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * Used in PathFinder.java to prune successors that are very close to existing nodes
     * @param pos1 LngLat object
     * @param pos2 LngLat object
     * @return True if pos1 and pos2 are < SystemConstants.DRONE_IS_CLOSE_DISTANCE * 0.75
     */
    public boolean isVeryCloseTo(LngLat pos1, LngLat pos2) {
        return distanceTo(pos1,pos2) < (SystemConstants.DRONE_IS_CLOSE_DISTANCE * 0.75);
    }

    /**
     * Uses isInRegion on point for the central area
     * @param point LngLat of point in question
     * @param centralArea NamedRegion object
     * @return True if the NamedRegion is the central area and point is within it
     */
    @Override
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        return LngLatHandling.super.isInCentralArea(point, centralArea);
    }

    /**
     *
     * @param position LngLat object
     * @param region NamedRegion object
     * @return True if position is within region
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] vertices = region.vertices();
        int n = vertices.length;
        //Checks if region is a valid polygon
        if (n < 3){
            return false;
        }
        LngLat inf = new LngLat(99999,position.lat());
        //Draw an edge from point to infinity on latitude
        LineSegment testRay = new LineSegment(position,inf);
        int intersectionCount = 0;
        for (int i=0;i<vertices.length;i++){
            //Draw an edge between adjacent vertices
            LineSegment edge = new LineSegment(vertices[i],vertices[(i+1)%n]);
            if (lines_intersect(edge,testRay)){
                //Edge case: point is collinear with region vertices
                if(orientation(vertices[i],position,vertices[(i+1)%n])==COLLINEAR){
                    //Point is in region if point is on edge
                    return edge.contains(position);
                }
                intersectionCount++;
            }
        }
        //If test ray intersects edges an odd number of times then the point is in
        return intersectionCount % 2 == 1;
    }

    /**
     * Computes the next position of the drone given its start position and movement angle
     * @param startPosition LngLat of the drone's start position
     * @param angle Direction which the drone wants to move
     * @return LngLat of the new position of the drone
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        //Drone does not move when hovering
        if (angle == 999){
            return startPosition;
        }
        angle = toRadians(angle) % (2*PI); //Ensures angle is 0<=angle<2pi radians
        double lngDiff = SystemConstants.DRONE_MOVE_DISTANCE*cos(angle);
        double latDiff = SystemConstants.DRONE_MOVE_DISTANCE*sin(angle);

        //Round values consistently
        lngDiff = new BigDecimal(lngDiff).setScale(13,RoundingMode.HALF_DOWN).doubleValue();
        latDiff = new BigDecimal(latDiff).setScale(13,RoundingMode.HALF_DOWN).doubleValue();

        return new LngLat(startPosition.lng()+lngDiff,startPosition.lat()+latDiff);
    }

    /**
     * Determines the orientation of three ordered points pA, pB, pC
     * Source: <a href="https://www.geeksforgeeks.org/orientation-3-ordered-points/">...</a>
     * @param pA LngLat point
     * @param pB LngLat point
     * @param pC LngLat point
     * @return Enum representing orientation of the ordered points
     */
    private static int orientation(LngLat pA, LngLat pB, LngLat pC){
        double calc = (pB.lat()-pA.lat()) * (pC.lng() - pB.lng())
                - (pB.lng()-pA.lng()) * (pC.lat() - pB.lat());
        if (calc == 0){
            return COLLINEAR; //All 3 points are parallel
        } else if (calc > 0){
            return CLOCKWISE;
        } else {
            return ANTICLOCKWISE;
        }
    }

    /**
     * Determines whether two line segments intersect given their start and end LngLat positions
     * Source:  <a href="https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/">...</a>
     * @param line1 LineSegment object
     * @param line2 LineSegment object
     * @return True if the line segments intersect
     */
    private static boolean lines_intersect(LineSegment line1, LineSegment line2){
        int o1 = orientation(line1.start,line1.end,line2.start);
        int o2 = orientation(line1.start,line1.end, line2.end);
        int o3 = orientation(line2.start,line2.end,line1.start);
        int o4 = orientation(line2.start,line2.end,line1.end);

        /*
        Edge cases: Any point P of a line segment that is collinear with the start and end points of the other line
        segment and the other line segment contains P then the line segments intersect
         */
        if (o1 == COLLINEAR && line1.contains(line2.start)){return true;}
        if (o2 == COLLINEAR && line1.contains(line2.end)){return true;}
        if (o3 == COLLINEAR && line2.contains(line1.start)){return true;}
        if (o4 == COLLINEAR && line2.contains(line1.end)){return true;}

        /*
        If the orientations of either point of a ray wrt to another ray are not the same then rays
        intersect
         */
        return (o1 != o2 && o3 != o4);
    }

    /**
     * Determines whether a possible path between two points goes through a No-Fly Zone
     * @param p1 LngLat of the start of the possible path
     * @param p2 LngLat of the end of the possible path
     * @param noFlyZones Array of NamedRegion objects representing No-Fly Zones
     * @return True if the path between p1 and p2 go through a No-Fly Zone edge
     */
    public boolean pathGoesThroughNoFlyZones(LngLat p1, LngLat p2, NamedRegion[] noFlyZones){
        LineSegment pathSegment = new LineSegment(p1,p2);
        for(NamedRegion noFlyZone : noFlyZones){
            LngLat[] vertices = noFlyZone.vertices();
            int n = vertices.length;
            for(int i=0;i<n;i++){
                //Make edge of noFLyZone
                LineSegment noFlyZoneEdge = new LineSegment(vertices[i],vertices[(i+1)%n]);
                if(lines_intersect(pathSegment,noFlyZoneEdge)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Represents testRay for isInRegion or edges of zones
     */
    private class LineSegment {
        private final LngLat start;
        private final LngLat end;
        private LineSegment(LngLat start, LngLat end){
            this.start = start;
            this.end = end;
        }

        /**
         *
         * @param point LngLat of a point
         * @return True if the line segment contains the point
         */
        private boolean contains(LngLat point){
            return ((point.lng() <= max(start.lng(), end.lng()))
                    && (point.lng()) >= min(start.lng(), end.lng())
                    && (point.lat() <= max(start.lat(), end.lat()))
                    && (point.lat() >= min(start.lat(), end.lat())));
        }
    }

}