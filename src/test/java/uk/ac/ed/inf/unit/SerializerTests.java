package uk.ac.ed.inf.unit;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.DroneMove;
import uk.ac.ed.inf.LngLatHandler;
import uk.ac.ed.inf.Serializer;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializerTests {
    private static final LngLatHandler lngLatHandler = new LngLatHandler();
    private static final LngLat AT = new LngLat(-3.186874,55.944494);
    private static final Serializer serializer;

    static {
        try {
            serializer = new Serializer("TEST");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateDeliveries(){
        Order order1 = new Order(
                "1",
                LocalDate.of(2023,10,12),
                OrderStatus.INVALID,
                OrderValidationCode.CVV_INVALID,
                500,
                new Pizza[]{new Pizza("foo",400)},
                new CreditCardInformation("ccNum","10/60","123"));
        Order order2 = new Order(
                "2",
                LocalDate.of(2023,10,12),
                OrderStatus.INVALID,
                OrderValidationCode.CVV_INVALID,
                560,
                new Pizza[]{new Pizza("poo",460)},
                new CreditCardInformation("ccNum","10/60","123"));
        try {
            serializer.serializeOrders(new Order[]{order1,order2});
            File f = new File("resultfiles/deliveries-TEST.json");
            assertTrue(f.exists());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    @Test
    void generateFlightpath(){
        LngLat pos1 = lngLatHandler.nextPosition(AT,45);
        LngLat pos2 = lngLatHandler.nextPosition(pos1,67.5);
        DroneMove move1 = new DroneMove("1",AT.lng(),AT.lat(),45,pos1.lng(),pos1.lat());
        DroneMove move2 = new DroneMove("1",pos1.lng(),pos1.lat(),67.5,pos2.lng(),pos2.lat());
        List<DroneMove> moves = new ArrayList<>(List.of((new DroneMove[]{move1, move2})));
        try{
            serializer.serializeFlights(moves);
            File f = new File("resultfiles/flightpath-TEST.json");
            assertTrue(f.exists());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    @Test
    void generateDrone(){
        LngLat pos1 = lngLatHandler.nextPosition(AT,45);
        LngLat pos2 = lngLatHandler.nextPosition(pos1,67.5);
        DroneMove move1 = new DroneMove("1",AT.lng(),AT.lat(),45,pos1.lng(),pos1.lat());
        DroneMove move2 = new DroneMove("1",pos1.lng(),pos1.lat(),67.5,pos2.lng(),pos2.lat());
        List<DroneMove> moves = new ArrayList<>(List.of((new DroneMove[]{move1, move2})));
        //Convert DroneMoves to array of coordinates
        double[][] coordinates = new double[moves.size()+1][2];
        coordinates[0] = new double[]{moves.get(0).getFromLongitude(),moves.get(0).getFromLatitude()};
        for (int i = 0;i < moves.size();i++){
            coordinates[i+1] = new double[]{moves.get(i).getToLongitude(),moves.get(i).getToLatitude()};
        }
        try{
            serializer.geoSerialisePaths(coordinates);
            File f = new File("resultfiles/drone-TEST.geojson");
            assertTrue(f.exists());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

}
