package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.Order;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;


public class Serializer {

    private static ObjectMapper mapper;
    private static String DATE;
    private static String RESULTFILES_PATH;

    public Serializer(String date) throws IOException{
        DATE = date;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); //Enables pretty print of files
        mapper.registerModule(new JavaTimeModule()); //Required for handling LocalDate objects
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));


        SimpleModule orderModule = new SimpleModule();
        //Add custom serializer for Order class
        orderModule.addSerializer(Order.class, new CustomOrderSerializer());
        //Add custom serializers for GeoJson file
        orderModule.addSerializer(FeatureCollection.class, new CustomFeatureCollectionSerializer());
        orderModule.addSerializer(Feature.class, new CustomFeatureSerializer());
        orderModule.addSerializer(LineString.class, new CustomLineStringSerializer());
        orderModule.addSerializer(Properties.class, new CustomPropertiesSerializer());
        mapper.registerModule(orderModule);

        //Sets an absolute path to resultfiles in the top-level of the project structure
        RESULTFILES_PATH = configResultfilesPath();

        //Check if there is a resultfile directory, create one if not
        Path resultfilesPath = Paths.get(RESULTFILES_PATH);
        if(!(Files.exists(resultfilesPath) && Files.isDirectory(resultfilesPath))){
            try {
                Files.createDirectories(resultfilesPath);
            } catch (IOException e) {
                throw new IOException("Issue with making 'resultfile' directory\n\n"+e.getMessage());
            }
        }

    }

    /**
     * This method is needed to set output of the Serializer to the resultfiles directory, for tests and when running
     * uber jar
     * @return String that is the absolute path to the result files directory
     */
    private String configResultfilesPath(){
        //Isolate directories
        String[] userDirTree = System.getProperty("user.dir").split("/");
        userDirTree = Arrays.copyOfRange(userDirTree,1,userDirTree.length);
        String resultFilesPath = "";

        //Navigate to project folder
        for(String dir : userDirTree){
            resultFilesPath += (File.separator+dir);
            if(dir.equals("PizzaDronz")){
                break;
            }
        }
        //Include the resultfiles directory
        resultFilesPath += (File.separator+"resultfiles");
        return resultFilesPath;

    }

    /**
     * Writes the validated Orders of the given day to a JSON file.
     * @param orders Array of Order objects.
     * @throws IOException Issue with ObjectMapper parsing Order objects
     */
    public void serializeOrders(Order[] orders) throws IOException {
        try{
            mapper.writeValue(new File(RESULTFILES_PATH+"/deliveries-"+DATE+".json"),orders);
        } catch (IOException e){
            throw new IOException("Issue with serializing Order objects.\n\n"+e.getMessage());
        }

    }

    /**
     * Writes a List of DroneMoves to a JSON file.
     * @param moves List of DroneMoves representing a drones flight path across the given day.
     * @throws IOException Issue with ObjectMapper parsing DroneMove object
     */
    public void serializeFlights(List<DroneMove> moves) throws IOException {
        try{
            mapper.writeValue(new File(RESULTFILES_PATH+"/flightpath-"+DATE+".json"),moves);
        } catch (IOException e){
            throw new IOException("Issue with serializing DroneMove objects.\n\n"+e.getMessage());
        }

    }

    /**
     * Converts an array of coordinates to a LineString feature to be written into GeoJSON file.
     * @param coordinates Array of 2 element double arrays representing coordinates
     * @throws IOException Issue with ObjectMapper parsing FeatureCollection, Feature, LineString, or Properties objects
     */
    public void geoSerialisePaths(double[][] coordinates) throws IOException {
        //When the provided coordinates are empty, LineString must be null
        LineString path = coordinates.length > 0 ? new LineString(coordinates) : null;
        Feature pathFeature = new Feature(path,(DATE+" path"));
        FeatureCollection featureCollection = new FeatureCollection(new Feature[]{pathFeature});
        try{
            mapper.writeValue(new File(RESULTFILES_PATH+"/drone-"+DATE+".geojson"),featureCollection);
        } catch (IOException e){
            throw new IOException("Issue with serializing FeatureCollection, Feature, LineString, or Properties object.\n\n"
                    +e.getMessage());
        }
    }

    /**
    Large wrapper object used for GeoJSON serialization
     */
    private class FeatureCollection{
        private final String type = "FeatureCollection";
        private final Feature[] features;
        private FeatureCollection(Feature[] features){
            this.features = features;
        }
    }

    /**
    Wrapper object for LineString feature for GeoJSON serialization
     */
    private class Feature{
        private final LineString geometry;
        private final String type = "Feature";
        private final Properties properties;
        private Feature(LineString lineString,String name){
            this.geometry = lineString;
            this.properties = new Properties(name);
        }

    }
    /**
    Object used to represent the drone's flightpath for GeoJSON serialization
     */
    private class LineString{
        private final double[][] coordinates;
        private final String type = "LineString";
        private LineString(double[][] coordinates) {
            this.coordinates = coordinates;
        }
    }
    /**
    Object required for a Feature object in GeoJSON serialization
     */
    private class Properties{
        private final String name;
        private Properties(String name){
            this.name = name;
        }
    }

    /**
     * Custom serializer for order objects. Only serializes the following:
     * orderNo, orderStatus, orderValidationCode, costInPence
     */
    private class CustomOrderSerializer extends JsonSerializer<Order> {
        @Override
        public void serialize(Order order, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("orderNo",order.getOrderNo());
            jsonGenerator.writeStringField("orderStatus",order.getOrderStatus().toString());
            jsonGenerator.writeStringField("orderValidationCode",order.getOrderValidationCode().toString());
            jsonGenerator.writeNumberField("costInPence",order.getPriceTotalInPence());
            jsonGenerator.writeEndObject();
        }
    }
    //Everything below are custom serializers for GeoJSON objects.
    private class CustomFeatureCollectionSerializer extends JsonSerializer<FeatureCollection> {
        @Override
        public void serialize(FeatureCollection featureCollection, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("type",featureCollection.type);
            jsonGenerator.writeObjectField("features",featureCollection.features);
            jsonGenerator.writeEndObject();
        }
    }

    private class CustomFeatureSerializer extends JsonSerializer<Feature> {
        @Override
        public void serialize(Feature feature, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("type",feature.type);
            jsonGenerator.writeObjectField("geometry",feature.geometry);
            jsonGenerator.writeObjectField("properties",feature.properties);
            jsonGenerator.writeEndObject();
        }
    }

    private class CustomLineStringSerializer extends JsonSerializer<LineString> {
        @Override
        public void serialize(LineString path, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("type",path.type);
            jsonGenerator.writeObjectField("coordinates",path.coordinates);
            jsonGenerator.writeEndObject();
        }
    }

    private class CustomPropertiesSerializer extends JsonSerializer<Properties>{
        @Override
        public void serialize(Properties properties, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", properties.name);
            jsonGenerator.writeEndObject();
        }
    }

}
