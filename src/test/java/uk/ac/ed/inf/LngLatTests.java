package uk.ac.ed.inf;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LngLatTests {
    LngLatHandling LngLatTings = new LngLatHandler();
    @Test
    void simpleRectangleIN(){
        LngLat[] vertices = {new LngLat(0,0),
                            new LngLat(5,0),
                            new LngLat(5,5),
                            new LngLat(0,5),
                            new LngLat(0,0)};
        NamedRegion simpleRect = new NamedRegion("simpleRect",vertices);
        LngLat point = new LngLat(2,2);
        boolean inRegion = LngLatTings.isInRegion(point,simpleRect);
        assertTrue(inRegion);
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
        boolean inRegion = LngLatTings.isInRegion(point,simpleRect);
        assertTrue(inRegion);
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
        boolean inRegion = LngLatTings.isInRegion(point,simpleRect);
        assertTrue(!inRegion);
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
        boolean inRegion = LngLatTings.isInRegion(point,simpleRect);
        assertTrue(!inRegion);
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
        boolean inRegion = LngLatTings.isInRegion(point,BayesCentre);
        assertTrue(inRegion);

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
        boolean inRegion = LngLatTings.isInRegion(point,BayesCentre);
        assertTrue(!inRegion);

    }
    @Test
    void inCentralRegion(){

    }
}
