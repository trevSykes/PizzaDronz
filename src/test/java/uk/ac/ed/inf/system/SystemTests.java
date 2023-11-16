package uk.ac.ed.inf.system;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.Controller;
import uk.ac.ed.inf.DroneMove;
import uk.ac.ed.inf.OrderValidator;
import uk.ac.ed.inf.RestGetClient;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import java.io.IOException;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class SystemTests {
    private static String restURL;
    private static String date;
    @BeforeAll
    static void setUp(){
        restURL = "https://ilp-rest.azurewebsites.net";
    }

    @Test
    void test2023_09_02(){
        long startTime = System.currentTimeMillis();
        date = "2023-09-02";
        Controller controller = new Controller(restURL,date);
        OrderValidator orderValidator = new OrderValidator();
        Order[] unvalidatedOrders;
        Restaurant[] restaurants;
        NamedRegion[] noFlyZones;
        NamedRegion centralArea;

        Set<Restaurant> visitedRestaurants = new HashSet<>();
        try{
            unvalidatedOrders = controller.getOrdersFromREST(date);
            restaurants = controller.getDefinedRestaurantsFromREST();
            noFlyZones = controller.getNoFlyZonesFromREST();
            centralArea = controller.getCentralAreaFromREST();
        } catch (Exception e){
            System.err.println(e.getMessage());
            return;
        }
        Order[] validatedOrders = controller.validateOrders(unvalidatedOrders,restaurants);
        int validCount = 0;
        System.out.println("ORDER_NO - VALIDATION CODE - STATUS_CODE");
        for(Order order: validatedOrders){
            System.out.println(order.getOrderNo()+" - "+order.getOrderValidationCode()+" - "+order.getOrderStatus());
            if(order.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)){
                validCount++;
            }
        }
        System.out.println("Total valid orders = "+validCount);

        List<DroneMove> droneMoves = controller.findPathsForValidOrders(validatedOrders,restaurants,noFlyZones,centralArea);

        int deliveredCount = 0;
        System.out.println("ORDER_NO - VALIDATION CODE - STATUS_CODE");
        for(Order order: validatedOrders){
            System.out.println(order.getOrderNo()+" - "+order.getOrderValidationCode()+" - "+order.getOrderStatus());
            if(order.getOrderStatus().equals(OrderStatus.DELIVERED)){
                deliveredCount++;
                visitedRestaurants.add(orderValidator.findValidRestaurant(order.getPizzasInOrder(), restaurants));
            }
        }
        System.out.println("Total delivered orders = "+deliveredCount);

        try{
            controller.serializeOrders(validatedOrders);
            controller.serializeFlightPath(droneMoves);
            controller.geoSerializeFlightPath(droneMoves);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        String restaurantsVisited = visitedRestaurants.stream()
                .map(Restaurant::name) // Extract the name from each Restaurant object
                .collect(Collectors.joining(", ")); // Join the names with a comma and space
        System.out.println("Restaurants visited:"+restaurantsVisited);
        System.out.println("Total time taken for processing : "+(endTime-startTime));
    }

    @Test
    void test2023_10_30(){
        long startTime = System.currentTimeMillis();
        date = "2023-10-30";
        Controller controller = new Controller(restURL,date);
        OrderValidator orderValidator = new OrderValidator();
        Order[] unvalidatedOrders;
        Restaurant[] restaurants;
        NamedRegion[] noFlyZones;
        NamedRegion centralArea;

        Set<Restaurant> visitedRestaurants = new HashSet<>();
        try{
            unvalidatedOrders = controller.getOrdersFromREST(date);
            restaurants = controller.getDefinedRestaurantsFromREST();
            noFlyZones = controller.getNoFlyZonesFromREST();
            centralArea = controller.getCentralAreaFromREST();
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }
        Order[] validatedOrders = controller.validateOrders(unvalidatedOrders,restaurants);
        int validCount = 0;
        System.out.println("ORDER_NO - VALIDATION CODE - STATUS_CODE");
        for(Order order: validatedOrders){
            System.out.println(order.getOrderNo()+" - "+order.getOrderValidationCode()+" - "+order.getOrderStatus());
            if(order.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)){
                validCount++;
            }
        }
        System.out.println("Total valid orders = "+validCount);

        List<DroneMove> droneMoves = controller.findPathsForValidOrders(validatedOrders,restaurants,noFlyZones,centralArea);

        int deliveredCount = 0;
        System.out.println("ORDER_NO - VALIDATION CODE - STATUS_CODE");
        for(Order order: validatedOrders){
            System.out.println(order.getOrderNo()+" - "+order.getOrderValidationCode()+" - "+order.getOrderStatus());
            if(order.getOrderStatus().equals(OrderStatus.DELIVERED)){
                deliveredCount++;
                visitedRestaurants.add(orderValidator.findValidRestaurant(order.getPizzasInOrder(), restaurants));
            }
        }
        System.out.println("Total delivered orders = "+deliveredCount);

        try{
            controller.serializeOrders(validatedOrders);
            controller.serializeFlightPath(droneMoves);
            controller.geoSerializeFlightPath(droneMoves);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        String restaurantsVisited = visitedRestaurants.stream()
                .map(Restaurant::name)
                .collect(Collectors.joining(", "));
        System.out.println("Restaurants visited:"+restaurantsVisited);
        System.out.println("Total time taken for processing : "+(endTime-startTime));
    }


}