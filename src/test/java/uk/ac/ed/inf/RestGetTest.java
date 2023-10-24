package uk.ac.ed.inf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.DayOfWeek;

//TODO Write tests for other tings

public class RestGetTest {
    private static Restaurant[] definedRestaurants;
    private static RestGetClient restClient;

    @BeforeAll
    static void setUpData(){
        restClient = new RestGetClient("https://ilp-rest.azurewebsites.net");
        Restaurant civerinos = new Restaurant("Civerinos Slice",
                new LngLat(-3.1912869215011597, 55.945535152517735),
                new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY },
                new Pizza[] {
                        new Pizza("Margarita", 1000),
                        new Pizza("Calzone", 1400)
                }
        );

        Restaurant soraLella = new Restaurant("Sora Lella Vegan Restaurant",
                new LngLat(-3.202541470527649, 55.943284737579376),
                new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY },
                new Pizza[] {
                        new Pizza("Meat Lover", 1400),
                        new Pizza("Vegan Delight", 1100)
                }
        );

        Restaurant dominos = new Restaurant("Domino's Pizza - Edinburgh - Southside",
                new LngLat(-3.1838572025299072, 55.94449876875712),
                new DayOfWeek[] { DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY },
                new Pizza[] {
                        new Pizza("Super Cheese", 1400),
                        new Pizza("All Shrooms", 900)
                }
        );

        Restaurant sodeberg = new Restaurant("Sodeberg Pavillion",
                new LngLat(-3.1940174102783203, 55.94390696616939),
                new DayOfWeek[] { DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY,
                        DayOfWeek.SUNDAY },
                new Pizza[] {
                        new Pizza("Proper Pizza", 1400),
                        new Pizza("Pineapple & Ham & Cheese", 900)
                }
        );

        definedRestaurants = new Restaurant[]{civerinos,soraLella,dominos,sodeberg};
    }

    private static boolean objectListEquals(Object[] o1, Object[] o2){
        if (o1.length != o2.length){
            return false;
        }
        for (int i=0;i<o1.length;i++){
            if (o1[i].hashCode() != o2[i].hashCode()){
                return false;
            }
        }
        return true;
    }
    private static boolean restaurantEquals(Restaurant r1, Restaurant r2){
        boolean name = (r1.name().equals(r2.name()));
        boolean location = (r1.location().hashCode()==r2.location().hashCode());
        boolean openingDays = objectListEquals(r1.openingDays(),r2.openingDays());
        boolean menu =  objectListEquals(r1.menu(),r2.menu());
        return name && location && openingDays && menu;
    }
    @Test
    void getRestaurantsTest(){
        Restaurant[] retrievedRestaurants = restClient.getRestaurants();
        boolean allEquals = true;
        for (int i=0; i<retrievedRestaurants.length;i++){
            allEquals = allEquals && restaurantEquals(retrievedRestaurants[i],definedRestaurants[i]);
        }
        Assertions.assertTrue(allEquals);
    }

    @Test
    void isRESTAlive(){
        Assertions.assertTrue(restClient.restIsAlive());
    }
}
