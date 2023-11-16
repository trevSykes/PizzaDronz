package uk.ac.ed.inf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import uk.ac.ed.inf.ilp.constant.SystemConstants;

import static java.lang.Math.sin;
import static java.lang.Math.cos;
import static java.lang.Math.toRadians;
public class LngLatTests {
    LngLatHandling lngLatHandler = new LngLatHandler();
    @Test
    void simpleRectangleIN(){
        LngLat[] vertices = {new LngLat(0,0),
                            new LngLat(5,0),
                            new LngLat(5,5),
                            new LngLat(0,5),
                            new LngLat(0,0)};
        NamedRegion simpleRect = new NamedRegion("simpleRect",vertices);
        LngLat point = new LngLat(2,2);
        boolean inRegion = lngLatHandler.isInRegion(point,simpleRect);
        Assertions.assertTrue(inRegion);
    }

    @Test
    void simpleRectangleINBoundary(){
        LngLat[] vertices = {new LngLat(0,0),
                new LngLat(5,0),
                new LngLat(5,5),
                new LngLat(0,5),
                new LngLat(0,0)};
        NamedRegion simpleRect = new NamedRegion("simpleRect",vertices);
        LngLat point = new LngLat(5,3);
        boolean inRegion = lngLatHandler.isInRegion(point,simpleRect);
        Assertions.assertTrue(inRegion);
    }
    @Test
    void simpleRectangleOutPos(){
        LngLat[] vertices = {new LngLat(0,0),
                new LngLat(5,0),
                new LngLat(5,5),
                new LngLat(0,5),
                new LngLat(0,0)};
        NamedRegion simpleRect = new NamedRegion("simpleRect",vertices);
        LngLat point = new LngLat(7,7);
        boolean inRegion = lngLatHandler.isInRegion(point,simpleRect);
        Assertions.assertFalse(inRegion);
    }

    @Test
    void simpleTriangleOutsidePointUniquePoints(){
        LngLat[] vertices = {new LngLat(6,0),
                            new LngLat(4,2),
                            new LngLat(8,2)};
        NamedRegion simpleTri = new NamedRegion("simpleTri",vertices);
        LngLat point = new LngLat(2,0);
        boolean inRegion = lngLatHandler.isInRegion(point,simpleTri);
        Assertions.assertFalse(inRegion);
    }

