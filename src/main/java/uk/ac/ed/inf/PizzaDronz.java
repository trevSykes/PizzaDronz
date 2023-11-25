package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PizzaDronz {
    private static final int INVALID_ARGUMENTS = 1;
    private static final int REST_FAILURE = 2;
    private static final int NULL_RESOURCE = 3;
    private static final int FLIGHTPATH_CALCULATION_FAILURE = 4;
    private static final int SERIALIZATION_FAILURE = 5;
    private static final int SUCCESS = 0;
    public static void main( String[] args )
    {

        if(args.length > 2){
            System.err.println("Too many arguments given. Arguments should be date (yyyy-MM-dd) and REST url.");
            System.exit(INVALID_ARGUMENTS);
        } else if(args.length < 2){
            System.err.println("Missing arguments. Arguments should be date (yyyy-MM-dd) and REST url.");
            System.exit(INVALID_ARGUMENTS);
        }
        String dateString = args[0];
        String url = args[1];

        //Validate date argument
        try{
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString,dateFormatter);
        } catch (DateTimeParseException e){
            System.err.println("First argument given is not a valid date of the format yyyy-MM-dd.");
            System.exit(INVALID_ARGUMENTS);
        }

        //Validate url argument
        String url_regex = "^(https?://)[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(/.*)?$";
        Pattern p = Pattern.compile(url_regex);
        Matcher matcher = p.matcher(url);
        if(!matcher.matches()){
            System.err.println("Second argument given is not a valid url.");
            System.exit(INVALID_ARGUMENTS);
        }

        Controller controller = new Controller(url,dateString);
        Restaurant[] restaurants = null;
        NamedRegion[] noFlyZones = null;
        NamedRegion centralArea = null;
        Order[] unvalidatedOrders = null;

        //Read resources from REST
        try {
            restaurants = controller.getDefinedRestaurantsFromREST();
            System.out.println("Read Restaurants from REST service...");
            noFlyZones = controller.getNoFlyZonesFromREST();
            System.out.println("Read No Fly Zones from REST service...");
            centralArea = controller.getCentralAreaFromREST();
            System.out.println("Read Central Area from REST service...");
            unvalidatedOrders = controller.getOrdersFromREST(dateString);
            System.out.printf("Read orders for %s from REST service...",dateString);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(REST_FAILURE);
        }

        if(restaurants == null || noFlyZones == null || centralArea == null || unvalidatedOrders == null){
            System.err.println("One of the retrieved resources from the REST service is null");
            System.exit(NULL_RESOURCE);
        }

        //Validate orders
        Order[] validatedOrders = controller.validateOrders(unvalidatedOrders,restaurants);

        //Calculate flightpahts
        List<DroneMove> flightpath = null;
        try{
            flightpath = controller.findPathsForValidOrders(validatedOrders,restaurants,noFlyZones,centralArea);
            System.out.println("Calculated flightpath...");
        } catch (RuntimeException e){
            System.err.println(e.getMessage());
            System.exit(FLIGHTPATH_CALCULATION_FAILURE);
         }

        //Serialize
        try{
            controller.serializeOrders(validatedOrders);
            System.out.println("Serialized orders...");
            controller.serializeFlightPath(flightpath);
            System.out.println("Serialized flightpath...");
            controller.geoSerializeFlightPath(flightpath);
            System.out.println("Geo-serialized flightpath...");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(SERIALIZATION_FAILURE);
        }

        System.out.printf("Success! Orders validated and flightpath calculated for %s.",dateString);
        System.exit(SUCCESS);
    }
}
