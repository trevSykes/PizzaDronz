package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class RestGetClient {
    /* URL constants for each resource */
    static final String RESTAURANTS_LOC = "/restaurants";
    static final String ORDERS_LOC = "/orders/";
    static final String NO_FLY_LOC = "/noFlyZones";
    static final String CENTRAL_LOC = "/centralArea";
    static final String IS_ALIVE_LOC = "/isAlive";
    private static ObjectMapper mapper;
    private static String baseURL;
    public RestGetClient(String restURL){
        baseURL = restURL;
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); //Required for parsing LocalDate objects
    }

    /**
     *
     * @return Array of Restaurant objects from REST server
     */
    public Restaurant[] getRestaurants(){
        try{
            return mapper.readValue(new URL(baseURL+ RESTAURANTS_LOC), Restaurant[].class);
        } catch (IOException e){
            System.err.println("Error with retrieving REST server's restaurant resource");
            e.printStackTrace();
            //TODO: Gracefully die
            return null;
        }
    }

    /**
     *
     * @return boolean indicating REST server is alive
     */
    public boolean restIsAlive(){
        try{
            return Objects.equals(mapper.readValue(new URL(baseURL+IS_ALIVE_LOC), String.class), "true");
        } catch (IOException e) {
            System.err.println("Issue with checking REST server is online");
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param date String of the date (YYYY-MM-DD) to be obtained from REST server's order resource
     * @return Array of Order objects that have been ordered on the date
     */
    public Order[] getOrders(String date){
        try{
            return mapper.readValue(new URL(baseURL+ORDERS_LOC+date), Order[].class);
        } catch (IOException e){
            System.err.println("Error with parsing REST server's Orders resource");
            e.printStackTrace();
            //TODO: Gracefully die
            return null;
        }
    }

    /**
     *
     * @return Array of NamedRegion found in REST service's NoFlyZone resource
     */
    public NamedRegion[] getNoFlyZones(){
        try{
            return mapper.readValue(new URL(baseURL+NO_FLY_LOC), NamedRegion[].class);
        } catch (IOException e){
            System.err.println("Error with parsing REST server's NoFlyZone resource");
            e.printStackTrace();
            //TODO: Gracefully die
            return null;
        }
    }

    /**
     *
     * @return NamedRegion of Central Area defined in REST service
     */
    public NamedRegion getCentralArea(){
        try{
            return mapper.readValue(new URL(baseURL+CENTRAL_LOC), NamedRegion.class);
        } catch (IOException e){
            System.err.println("Error with parsing REST server's CentralArea resource");
            e.printStackTrace();
            //TODO: Gracefully die and possibly do more comprehensive logging
            return null;
        }
    }

}
