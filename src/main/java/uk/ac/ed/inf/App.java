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


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        if(args.length > 2){
            System.err.println("ERROR: Too many arguments given. Arguments should be date (yyyy-MM-dd) and REST url");
        }
        String dateString = args[0];
        String url = args[1];

        //Validate date argument
        try{
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString,dateFormatter);
        } catch (DateTimeParseException e){
            System.err.println("ERROR: First argument given is not a valid date of the format yyyy-MM-dd." +
                    "\nPlease run terminal command with a valid date");
            return;
        }

        //Validate url argument
        String url_regex = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)";
        Pattern p = Pattern.compile(url_regex);
        Matcher matcher = p.matcher(url);
        if(!matcher.matches()){
            System.err.println("ERROR: Second argument given is not a valid url." +
                    "\nPlease run terminal command with a valid url");
            return;
        }

        Controller controller = new Controller(url,dateString);
        Restaurant[] restaurants;
        NamedRegion[] noFlyZones;
        NamedRegion centralArea ;
        Order[] unvalidatedOrders;

        try {
            restaurants = controller.getDefinedRestaurantsFromREST();
            System.out.println("Read Restaurants from REST service...");
            noFlyZones = controller.getNoFlyZonesFromREST();
            System.out.println("Read No Fly Zones from REST service...");
            centralArea = controller.getCentralAreaFromREST();
            System.out.println("Read Central Area from REST service...");
            unvalidatedOrders = controller.getOrdersFromREST(dateString);
            System.out.printf("Read orders for %s from REST service...%n",dateString);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        Order[] validatedOrders = controller.validateOrders(unvalidatedOrders,restaurants);
        List<DroneMove> flightpath;
        try{
            flightpath = controller.findPathsForValidOrders(restaurants,noFlyZones,centralArea);
        } catch (RuntimeException e){
            System.err.println(e.getMessage());
            return;
         }

        try{
            controller.serializeOrders(validatedOrders);
            System.out.println("Serialized orders...");
            controller.serializeFlightPath(flightpath);
            System.out.println("Serialized flightpath...");
            controller.geoSerializeFlightPath(flightpath);
            System.out.println("Geo-serialized flightpath...");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("Success!\nOrders validated and flightpath calculated for PizzaDronz.");

    }
}
