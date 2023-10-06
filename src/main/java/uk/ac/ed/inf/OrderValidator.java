package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;


import java.util.*;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderValidator implements OrderValidation {

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        //Return order if already validated
        if (orderToValidate.getOrderValidationCode() != OrderValidationCode.UNDEFINED){
            return orderToValidate;
        }
        Pizza[] pizzasInOrder = orderToValidate.getPizzasInOrder();
        CreditCardInformation creditCardInformation = orderToValidate.getCreditCardInformation();
        LocalDate orderDate = orderToValidate.getOrderDate();
        int priceTotalInPence = orderToValidate.getPriceTotalInPence();
        OrderValidationCode validationCode;
        //Assume every order passed is initially invalid
        OrderStatus statusCode = OrderStatus.INVALID;

        if (invalidNumberOfPizzas(pizzasInOrder)){
            validationCode = OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        }
        else if (invalidCCNumber(creditCardInformation.getCreditCardNumber())){
            validationCode = OrderValidationCode.CARD_NUMBER_INVALID;
        }
        else if (invalidCCExpiry(creditCardInformation.getCreditCardExpiry(),orderDate )){
            validationCode = OrderValidationCode.EXPIRY_DATE_INVALID;
        }
        else if (invalidCVV(creditCardInformation.getCvv())){
            validationCode = OrderValidationCode.CVV_INVALID;
        }
        else if (totalIsIncorrect(priceTotalInPence,pizzasInOrder)){
            validationCode = OrderValidationCode.TOTAL_INCORRECT;
        }
        else if (orderedPizzasDontExist(pizzasInOrder,definedRestaurants)){
            validationCode = OrderValidationCode.PIZZA_NOT_DEFINED;
        } else{
            //Looks for a restaurant that serves all the pizzas specified in order
            Restaurant validRestaurant = findValidRestaurant(pizzasInOrder,definedRestaurants);
            if (validRestaurant == null){
                validationCode = OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
            }
            else if (restaurantIsClosed(orderDate,validRestaurant)){
                validationCode = OrderValidationCode.RESTAURANT_CLOSED;
            } else {
                validationCode = OrderValidationCode.NO_ERROR;
                statusCode = OrderStatus.VALID_BUT_NOT_DELIVERED;
            }
        }
        Order validatedOrder = cloneOrderAndAssignCodes(orderToValidate, statusCode, validationCode);
        return validatedOrder;
    }

    public Order cloneOrderAndAssignCodes(Order orderToValidate, OrderStatus statusCode, OrderValidationCode validationCode) {
        Order validatedOrder = new Order(
                orderToValidate.getOrderNo(),
                orderToValidate.getOrderDate(),
                orderToValidate.getPriceTotalInPence(),
                orderToValidate.getPizzasInOrder(),
                orderToValidate.getCreditCardInformation()
        );
        validatedOrder.setOrderStatus(statusCode);
        validatedOrder.setOrderValidationCode(validationCode);
        return validatedOrder;
    }

    //Searches for a restaurant that makes all the pizzas in the order
    private Restaurant findValidRestaurant(Pizza[] pizzas, Restaurant[] restaurants){
        Set<Pizza> pizzaSet = new HashSet<>(Arrays.asList(pizzas));
        for (Restaurant restaurant : restaurants){
            Set<Pizza> menu = new HashSet<>(Arrays.asList(restaurant.menu()));
            if (menu.containsAll(pizzaSet)){
                return restaurant;
            }
        }
        return null;
    }

    //Checks if the pizzas in the order exist on the menus of any restaurant
    private boolean orderedPizzasDontExist(Pizza[] orderedPizzas, Restaurant[] restaurants){
        Set<Pizza> allPizzas = new HashSet<>();
        for(Restaurant restaurant : restaurants) {
            List<Pizza> restaurantPizzas = Arrays.asList(restaurant.menu());
            allPizzas.addAll(restaurantPizzas);
        }
        for(Pizza orderedPizza : orderedPizzas){
            if (!allPizzas.contains(orderedPizza)) {return true;}
        }
        return false;
    }

    //Checks if the total in the order matches the true total cost of pizzas and delivery charge
    private boolean totalIsIncorrect(int priceTotalInPence,Pizza[] pizzas){
        int trueTotal = SystemConstants.ORDER_CHARGE_IN_PENCE;
        for(Pizza pizza : pizzas){
            trueTotal += pizza.priceInPence();
        }
        return trueTotal != priceTotalInPence;
    }
    //Returns true if day of week of the order date is on a day the restaurant is closed
    private boolean restaurantIsClosed(LocalDate orderDate, Restaurant restaurant){
        return Arrays.stream(restaurant.openingDays()).noneMatch(orderDate.getDayOfWeek()::equals);
    }

    // Returns true if the number of pizzas exceeds the max carrying capacity of drone
    private boolean invalidNumberOfPizzas(Pizza[] pizzasInOrder){
        return pizzasInOrder.length > 4;
    }

    // Returns true if the credit card expired before the orderdate and current date
    private boolean invalidCCExpiry(String creditCardExpiry, LocalDate orderDate){
        if (!creditCardExpiry.contains("/")){
            return true;
        }
        String[] expiryParts = creditCardExpiry.split("/");
        int month = Integer.parseInt(expiryParts[0]);
        int year = Integer.parseInt((expiryParts[1]));

       LocalDate expiryDate = LocalDate.of(2000+year,month,1);
       //Credit cards are valid until the first day of the next month
       expiryDate = expiryDate.plusMonths(1);

       return ((expiryDate.isBefore(orderDate)) || (expiryDate.isBefore(LocalDate.now())));

    }
    //Checks if Credit Card Number is not a string of 16 digits
    private boolean invalidCCNumber(String creditCardNumber){
        String ccNumPattern = "\\d{16}";
        Pattern reg = Pattern.compile(ccNumPattern);
        Matcher matcher = reg.matcher(creditCardNumber);
        return !matcher.matches();
    }
    //Checks if CVV number is not a string of 16 digits
    private boolean invalidCVV(String cvv){
        String cvvPattern = "\\d{3}";
        Pattern reg = Pattern.compile(cvvPattern);
        Matcher matcher = reg.matcher(cvv);
        return !matcher.matches();
    }
}
