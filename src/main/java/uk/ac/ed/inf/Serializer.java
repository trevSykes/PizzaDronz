package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mapbox.services.commons.geojson.*;
import uk.ac.ed.inf.ilp.data.Order;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class Serializer {

    private static ObjectMapper mapper;
    private static String DATE;

    public Serializer(String date){
        DATE = date;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); //Enables pretty print of files
        mapper.registerModule(new JavaTimeModule()); //Required for handling LocalDate objects
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-ddd"));

    }

    public void serializeOrders(Order[] orders) throws IOException {
        mapper.writeValue(new File("resultfiles/deliveries-"+DATE+".json"),orders);
    }

    public void serializeFlights(List<DroneMove> moves) throws IOException {
        mapper.writeValue(new File("resultfiles/flightpath-"+DATE+".json"),moves);
    }

    public void geoSerialisePaths(List<DroneMove> moves) throws IOException {
        double[][] coordinates = new double[2][moves.size()+1];
        //First coordinate will come from AT
        coordinates[0] = new double[]{moves.get(0).getFromLongitude(),moves.get(0).getFromLongitude()};
        //Add destination coordinates of each ove
        for (int i = 0;i < moves.size();i++){
            coordinates[i+1] = new double[]{moves.get(i).getToLongitude(),moves.get(i).getToLongitude()};
        }
        LineString path = LineString.fromCoordinates(coordinates);
        Feature pathFeature = Feature.fromGeometry(path);
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(new Feature[]{pathFeature});

        mapper.writeValue(new File("resultfiles/drone-"+DATE+".geojson"),featureCollection);
    }

}