    @Test
    void simpleTriangleOutsidePointRepeatedPoints(){
        LngLat[] vertices = {new LngLat(6,0),
                new LngLat(4,2),
                new LngLat(8,2),
                new LngLat(6,0)};
        NamedRegion simpleTri = new NamedRegion("simpleTri",vertices);
        LngLat point = new LngLat(2,0);
        boolean inRegion = lngLatHandler.isInRegion(point,simpleTri);
        Assertions.assertFalse(inRegion);
    }
    @Test
    void simpleRectangleOutNeg(){
        LngLat[] vertices = {new LngLat(0,0),
                new LngLat(5,0),
                new LngLat(5,5),
                new LngLat(0,5),
                new LngLat(0,0)};

        NamedRegion simpleRect = new NamedRegion("simpleRect",vertices);
        LngLat point = new LngLat(-1,2);
        boolean inRegion = lngLatHandler.isInRegion(point,simpleRect);
        Assertions.assertFalse(inRegion);
    }
    @Test
    void inBayes(){
        LngLat[] vertices = {
                new LngLat(-3.1876927614212, 55.9452069673277),
                new LngLat(-3.18755596876144, 55.9449621408666),
                new LngLat(-3.18698197603226, 55.9450567672283),
                new LngLat(-3.18723276257515, 55.9453699337766),
                new LngLat(-3.18744599819183, 55.9453361389472),
                new LngLat(-3.18737357854843, 55.9451934493426),
                new LngLat(-3.18759351968765, 55.9451566503593),
                new LngLat(-3.18762436509132, 55.9452197343093),
                new LngLat(-3.1876927614212, 55.9452069673277)
        };

        NamedRegion BayesCentre = new NamedRegion("Baye's Centre", vertices);
        LngLat point = new LngLat(-3.1874,55.9451);
        boolean inRegion = lngLatHandler.isInRegion(point,BayesCentre);
        Assertions.assertTrue(inRegion);

    }
    @Test
    void outBayes(){
        LngLat[] vertices = {
                new LngLat(-3.1876927614212, 55.9452069673277),
                new LngLat(-3.18755596876144, 55.9449621408666),
                new LngLat(-3.18698197603226, 55.9450567672283),
                new LngLat(-3.18723276257515, 55.9453699337766),
                new LngLat(-3.18744599819183, 55.9453361389472),
                new LngLat(-3.18737357854843, 55.9451934493426),
                new LngLat(-3.18759351968765, 55.9451566503593),
                new LngLat(-3.18762436509132, 55.9452197343093),
                new LngLat(-3.1876927614212, 55.9452069673277)
        };

        NamedRegion BayesCentre = new NamedRegion("Baye's Centre", vertices);
        LngLat point = new LngLat(-3.188,55.9451);
        boolean inRegion = lngLatHandler.isInRegion(point,BayesCentre);
        Assertions.assertFalse(inRegion);

    }
    @Test
    void outCentralRegion(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.195,55.945);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertFalse(inRegion);
    }
    @Test
    void inCentralRegion(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.1875,55.945);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertTrue(inRegion);
    }

    @Test
    void onEastCentralBorder(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.192473,55.944);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertTrue(inRegion);
    }

    @Test
    void onSouthCentralBorder(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.188,55.942617);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertTrue(inRegion);
    }

    @Test
    void onWestCentralBorder(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.184319,55.944);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertTrue(inRegion);
    }

    @Test
    void onNorthCentralBorder(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.189,55.946233);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertTrue(inRegion);
    }

    @Test
    void onCentralVertex(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.184319, 55.946233);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertTrue(inRegion);
    }

    @Test
    void onCentralRegionBorder(){
        LngLat[] vertices = {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        };

        NamedRegion central = new NamedRegion("central", vertices);
        LngLat point = new LngLat(-3.192473,55.944);
        boolean inRegion = lngLatHandler.isInCentralArea(point,central);
        Assertions.assertTrue(inRegion);
    }

    //Helper function to simulate the coordinate tolerance allowed for next position calculations
    private static boolean pointEquals(LngLat p1, LngLat p2){
        double tolerance = 1E-12;
        return (p1.lat() >= p2.lat()-tolerance
                && p1.lat() <= p2.lat()+tolerance
                && p1.lng() >= p2.lng()-tolerance
                && p1.lng() <= p2.lng()+tolerance);
    }
    @Test
    void move0degrees(){
        LngLat startPosition = new LngLat(0,0);
        double angle = 0;
        LngLat expectedFinalPosition = new LngLat(SystemConstants.DRONE_MOVE_DISTANCE,0);
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(expectedFinalPosition,finalPosition));
    }

    @Test
    void move90degrees(){
        LngLat startPosition = new LngLat(-3.1874,55.9451);
        double angle = 90;
        LngLat expectedFinalPosition = new LngLat(-3.1874,55.9451+SystemConstants.DRONE_MOVE_DISTANCE);
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(expectedFinalPosition,finalPosition));
    }
    @Test
    void move180degrees(){
        LngLat startPosition = new LngLat(0,0);
        double angle = 180;
        LngLat expectedFinalPosition = new LngLat(-SystemConstants.DRONE_MOVE_DISTANCE,0);
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(expectedFinalPosition,finalPosition));
    }
    @Test
    void move270degrees(){
        LngLat startPosition = new LngLat(0,0);
        double angle = 270;
        LngLat expectedFinalPosition = new LngLat(0,-SystemConstants.DRONE_MOVE_DISTANCE);
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(expectedFinalPosition,finalPosition));
    }

    @Test
    void hover(){
        LngLat startPosition = new LngLat(0,0);
        double angle = 999;
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(startPosition,finalPosition));
    }

    @Test
    void move22_5degrees(){
        LngLat startPosition = new LngLat(0,0);
        double angle = 22.5;
        LngLat expectedFinalPosition = new LngLat(0.00015*cos(toRadians(22.5)),0.00015*sin(toRadians(22.5)));
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(expectedFinalPosition,finalPosition));
    }

    @Test
    void move45degrees(){
        LngLat startPosition = new LngLat(0,0);
        double angle = 45;
        LngLat expectedFinalPosition = new LngLat(0.00015*cos(toRadians(45)),0.00015*sin(toRadians(45)));
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(expectedFinalPosition,finalPosition));
    }

    @Test
    void move67_5degrees(){
        LngLat startPosition = new LngLat(0,0);
        double angle = 67.5;
        LngLat expectedFinalPosition = new LngLat(0.00015*cos(toRadians(67.5)),0.00015*sin(toRadians(67.5)));
        LngLat finalPosition = lngLatHandler.nextPosition(startPosition,angle);
        Assertions.assertTrue(pointEquals(expectedFinalPosition,finalPosition));
    }

    @Test
    void moveInCicrleIn16Steps(){
        LngLat currentPosition = new LngLat(0,0);
        LngLat expectedFinalPosition = new LngLat(0,0);
        double angle = 0;
        for (int i=0;i<16;i++){
            currentPosition = lngLatHandler.nextPosition(currentPosition,angle);
            angle = angle + 22.5;
        }
        Assertions.assertTrue(pointEquals(currentPosition,expectedFinalPosition));
    }
}
