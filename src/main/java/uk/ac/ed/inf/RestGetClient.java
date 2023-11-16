package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class RestGetClient {
    /* URL constants for each resource */
    static final String RESTAURANTS_LOC = "/restaurants";
    static final String ORDERS_LOC = "/orders";
    static final String NO_FLY_LOC = "/noFlyZones";
    static final String CENTRAL_LOC = "/centralArea";
    static final String IS_ALIVE_LOC = "/isAlive";
    private static ObjectMapper mapper;
    private static String baseURL;
    public RestGetClient(String restURL){
        baseURL = restURL;
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); //Required for parsing LocalDate objects

        //Add CreditCardInformation deserializer to mapper
        SimpleModule module = new SimpleModule("CustomCreditCardInformationDeserializer");
        module.addDeserializer(CreditCardInformation.class, new CustomCreditCardInformationDeserializer());
        mapper.registerModule(module);
    }

    /**
     *
     * @return Array of Restaurant objects from REST server
     */
    public Restaurant[] getRestaurants() throws IOException{
        try{
            return mapper.readValue(new URL(baseURL+ RESTAURANTS_LOC), Restaurant[].class);
        } catch (IOException e){
            throw new IOException("Error with retrieving REST server's Restaurants resource\n\n"+e.getMessage());
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
            System.err.println("Issue with checking REST server is online\n\n"+e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param date String of the date (YYYY-MM-DD) to be obtained from REST server's order resource
     * @return Array of Order objects that have been ordered on the date
     */
    public Order[] getOrders(String date) throws IOException{
        try{
            return mapper.readValue(new URL(baseURL+ORDERS_LOC+'/'+date), Order[].class);
        } catch (IOException e){
            throw new IOException("Error with parsing REST server's Orders resource\n\n"+e.getMessage());
        }
    }

    /**
     *
     * @return Array of NamedRegion found in REST service's NoFlyZone resource
     */
    public NamedRegion[] getNoFlyZones() throws IOException{
        try{
            return mapper.readValue(new URL(baseURL+NO_FLY_LOC), NamedRegion[].class);
        } catch (IOException e){
            throw new IOException("Error with parsing REST server's NoFlyZone resource\n\n"+e.getMessage());
        }
    }

    /**
     *
     * @return NamedRegion of Central Area defined in REST service
     */
    public NamedRegion getCentralArea() throws IOException{
        try{
            return mapper.readValue(new URL(baseURL+CENTRAL_LOC), NamedRegion.class);
        } catch (IOException e){
            throw new IOException("Error with parsing REST server's CentralArea resource\n\n"+e.getMessage());
        }
    }

    //Custom deserializer
    private static class CustomCreditCardInformationDeserializer extends StdDeserializer<CreditCardInformation> {
        private CustomCreditCardInformationDeserializer(){
            this(null);
        }

        public CustomCreditCardInformationDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public CreditCardInformation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException{
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode node = codec.readTree(jsonParser);

            JsonNode ccNumNode = node.get("creditCardNumber");
            String ccNum = ccNumNode.asText();

            JsonNode ccExpiryNode = node.get("creditCardExpiry");
            String ccExpiry = ccExpiryNode.asText();

            JsonNode cvvNode = node.get("cvv");
            String cvv = cvvNode.asText();

            return new CreditCardInformation(ccNum,ccExpiry,cvv);
        }

    }


}
