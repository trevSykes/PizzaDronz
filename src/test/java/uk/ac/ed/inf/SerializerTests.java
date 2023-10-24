package uk.ac.ed.inf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.time.LocalDate;

public class SerializerTests {
    private static Order[] orders;
    @BeforeAll
    static void beforeAll(){
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
        orders = new Order[]{order1,order2};
    }

    @Test
    void generateDeliveries(){
        Serializer serializer = new Serializer("2023-10-12");
        try {
            serializer.serializeOrders(orders);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
