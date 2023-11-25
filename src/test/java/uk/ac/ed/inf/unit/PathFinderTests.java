package uk.ac.ed.inf.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.DroneMove;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.PathFinder;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import java.time.DayOfWeek;
import java.util.List;


public class PathFinderTests {
    private static NamedRegion central;
    private static NamedRegion[] noFlyZones;
    private static PathFinder pathFinder;
    private static LngLatHandler lngLatHandler;
    private static Restaurant civerinos, soraLella, dominos, sodeberg, laTrattoria, halalPizza, worldOfPizza;
    private static final LngLat AT = new LngLat(-3.186874,55.944494);
    private static final double HOVER_ANGLE = 999;


    //Initalizes all regions, restaurants and utility classes
    @BeforeAll
    static void beforeAll(){
        pathFinder = new PathFinder();
        lngLatHandler = new LngLatHandler();
        central = new NamedRegion("central",new LngLat[]{new LngLat(-3.192473,55.946233),
                new LngLat(-3.192473,55.942617),
                new LngLat(-3.184319,55.942617),
                new LngLat(-3.184319,55.946233)});
        noFlyZones = new NamedRegion[]{
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
                })};
        civerinos = new Restaurant("Civerinos Slice",
                new LngLat(-3.1912869215011597,55.945535152517735),
                new DayOfWeek[]{DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY,DayOfWeek.SUNDAY},
                new Pizza[]{
                        new Pizza("R1: Margarita",1000),
                        new Pizza("R1: Calzone",1400)
                });
        soraLella = new Restaurant(
                "Sora Lella Vegan Restaurant",
                new LngLat(-3.202541470527649, 55.943284737579376),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY},
                new Pizza[]{
                        new Pizza("R2: Meat Lover", 1400),
                        new Pizza("R2: Vegan Delight", 1100)
                });

        dominos = new Restaurant(
                "Domino's Pizza - Edinburgh - Southside",
                new LngLat(-3.1838572025299072, 55.94449876875712),
                new DayOfWeek[]{DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{
                        new Pizza("R3: Super Cheese", 1400),
                        new Pizza("R3: All Shrooms", 900)
                });

        sodeberg = new Restaurant(
                "Sodeberg Pavillion",
                new LngLat(-3.1940174102783203, 55.94390696616939),
                new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{
                        new Pizza("R4: Proper Pizza", 1400),
                        new Pizza("R4: Pineapple & Ham & Cheese", 900)
                });

        laTrattoria = new Restaurant(
                "La Trattoria",
                new LngLat(-3.1810810679852035, 55.938910643735845),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{
                        new Pizza("R5: Pizza Dream", 1400),
                        new Pizza("R5: My kind of pizza", 900)
                });

        halalPizza = new Restaurant(
                "Halal Pizza",
                new LngLat(-3.185428203143916, 55.945846113595),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{
                        new Pizza("R6: Sucuk delight", 1400),
                        new Pizza("R6: Dreams of Syria", 900)
                });

        worldOfPizza = new Restaurant(
                "World of Pizza",
                new LngLat(-3.179798972064253, 55.939884084483),
                new DayOfWeek[]{DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.TUESDAY},
                new Pizza[]{
                        new Pizza("R7: Hot, hotter, the hottest", 1400),
                        new Pizza("R7: All you ever wanted", 900)
                });
    }

    @Test
    void civerinosPath(){
        List<DroneMove> pathTo = pathFinder.findPath("abc123",
                AT,civerinos.location(),noFlyZones,central);

        List<DroneMove> pathBack = pathFinder.findPath("abc123",
                civerinos.location(),AT,noFlyZones,central);

        System.out.printf("No. moves from AT to %s: %d --- Num moves for path back to AT: %d\n",
                civerinos.name(),pathTo.size(),pathBack.size());

        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathTo));
        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathBack));
        Assertions.assertTrue(centralAreaRequirementMet(pathBack));
    }

    @Test
    void soraLellaPath(){
        List<DroneMove> pathTo = pathFinder.findPath("abc123",
                AT,soraLella.location(),noFlyZones,central);

        List<DroneMove> pathBack = pathFinder.findPath("abc123",
                soraLella.location(),AT,noFlyZones,central);

        System.out.printf("No. moves from AT to %s: %d --- Num moves for path back to AT: %d\n",
                soraLella.name(),pathTo.size(),pathBack.size());

        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathTo));
        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathBack));
        Assertions.assertTrue(centralAreaRequirementMet(pathBack));
    }

    @Test
    void dominosPath(){
        List<DroneMove> pathTo = pathFinder.findPath("abc123",
                AT,dominos.location(),noFlyZones,central);

        List<DroneMove> pathBack = pathFinder.findPath("abc123",
                dominos.location(),AT,noFlyZones,central);

        System.out.printf("No. moves from AT to %s: %d --- Num moves for path back to AT: %d\n",
                dominos.name(),pathTo.size(),pathBack.size());

        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathTo));
        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathBack));
        Assertions.assertTrue(centralAreaRequirementMet(pathBack));
    }

    @Test
    void soderbergPath(){
        List<DroneMove> pathTo = pathFinder.findPath("abc123",
                AT,sodeberg.location(),noFlyZones,central);

        List<DroneMove> pathBack = pathFinder.findPath("abc123",
                sodeberg.location(),AT,noFlyZones,central);

        System.out.printf("No. moves from AT to %s: %d --- Num moves for path back to AT: %d\n",
                sodeberg.name(),pathTo.size(),pathBack.size());

        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathTo));
        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathBack));
        Assertions.assertTrue(centralAreaRequirementMet(pathBack));
    }

    @Test
    void laTrattoriaPath(){
        List<DroneMove> pathTo = pathFinder.findPath("abc123",
                AT,laTrattoria.location(),noFlyZones,central);

        List<DroneMove> pathBack = pathFinder.findPath("abc123",
                laTrattoria.location(),AT,noFlyZones,central);

        System.out.printf("No. moves from AT to %s: %d --- Num moves for path back to AT: %d\n",
                laTrattoria.name(),pathTo.size(),pathBack.size());

        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathTo));
        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathBack));
        Assertions.assertTrue(centralAreaRequirementMet(pathBack));
    }

    @Test
    void halalPizzaPath(){
        List<DroneMove> pathTo = pathFinder.findPath("abc123",
                AT,halalPizza.location(),noFlyZones,central);

        List<DroneMove> pathBack = pathFinder.findPath("abc123",
                halalPizza.location(),AT,noFlyZones,central);

        System.out.printf("No. moves from AT to %s: %d --- Num moves for path back to AT: %d\n",
                halalPizza.name(),pathTo.size(),pathBack.size());

        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathTo));
        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathBack));
        Assertions.assertTrue(centralAreaRequirementMet(pathBack));
    }

    @Test
    void worldOfPizzaPath(){
        List<DroneMove> pathTo = pathFinder.findPath("abc123",
                AT,worldOfPizza.location(),noFlyZones,central);

        List<DroneMove> pathBack = pathFinder.findPath("abc123",
                worldOfPizza.location(),AT,noFlyZones,central);

        System.out.printf("No. moves from AT to %s: %d --- Num moves for path back to AT: %d\n",
                worldOfPizza.name(),pathTo.size(),pathBack.size());

        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathTo));
        Assertions.assertFalse(pathGoesThroughNoFlyZone(pathBack));
        Assertions.assertTrue(centralAreaRequirementMet(pathBack));
    }


    private static boolean pointInNoFlyZone(LngLat point,NamedRegion[] noFlyZones){
        for(NamedRegion noFlyZone : noFlyZones){
            if(lngLatHandler.isInRegion(point,noFlyZone)){
                return true;
            }
        }
        return false;
    }

    private static boolean pathGoesThroughNoFlyZone(List<DroneMove> path){
        boolean pathInNoFlyZone = pointInNoFlyZone(
                new LngLat(path.get(0).getFromLongitude(),path.get(0).getFromLatitude()),noFlyZones);
        for(DroneMove move : path){
            pathInNoFlyZone = pathInNoFlyZone && pointInNoFlyZone(
                    new LngLat(move.getFromLongitude(),move.getFromLatitude()),noFlyZones);
        }
        return pathInNoFlyZone;
    }

    private static boolean centralAreaRequirementMet(List<DroneMove> path){
        int re_enteredCentral = 0;
        for(DroneMove move: path){
            LngLat pos = new LngLat(move.getFromLongitude(), move.getFromLatitude());
            if (re_enteredCentral == 0 && lngLatHandler.isInRegion(pos,central)){
                re_enteredCentral = 1;
            }
            if (re_enteredCentral == 1 && !lngLatHandler.isInRegion(pos,central)){
                return false;
            }
        }
        return true;
    }


}
