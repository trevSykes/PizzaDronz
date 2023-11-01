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
//    @Override
//    public boolean isInRegion(LngLat position, NamedRegion region) {
//        if (region == null) {
//            throw new IllegalArgumentException("The named region is null");
//        }
//        LngLat[] vertices = region.vertices();
//        int n = vertices.length;
//        //Checks if region is a valid polygon
//        if (n < 3){
//            return false;
//        }
//        LngLat inf = new LngLat(99999,position.lat());
//        //Draw an edge from point to infinity on latitude
////        Ray testRay = new Ray(position,inf);
//        int intersectionCount = 0;
//        for (int i=0;i<vertices.length;i++){
////            //Draw an edge between adjacent vertices
////            Ray edge = new Ray(vertices[i],vertices[(i+1)%n]);
//            if (intersecting(vertices[i],vertices[(i+1)%n],position,inf)){
//                //Edge case: point is collinear with region vertices
//                if(orientation(vertices[i],position,vertices[(i+1)%n])==COLLINEAR){
//                    //Point is in region if point is on edge
//                    return rayContainsPoint(vertices[i],vertices[(i+1)%n],position);
//                }
//                intersectionCount++;
//            }
//        }
//        //If test ray intersects edges an odd number of times then the point is in
//        return intersectionCount % 2 == 1;
//    }
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] polygon = region.vertices();
        boolean odd = false;

        for (int i = 0, j = polygon.length - 1; i < polygon.length; i++) {
            // Check if point is a vertex
            if (position.lat() == polygon[i].lat() && position.lng() == polygon[i].lng()) {
                return true;
            }

            // Check if point is on an edge
            if (position.lng() > Math.min(polygon[i].lng(), polygon[j].lng()) && position.lng() <= Math.max(polygon[i].lng(), polygon[j].lng())) {
                double slope = (polygon[j].lat() - polygon[i].lat()) / (polygon[j].lng() - polygon[i].lng());
                double expectedLat = slope * (position.lng() - polygon[i].lng()) + polygon[i].lat();
                if (position.lat() == expectedLat) {
                    return true;
                }
            }
            // Get ray-casting intercepts
            if ((polygon[i].lat() > position.lat()) != (polygon[j].lat() > position.lat()) &&
                    (position.lng() < (polygon[j].lng() - polygon[i].lng()) * (position.lat() - polygon[i].lat()) / (polygon[j].lat() - polygon[i].lat()) + polygon[i].lng())) {
                odd = !odd;
            }
            j = i;
        }
        return odd;
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
//        lngDiff = new BigDecimal(lngDiff).setScale(13,RoundingMode.HALF_DOWN).doubleValue();
//        latDiff = new BigDecimal(latDiff).setScale(13,RoundingMode.HALF_DOWN).doubleValue();

        return new LngLat(startPosition.lng()+lngDiff,startPosition.lat()+latDiff);
    }

    /**
     * Class used to represent a test ray and polygon edges used in inRegion
     */
//    private static class Ray {
//        public LngLat p1, p2;
//        public Ray(LngLat p1, LngLat p2){
//            this.p1 = p1;
//            this.p2 = p2;
//        }
//        public boolean hasPoint(LngLat p){
//            return ((p.lng() <= max(p1.lng(), p2.lng()))
//                    && (p.lng()) >= min(p1.lng(), p2.lng())
//                    && (p.lat() <= max(p1.lat(), p2.lat()))
//                    && (p.lat() >= min(p1.lat(), p2.lat())));
//        }
//    }

    /**
     * Determines the orientation of three ordered points pA,pB,pC
     * Source: <a href="https://www.geeksforgeeks.org/orientation-3-ordered-points/">...</a>
     * @param pA LngLat point
     * @param pB LngLat point
     * @param pC LngLat point
     * @return Enum representing oreintation of the ordered points
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

    private static boolean rayContainsPoint(LngLat ray_1, LngLat ray_2, LngLat p){
        return ((p.lng() <= max(ray_1.lng(), ray_2.lng()))
                && (p.lng()) >= min(ray_1.lng(), ray_2.lng())
                && (p.lat() <= max(ray_1.lat(), ray_2.lat()))
                && (p.lat() >= min(ray_1.lat(), ray_2.lat())));
    }

    /**
     * Source:  <a href="https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/">...</a>
     * @param rayA_1
     * @param rayA_2
     * @param rayB_1
     * @param rayB_2
     * @return
     */
    private static boolean intersecting(LngLat rayA_1, LngLat rayA_2, LngLat rayB_1, LngLat rayB_2){
        int o1 = orientation(rayA_1,rayA_2,rayB_1); //Orientation of r2.point1 wrt to r1
        int o2 = orientation(rayA_1,rayA_2,rayB_2); //Orientation of r2.point2 wrt to r2

        int o3 = orientation(rayB_1,rayB_2,rayA_1); //Orientation of r1.point1 wrt to r2
        int o4 = orientation(rayB_1,rayB_2,rayA_2); //Orientation of r1.point2 wrt to r2

        /*
        Edge cases: Any point on one ray that is collinear with the points of another ray and that
        point lies on the other ray then the rays intersect.
         */
        if (o1 == COLLINEAR && rayContainsPoint(rayA_1,rayA_2,rayB_1)){return true;}
        if (o2 == COLLINEAR && rayContainsPoint(rayA_1,rayA_2,rayB_2)){return true;}
        if (o3 == COLLINEAR && rayContainsPoint(rayB_1,rayB_2,rayA_1)){return true;}
        if (o4 == COLLINEAR && rayContainsPoint(rayB_1,rayB_2,rayA_2)){return true;}

        /*
        If the orientations of either point of a ray wrt to another ray are not the same then rays
        intersect
         */
        return (o1 != o2 && o3 != o4);
    }

}