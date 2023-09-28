package uk.ac.ed.inf;
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



public class LngLatHandler implements LngLatHandling {

    static final double DISTANCE_TOLERANCE = 0.00015;
    static final double MOVEMENT_DISTANCE = 0.00015;
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
        return distanceTo(startPosition,otherPosition) < DISTANCE_TOLERANCE;
    }

    @Override
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        return LngLatHandling.super.isInCentralArea(point, centralArea);
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] vertices = region.vertices();
        int n = vertices.length;
        //Checks if region has enough vertices to be a valid shape
        if (n < 3){
            return false;
        }
        LngLat inf = new LngLat(99999,position.lat());
        Ray testRay = new Ray(position,inf);
        int count = 0;
        for (int i=0;i<vertices.length;i++){
            Ray edge = new Ray(vertices[i],vertices[(i+1)%n]);
            if (intersecting(edge,testRay)){
                //Edge case: point is collinear with region vertices
                if(orientation(edge.p1,position,edge.p2)==COLLINEAR){
                    //Point is in region if point is on edge
                    return edge.hasPoint(position);
                }
                count++;
            }
        }
        //If test ray intersects edges an odd amount of times then the point is in
        return count % 2 != 0;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        angle = toRadians(angle) % (2*PI); //Ensures angle is in range 0<=angle<2pi radians
        double lngDiff = MOVEMENT_DISTANCE*cos(angle);
        double latDiff = MOVEMENT_DISTANCE*sin(angle);
        return new LngLat(startPosition.lng()+lngDiff,startPosition.lat()+latDiff);
    }

    private static class Ray {
        public LngLat p1, p2;
        public Ray( LngLat p1, LngLat p2){
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
    //Defined by https://www.geeksforgeeks.org/orientation-3-ordered-points/
    static int orientation(LngLat pA, LngLat pB, LngLat pC){
        double calc = (pB.lat()-pA.lat()) * (pC.lng() - pB.lng())
                - (pB.lng()-pA.lng()) * (pC.lat() - pB.lat());
        if (calc == 0){
            return COLLINEAR;
        } else if (calc > 0){
            return CLOCKWISE;
        } else {
            return ANTICLOCKWISE;
        }

    }
    // Takes two rays and returns true if they are intersecting
    // Obtained from https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    static boolean intersecting(Ray r1, Ray r2){
        int o1 = orientation(r1.p1,r1.p2,r2.p1); //Bearing of r2.point1 wrt to r1
        int o2 = orientation(r1.p1,r1.p2,r2.p2); //Bearing of r2.point2 wrt to r2

        int o3 = orientation(r2.p1,r2.p2,r1.p1); //Bearing of r1.point1 wrt to r2
        int o4 = orientation(r2.p1,r2.p2,r1.p2); //Bearing of r1.point2 wrt to r2

        if (o1 != o2 && o3 != o4){
            return true;}
        if (o1 == COLLINEAR && r1.hasPoint(r2.p1)){
            return true;}
        if (o2 == COLLINEAR && r1.hasPoint(r2.p2)){
            return true;}
        if (o3 == COLLINEAR && r2.hasPoint(r1.p1)){
            return true;}
        if (o4 == COLLINEAR && r2.hasPoint(r1.p2)){
            return true;}
        return false;
    }

}