package uk.ac.ed.inf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathFinderTests {
    private static NamedRegion central = new NamedRegion("central",new LngLat[]{new LngLat(-3.192473,55.946233),
            new LngLat(-3.192473,55.942617),
            new LngLat(-3.184319,55.942617),
            new LngLat(-3.184319,55.946233)});;
    private static NamedRegion[] noFlyZones = new NamedRegion[]{
            new NamedRegion("George Square Area", new LngLat[]{
                    new LngLat(-3.190578818321228, 55.94402412577528),
                    new LngLat(-3.1899887323379517, 55.94284650540911),
                    new LngLat(-3.187097311019897, 55.94328811724263),
                    new LngLat(-3.187682032585144, 55.944477740393744),
                    new LngLat(-3.190578818321228, 55.94402412577528)
            }),
            new NamedRegion("Dr Elsie Inglis Quadrangle", new LngLat[]{
                    new LngLat(-3.1907182931900024, 55.94519570234043),
                    new LngLat(-3.1906163692474365, 55.94498241796357),
                    new LngLat(-3.1900262832641597, 55.94507554227258),
                    new LngLat(-3.190133571624756, 55.94529783810495),
                    new LngLat(-3.1907182931900024, 55.94519570234043)
            }),
            new NamedRegion("Bristo Square Open Area", new LngLat[]{
                    new LngLat(-3.189543485641479, 55.94552313663306),
                    new LngLat(-3.189382553100586, 55.94553214854692),
                    new LngLat(-3.189259171485901, 55.94544803726933),
                    new LngLat(-3.1892001628875732, 55.94533688994374),
                    new LngLat(-3.189194798469543, 55.94519570234043),
                    new LngLat(-3.189135789871216, 55.94511759833873),
                    new LngLat(-3.188138008117676, 55.9452738061846),
                    new LngLat(-3.1885510683059692, 55.946105902745614),
                    new LngLat(-3.1895381212234497, 55.94555918427592),
                    new LngLat(-3.189543485641479, 55.94552313663306)
            }),
            new NamedRegion("Bayes Central Area", new LngLat[]{
                    new LngLat(-3.1876927614212036, 55.94520696732767),
                    new LngLat(-3.187555968761444, 55.9449621408666),
                    new LngLat(-3.186981976032257, 55.94505676722831),
                    new LngLat(-3.1872327625751495, 55.94536993377657),
                    new LngLat(-3.1874459981918335, 55.9453361389472),
                    new LngLat(-3.1873735785484314, 55.94519344934259),
                    new LngLat(-3.1875935196876526, 55.94515665035927),
                    new LngLat(-3.187624365091324, 55.94521973430925),
                    new LngLat(-3.1876927614212036, 55.94520696732767)
            })};;
//    @BeforeEach
//    void setUp(){
//
//
//
//        //{"name":"central","vertices":[{"lng":-3.192473,"lat":55.946233},{"lng":-3.192473,"lat":55.942617},{"lng":-3.184319,"lat":55.942617},{"lng":-3.184319,"lat":55.946233}]}
//        //[{"name":"George Square Area","vertices":[{"lng":-3.190578818321228,"lat":55.94402412577528},{"lng":-3.1899887323379517,"lat":55.94284650540911},{"lng":-3.187097311019897,"lat":55.94328811724263},{"lng":-3.187682032585144,"lat":55.944477740393744},{"lng":-3.190578818321228,"lat":55.94402412577528}]},{"name":"Dr Elsie Inglis Quadrangle","vertices":[{"lng":-3.1907182931900024,"lat":55.94519570234043},{"lng":-3.1906163692474365,"lat":55.94498241796357},{"lng":-3.1900262832641597,"lat":55.94507554227258},{"lng":-3.190133571624756,"lat":55.94529783810495},{"lng":-3.1907182931900024,"lat":55.94519570234043}]},{"name":"Bristo Square Open Area","vertices":[{"lng":-3.189543485641479,"lat":55.94552313663306},{"lng":-3.189382553100586,"lat":55.94553214854692},{"lng":-3.189259171485901,"lat":55.94544803726933},{"lng":-3.1892001628875732,"lat":55.94533688994374},{"lng":-3.189194798469543,"lat":55.94519570234043},{"lng":-3.189135789871216,"lat":55.94511759833873},{"lng":-3.188138008117676,"lat":55.9452738061846},{"lng":-3.1885510683059692,"lat":55.946105902745614},{"lng":-3.1895381212234497,"lat":55.94555918427592},{"lng":-3.189543485641479,"lat":55.94552313663306}]},{"name":"Bayes Central Area","vertices":[{"lng":-3.1876927614212036,"lat":55.94520696732767},{"lng":-3.187555968761444,"lat":55.9449621408666},{"lng":-3.186981976032257,"lat":55.94505676722831},{"lng":-3.1872327625751495,"lat":55.94536993377657},{"lng":-3.1874459981918335,"lat":55.9453361389472},{"lng":-3.1873735785484314,"lat":55.94519344934259},{"lng":-3.1875935196876526,"lat":55.94515665035927},{"lng":-3.187624365091324,"lat":55.94521973430925},{"lng":-3.1876927614212036,"lat":55.94520696732767}]}]
//    }
    @Test
    void testNodeComparable(){
        LngLatHandler lngLatHandler = new LngLatHandler();
        PathFinder pathFinder = new PathFinder(null,null);
        double[] fVals = pathFinder.TestArrayForNodeComparable();
        double[] expected = new double[]{1.0,2.0,3.0,4.0};
        assertEquals(fVals.hashCode(),expected.hashCode());
    }
    @Test
    void close_point_to_appleton(){
        LngLat closePoint = new LngLat(-3.186574, 55.943744);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",closePoint,AT);
        System.out.println(goal);
    }

    @Test
    void dominoes_to_appleton(){
        LngLat dominoes = new LngLat(-3.1838572025299072, 55.94449876875712);
//        LngLat middle_ground = new LngLat(-3.1848, 55.9445);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",dominoes,AT);
        System.out.println(goal);

    }

    @Test
    void soderberg_to_appleton(){
        LngLat soderberg = new LngLat(	55.9439,-3.1940);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",soderberg,AT);
        System.out.println(goal);
    }

    @Test
    void behind_GS_corner(){
        LngLat start = new LngLat(-3.188,55.943);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",start,AT);
        System.out.println(goal);
    }

    @Test
    void behind_bayes_corner(){
        LngLat start = new LngLat(-3.1877,55.946);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",start,AT);
        System.out.println(goal);
    }

    @Test
    void behind_McEwan_corner(){
        LngLat start = new LngLat(-3.1894,55.9459);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",start,AT);
        System.out.println(goal);
    }

    @Test
    void civs_to_appleton(){
        LngLat start = new LngLat(-3.1913,55.9455);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = "NO PATH";
        try {
            goal = pathFinder.findPath("TEST", start, AT);
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }
        System.out.println(goal);
    }

    @Test
    void MMW_to_appleton(){
        LngLat start = new LngLat(-3.191,55.9442);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",start,AT);
        System.out.println(goal);
    }
    @Test
    void meadows_to_appleton(){
        LngLat start = new LngLat(-3.1869,55.942);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",start,AT);
        System.out.println(goal);
    }


    @Test
    void lowerMMW_to_appleton(){
        LngLat start = new LngLat(-3.191,55.943);
        LngLat AT = new LngLat(-3.186874,55.944494);
        PathFinder pathFinder = new PathFinder(noFlyZones,central);
        String goal = pathFinder.findPath("TEST",start,AT);
        System.out.println(goal);
    }

}
