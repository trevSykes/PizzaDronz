package uk.ac.ed.inf.unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.RestGetClient;
import uk.ac.ed.inf.ilp.data.*;

import java.io.IOException;

public class RestGetTests {
    private static RestGetClient restClient;

    @BeforeAll
    static void setUp(){
        restClient = new RestGetClient("https://ilp-rest.azurewebsites.net");
    }
    @Test
    void getRestaurantsTest(){
        Restaurant[] retrievedRestaurants = null;
        try {
            retrievedRestaurants = restClient.getRestaurants();
        } catch (IOException e){
            System.err.println(e.getMessage());
        }

    }

    @Test
    void getOrdersTest(){
        Order[] orders = null;
        try{
            orders = restClient.getOrders("2023-09-02");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    @Test
    void checkNullParsedForNoOrderDate(){
        Order[] orders = null;
        try{
            orders = restClient.getOrders("2025-09-02");
            Assertions.assertNull(orders);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void getNamedRegionTest(){
        NamedRegion[] noflyzones = null;
        try{
            noflyzones = restClient.getNoFlyZones();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void isRESTAlive(){
        Assertions.assertTrue(restClient.restIsAlive());
    }


}
