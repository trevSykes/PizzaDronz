package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Controller {
    private final RestGetClient restClient;
    private final OrderValidator orderValidator;
    private final Serializer serializer;
    private final PathFinder pathFinder;
    private final LngLat AT = new LngLat(-3.186874, 55.944494);

    private final HashMap<String,List<DroneMove>> routeCache = new HashMap<>();

    public Controller(String restURL, String date) throws IOException {
        restClient = new RestGetClient(restURL);
        orderValidator = new OrderValidator();
        pathFinder = new PathFinder();
        serializer = new Serializer(date);
    }

    /**
     *
     * @param date String of the date from which orders are being processed
     * @return Array of Orders retrieved from the REST service
     * @throws Exception REST service is down
     */
    public Order[] getOrdersFromREST(String date) throws Exception{
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getOrders(date);
    }

    /**
     *
     * @return Array of Restaurant objects retrieved from REST
     * @throws Exception REST service is down
     */
    public Restaurant[] getDefinedRestaurantsFromREST() throws Exception {
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getRestaurants();
    }

    /**
     *
     * @return Array of NamedRegion objects representing No-Fly zones from the REST service
     * @throws Exception REST service is down
     */
    public NamedRegion[] getNoFlyZonesFromREST() throws Exception {
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getNoFlyZones();
    }

    /**
     *
     * @return NamedRegion object representing the Central Area retrieved from the REST service
     * @throws Exception Rest Service is down
     */
    public NamedRegion getCentralAreaFromREST() throws Exception {
        if(!restClient.restIsAlive()){
            throw new Exception("REST service is not live");
        }
        return restClient.getCentralArea();
    }

    /**
     *
     * @param unvalidatedOrders Array of Order objects that are the raw orders retrieved from the REST service
     * @param definedRestaurants Array of Restaurants objects defined by the REST service
     * @return Array of Order objects that are unvalidatedOrders elements with the appropriate OrderStatusCode and
     *         OrderValidationCode set.
     */
    public Order[] validateOrders(Order[] unvalidatedOrders, Restaurant[] definedRestaurants){
        Order[] validatedOrders = new Order[unvalidatedOrders.length];
        for(int i=0;i< unvalidatedOrders.length;i++){
            Order orderToValidate = unvalidatedOrders[i];
            validatedOrders[i] = orderValidator.validateOrder(orderToValidate,definedRestaurants);
        }
        return validatedOrders;
    }

    /**
     * Calculates the drone's flightpath for servicing all valid orders in a given day
     * @param definedRestaurants Array of Restaurant objects that were retrieved from the REST service
     * @param noFlyZones Array of NamedRegion objects representing No-Fly Zones retrieved from the REST service
     * @param centralArea NamedRegion object representing the Central Area retrieved from the REST service
     * @return List of DroneMoves illustrating the drones flightpath for a given day
     */
    public List<DroneMove> findPathsForValidOrders(Order[] validatedOrders, Restaurant[] definedRestaurants,
                                                   NamedRegion[] noFlyZones, NamedRegion centralArea){
        List<DroneMove> droneMoves = new ArrayList<>();
        for(Order order : validatedOrders){
            //Only calculate flight paths for valid orders
            if(order.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)){
                Restaurant restaurant = orderValidator.findValidRestaurant(order.getPizzasInOrder(),definedRestaurants);
                if(routeCache.containsKey(restaurant.name())){
                    droneMoves.addAll(routeCache.get(restaurant.name()));
                    order.setOrderStatus(OrderStatus.DELIVERED);
                } else {
                    //Add flight moves from AT to restaurant to total moves
                    try {
                        List<DroneMove> route = new ArrayList<>();

                        List<DroneMove> ATtoRestaurant = pathFinder.findPath(order.getOrderNo(), AT, restaurant.location(),
                                noFlyZones, centralArea);
                        route.addAll(ATtoRestaurant);
                        route.add(pathFinder.makeHoverMove()); //Include hover while at restaurant
                        pathFinder.clearCurrentPath();

                        //Add flight moves from restaurant to AT to total moves
                        List<DroneMove> RestaurantToAT = pathFinder.findPath(order.getOrderNo(), restaurant.location(),
                                AT, noFlyZones, centralArea);
                        route.addAll(RestaurantToAT);
                        route.add(pathFinder.makeHoverMove()); //Include hover while at Appleton Tower
                        pathFinder.clearCurrentPath();
                        //Update order status to delivered
                        order.setOrderStatus(OrderStatus.DELIVERED);

                        droneMoves.addAll(route);
                        //Add route to cache
                        routeCache.put(restaurant.name(), route);

                    } catch (Exception e){
                        System.err.println(e.getMessage());
                        order.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
                    }
                }


            }
        }
        return droneMoves;
    }

    /**
     * Serializes the validatedOrders into a JSON file titled deliveries-DATE.json where DATE is the
     * string used to initialize the controller.
     * @param validatedOrders Array of validated orders
     * @throws IOException Error with writing POJOs into JSON objects and writing to file.
     */
    public void serializeOrders(Order[] validatedOrders) throws IOException {
        serializer.serializeOrders(validatedOrders);
    }

    /**
     * Serializes a List of DroneMoves illustrating the drone's flightpath for a given day (DATE) to a JSON file with the
     * name flightpath-DATE.json
     * @param droneMoves List of DroneMove objects
     * @throws IOException Error with writing POJOs into JSON objects and writing to file.
     */
    public void serializeFlightPath(List<DroneMove> droneMoves) throws IOException {
        serializer.serializeFlights(droneMoves);
    }


    /**
     * Converts a List of DroneMove to coordinates. Uses the coordinates to make FeatureCollection object composed of a
     * single LineString feature that is built from the coordinates. From this a GeoJSON file is created.
     * @param droneMoves List of DroneMoves representing the path for the entire day
     * @throws IOException Error with writing POJOs to GeoJSON objects and writing to file
     */
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

    /**
     * Helper function for the special case when the retrieved orders are empty; the files serialized are empty
     * @throws IOException Error with writing POJOs to JSON and GeoJSON files.
     */
    public void outputEmptyFilesForNoOrders() throws IOException{
        serializer.serializeOrders(new Order[0]);
        serializer.serializeFlights(new ArrayList<>());
        serializer.geoSerialisePaths(new double[0][2]);
    }

}
