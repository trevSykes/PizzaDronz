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



public class LngLatHandler implements LngLatHandling {

    //Constants used in defining orientation of 3 ordered points.
    // Used to determine if rays intersect
    static final int COLLINEAR = 0;
    static final int CLOCKWISE = 1;
    static final int ANTICLOCKWISE = 2;
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double lngDiff = startPosition.lng()-endPosition.lng();
        double latDiff = startPosition.lat()-startPosition.lat();
        return sqrt(pow(lngDiff,2)+pow(latDiff,2));
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition,otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        return LngLatHandling.super.isInCentralArea(point, centralArea);
    }

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
        Ray testRay = new Ray(position,inf);
        int intersectionCount = 0;
        for (int i=0;i<vertices.length;i++){
            //Draw an edge between adjacent vertices
            Ray edge = new Ray(vertices[i],vertices[(i+1)%n]);
            if (intersecting(edge,testRay)){
                //Edge case: point is collinear with region vertices
                if(orientation(edge.p1,position,edge.p2)==COLLINEAR){
                    //Point is in region if point is on edge
                    return edge.hasPoint(position);
                }
                intersectionCount++;
            }
        }
        //If test ray intersects edges an odd number of times then the point is in
        return intersectionCount % 2 == 1;
    }

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
        lngDiff = new BigDecimal(lngDiff).setScale(13,BigDecimal.ROUND_DOWN).doubleValue();
        latDiff = new BigDecimal(latDiff).setScale(13,BigDecimal.ROUND_DOWN).doubleValue();
        return new LngLat(startPosition.lng()+lngDiff,startPosition.lat()+latDiff);
    }

    //Class used to represent polygon edges and test ray in conducting inRegion test
    private static class Ray {
        public LngLat p1, p2;
        public Ray(LngLat p1, LngLat p2){
            this.p1 = p1;
            this.p2 = p2;
        }
        public boolean hasPoint(LngLat p){
            return ((p.lng() <= max(p1.lng(), p2.lng()))
                    && (p.lng()) >= min(p1.lng(), p2.lng())
                    && (p.lat() <= max(p1.lat(), p2.lat()))
                    && (p.lat() >= min(p1.lat(), p2.lat())));
        }
    }

    //Determines the orientation of 3 ordered points.
    //Source: https://www.geeksforgeeks.org/orientation-3-ordered-points/
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
    // Takes two rays and returns true if they are intersecting
    // Source: https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    private static boolean intersecting(Ray r1, Ray r2){
        int o1 = orientation(r1.p1,r1.p2,r2.p1); //Orientation of r2.point1 wrt to r1
        int o2 = orientation(r1.p1,r1.p2,r2.p2); //Orientation of r2.point2 wrt to r2

        int o3 = orientation(r2.p1,r2.p2,r1.p1); //Orientation of r1.point1 wrt to r2
        int o4 = orientation(r2.p1,r2.p2,r1.p2); //Orientation of r1.point2 wrt to r2

        /*
        If the orientations of either point of a ray wrt to another ray are not the same then rays
        intersect
         */
        if (o1 != o2 && o3 != o4){return true;}
        /*
        Edge cases:
        Any point on one ray that is collinear with the points of another ray and that point lies
        on the other ray then the rays intersect.
         */
        if (o1 == COLLINEAR && r1.hasPoint(r2.p1)){return true;}
        if (o2 == COLLINEAR && r1.hasPoint(r2.p2)){return true;}
        if (o3 == COLLINEAR && r2.hasPoint(r1.p1)){return true;}
        if (o4 == COLLINEAR && r2.hasPoint(r1.p2)){return true;}

        return false;
    }

}