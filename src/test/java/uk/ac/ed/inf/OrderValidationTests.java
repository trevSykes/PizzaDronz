package uk.ac.ed.inf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;

public class OrderValidationTests {
    OrderValidator orderValidator;
    Restaurant[] definedRestaurants;
    Order baseValidOrder;
    @BeforeEach
    void setUp(){
         orderValidator = new OrderValidator();

        Restaurant civerinos = new Restaurant("Civerinos Slice",
                new LngLat(-3.1912869215011597, 55.945535152517735),
                new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY,
                                                            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY },
                new Pizza[] {
                        new Pizza("Margarita", 1000),
                        new Pizza("Calzone", 1400)
                }
        );

        Restaurant soraLella = new Restaurant("Sora Lella Vegan Restaurant",
                new LngLat(-3.202541470527649, 55.943284737579376),
                new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                                            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY },
                new Pizza[] {
                        new Pizza("Meat Lover", 1400),
                        new Pizza("Vegan Delight", 1100)
                }
        );

        Restaurant dominos = new Restaurant("Domino's Pizza - Edinburgh - Southside",
                new LngLat(-3.1838572025299072, 55.94449876875712),
                new DayOfWeek[] { DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
                                                            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY },
                new Pizza[] {
                        new Pizza("Super Cheese", 1400),
                        new Pizza("All Shrooms", 900)
                }
        );

        Restaurant sodeberg = new Restaurant("Sodeberg Pavillion",
                new LngLat(-3.1940174102783203, 55.94390696616939),
                new DayOfWeek[] { DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY,
                                                                                DayOfWeek.SUNDAY },
                new Pizza[] {
                        new Pizza("Proper Pizza", 1400),
                        new Pizza("Pineapple & Ham & Cheese", 900)
                }
        );

        definedRestaurants = new Restaurant[]{civerinos,soraLella,dominos,sodeberg};

        baseValidOrder = new Order("6FF2819F",
                LocalDate.of(2023,10,5),
                OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED,
                2400,
                new Pizza[] {
                        new Pizza("Super Cheese", 1400),
                        new Pizza("All Shrooms", 900)
                },
                new CreditCardInformation("1449815592072338",
                                            "03/28",
                                                     "057")
        );
    }

    private static boolean orderEquals(Order order1, Order order2){
        return (order1.getOrderNo().equals(order2.getOrderNo()))
        && (order1.getOrderDate().equals(order2.getOrderDate()))
        &&(order1.getOrderStatus().equals(order2.getOrderStatus()))
        &&(order1.getOrderValidationCode().equals((order2.getOrderValidationCode())))
        && (order1.getPriceTotalInPence() == order2.getPriceTotalInPence())
        && (Arrays.equals(order1.getPizzasInOrder(), order2.getPizzasInOrder()))
        &&(order1.getCreditCardInformation().equals(order2.getCreditCardInformation()));
    }

    @Test
    void simpleValidOrder(){
        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.VALID_BUT_NOT_DELIVERED,OrderValidationCode.NO_ERROR);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void invalidCardNumberShort(){
        CreditCardInformation wrongCCInfo = new CreditCardInformation(
                "15592072338", "03/28", "057");
        baseValidOrder.setCreditCardInformation(wrongCCInfo);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.CARD_NUMBER_INVALID);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void invalidCardNumberLong(){
        CreditCardInformation wrongCCInfo = new CreditCardInformation(
                "155920782148012374012338", "03/28", "057");
        baseValidOrder.setCreditCardInformation(wrongCCInfo);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.CARD_NUMBER_INVALID);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void invalidExpiryDate(){
        CreditCardInformation wrongCCInfo = new CreditCardInformation(
                "1449815592072338", "03/20", "057");
        baseValidOrder.setCreditCardInformation(wrongCCInfo);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.EXPIRY_DATE_INVALID);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void validExpiryDateEdgeCase(){
        CreditCardInformation wrongCCInfo = new CreditCardInformation(
                "1449815592072338", "10/23", "057");
        baseValidOrder.setCreditCardInformation(wrongCCInfo);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.VALID_BUT_NOT_DELIVERED,OrderValidationCode.NO_ERROR);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void invalidCVVShort(){
        CreditCardInformation wrongCCInfo = new CreditCardInformation(
                "1449815592072338", "03/28", "1");
        baseValidOrder.setCreditCardInformation(wrongCCInfo);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.CVV_INVALID);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void invalidCVVLong(){
        CreditCardInformation wrongCCInfo = new CreditCardInformation(
                "1449815592072338", "03/28", "12134");
        baseValidOrder.setCreditCardInformation(wrongCCInfo);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.CVV_INVALID);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void incorrectTotal(){
        baseValidOrder.setPriceTotalInPence(2300);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.TOTAL_INCORRECT);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void undefinedPizza(){
        baseValidOrder.setPizzasInOrder(new Pizza[] {
                new Pizza("Super Cheese", 1400),
                new Pizza("All Shrooms", 900),
                new Pizza ("Imaginary pizza",2300)});
        baseValidOrder.setPriceTotalInPence(4700);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.PIZZA_NOT_DEFINED);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void multipleRestaurants(){
        baseValidOrder.setPizzasInOrder(new Pizza[] {
                new Pizza("Super Cheese", 1400),
                new Pizza("Proper Pizza", 1400)});
        baseValidOrder.setPriceTotalInPence(2900);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void overMaxPizzaCount(){
        baseValidOrder.setPizzasInOrder(new Pizza[] {
                new Pizza("Super Cheese", 1400),
                new Pizza("Super Cheese", 1400),
                new Pizza("Super Cheese", 1400),
                new Pizza("Super Cheese", 1400),
                new Pizza("All Shrooms", 900)});
        baseValidOrder.setPriceTotalInPence(6500);

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode. MAX_PIZZA_COUNT_EXCEEDED);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

    @Test
    void closedRestaurantOrder(){
        baseValidOrder.setOrderDate(LocalDate.of(2023,10,2));

        Order expectedValidatedOrder = orderValidator.cloneOrderAndAssignCodes(
                baseValidOrder,OrderStatus.INVALID,OrderValidationCode. RESTAURANT_CLOSED);
        Order validatedOrder = orderValidator.validateOrder(baseValidOrder,definedRestaurants);
        Assertions.assertTrue(orderEquals(expectedValidatedOrder,validatedOrder));
    }

}
