package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final RestGetClient restClient;
    private final OrderValidator orderValidator;
    private final Serializer serializer;
    private final PathFinder pathFinder;
    private Order[] validatedOrders;
    private final LngLat AT = new LngLat(-3.186874, 55.944494);


    public Controller(String restURL, String date){
        restClient = new RestGetClient(restURL);
        orderValidator = new OrderValidator();
        pathFinder = new PathFinder();
        serializer = new Serializer(date);

    }

    public Order[] getOrdersFromREST(String date) throws Exception{
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getOrders(date);
    }

    public Restaurant[] getDefinedRestaurantsFromREST() throws Exception {
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getRestaurants();
    }

    public NamedRegion[] getNoFlyZonesFromREST() throws Exception {
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getNoFlyZones();
    }

    public NamedRegion getCentralAreaFromREST() throws Exception {
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getCentralArea();
    }

    public Order[] validateOrders(Order[] unvalidatedOrders, Restaurant[] definedRestaurants){
        validatedOrders = new Order[unvalidatedOrders.length];
        for(int i=0;i< unvalidatedOrders.length;i++){
            Order orderToValidate = unvalidatedOrders[i];
            validatedOrders[i] = orderValidator.validateOrder(orderToValidate,definedRestaurants);
        }
        System.out.printf("Successfully validated %s orders%n",unvalidatedOrders.length);
        return validatedOrders;
    }

    public void serializeOrders(Order[] validatedOrders) throws IOException {
        serializer.serializeOrders(validatedOrders);
    }

    public List<DroneMove> findPathsForValidOrders(Restaurant[] definedRestaurants, NamedRegion[] noFlyZones,
                                                   NamedRegion centralArea) throws RuntimeException{
        List<DroneMove> droneMoves = new ArrayList<>();
        for(Order order : validatedOrders){
            //Only calculate flight paths for valid orders
            if(order.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)){
                Restaurant restaurant = orderValidator.findValidRestaurant(order.getPizzasInOrder(),definedRestaurants);
                //Add flight moves from AT to restaurant to total moves
                List<DroneMove> ATtoRestaurant = pathFinder.findPath(order.getOrderNo(),AT,restaurant.location(),
                        noFlyZones,centralArea);
                droneMoves.addAll(ATtoRestaurant);
                droneMoves.add(pathFinder.makeHoverMove()); //Include hover while at restaurant
                pathFinder.clearCurrentPath();

                //Add flight moves from restaurant to AT to total moves
                List<DroneMove> RestaurantToAT = pathFinder.findPath(order.getOrderNo(),restaurant.location(),AT,
                        noFlyZones,centralArea);
                droneMoves.addAll(RestaurantToAT);
                droneMoves.add(pathFinder.makeHoverMove()); //Include hover while at Appleton Tower
                pathFinder.clearCurrentPath();
                //Update order status to delivered
                order.setOrderStatus(OrderStatus.DELIVERED);
            }
        }
        return droneMoves;
    }

    public void serializeFlightPath(List<DroneMove> droneMoves) throws IOException {
        serializer.serializeFlights(droneMoves);
    }

    public void geoSerializeFlightPath(List<DroneMove> droneMoves) throws IOException{
        //Convert DroneMoves to array of coordinates
        double[][] coordinates = new double[droneMoves.size()+1][2];
        //First coordinate will come from start of first move
        coordinates[0] = new double[]{droneMoves.get(0).getFromLongitude(),droneMoves.get(0).getFromLatitude()};
        //Add destination coordinates of each successive move
        for (int i = 0;i < droneMoves.size();i++){
            coordinates[i+1] = new double[]{droneMoves.get(i).getToLongitude(),droneMoves.get(i).getToLatitude()};
        }
        serializer.geoSerialisePaths(coordinates);
    }

}
