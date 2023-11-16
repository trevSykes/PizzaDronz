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
import java.util.stream.Collectors;

public class OrderValidator implements OrderValidation {

    /**
     * Validates and order by checking all fields
     * @param orderToValidate Order object
     * @param definedRestaurants Array of Restaurant objects
     * @return Order object with updated validation and status codes
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        Pizza[] pizzasInOrder = orderToValidate.getPizzasInOrder();
        CreditCardInformation creditCardInformation = orderToValidate.getCreditCardInformation();
        LocalDate orderDate = orderToValidate.getOrderDate();
        int priceTotalInPence = orderToValidate.getPriceTotalInPence();

        //Check for null data input
        try {
            checkForNullData(pizzasInOrder, creditCardInformation, orderDate, definedRestaurants);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Return order if already validated
        if (orderToValidate.getOrderValidationCode() != OrderValidationCode.UNDEFINED){
            return orderToValidate;
        }
        //Assume every order passed is initially invalid
        OrderStatus statusCode = OrderStatus.INVALID;
        OrderValidationCode validationCode;

        Map<String, Integer> allDefinedPizzas = getAllDefinedPizzas(definedRestaurants);

        if (orderedPizzasDontExist(pizzasInOrder, allDefinedPizzas)){
            validationCode = OrderValidationCode.PIZZA_NOT_DEFINED;
        }
        else if (totalIsIncorrect(priceTotalInPence, pizzasInOrder, allDefinedPizzas)) {
            validationCode = OrderValidationCode.TOTAL_INCORRECT;
        }
        else if (invalidNumberOfPizzas(pizzasInOrder)){
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
        return cloneOrderAndAssignCodes(orderToValidate, statusCode, validationCode);
    }

    /**
     * Used to return a cloned order after validation
     * @param orderToValidate Order object to be cloned
     * @param statusCode OrderStatus enum to be assigned to cloned Order
     * @param validationCode OrderValidationCode enum to be assigned to cloned Order
     * @return Clone of orderToValidate with statusCode and validationCode
     * set to their relevant field
     */
    public Order cloneOrderAndAssignCodes(Order orderToValidate, OrderStatus statusCode,
                                          OrderValidationCode validationCode) {
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

    /**
     * Helper method to validate whether order data exists
     * @param pizzasInOrder Array of Pizza objects from orderToValidate
     * @param creditCardInformation CreditCardInformation object from orderToValidate
     * @param orderDate LocalDate of the orderDate from orderToValidate
     * @param definedRestaurants Array of Restaurant objects from orderToValidate
     * @throws Exception which corresponds to which field is identified null
     */
    private void checkForNullData(Pizza[] pizzasInOrder,
                                  CreditCardInformation creditCardInformation, LocalDate orderDate,
                                  Restaurant[] definedRestaurants) throws Exception {
        if (pizzasInOrder == null) {
            throw new Exception("Pizzas in order is null. Cannot validate order. Check inputs.");
        }
        if (creditCardInformation == null) {
            throw new Exception("Credit card information is null. Cannot validate order. Check inputs.");
        }
        if (orderDate == null) {
            throw new Exception("Order date is null. Cannot validate order. Check inputs.");
        }
        if (definedRestaurants == null) {
            throw new Exception("Defined restaurants are null. Cannot validate order. Check inputs.");
        }
    }

    /**
     * Searches for a restaurant that makes all the pizzas in the order
     * @param pizzas Array of Pizza objects in the order
     * @param restaurants Array of restaurants that are defined in the system
     * @return A Restaurant object that makes all pizzas in the order or null if it does not exist
     */
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

    /**
     * Checks if the pizzas in the order exist on the menus of any restaurant
     * @param orderedPizzas Array of pizza objects in the order
     * @param allPizzas Map of all pizza names found in restaurant menus and prices
     * @return True if a Pizza in orderedPizzas does not have a name that can be found in allPizzas
     */
    private boolean orderedPizzasDontExist(Pizza[] orderedPizzas, Map<String,Integer> allPizzas){
        Set<String> allPizzaNames = allPizzas.keySet();
        return Arrays.stream(orderedPizzas).anyMatch(orderedPizza -> !allPizzaNames.contains(orderedPizza.name()));
    }

    /**
     * Compiles all the pizzas available in the system
     * @param restaurants Array of defined restaurants in the system
     * @return A Map containing all pizzas made by all restaurants. With pizza names as keys
     *         and their price as values
     */
    private Map<String,Integer> getAllDefinedPizzas(Restaurant[] restaurants){
        Set<Pizza> allPizzas = new HashSet<>();
        for(Restaurant restaurant : restaurants) {
            List<Pizza> restaurantPizzas = Arrays.asList(restaurant.menu());
            allPizzas.addAll(restaurantPizzas);
        }
        return allPizzas.stream().collect(Collectors.toMap(Pizza::name,Pizza::priceInPence));
    }


    /**
     * Checks if the total in the order matches the true total cost of pizzas and delivery charge
     * @param priceTotalInPence int of the total price listed by an order
     * @param pizzasInOrder Array of Pizza objects in the order
     * @param allPizzas Map of all pizza names and their prices
     * @return True if the total found in the order is not equal to the true total
     *         of pizza prices and the devliery charge
     */
    private boolean totalIsIncorrect(int priceTotalInPence, Pizza[] pizzasInOrder,
                                     Map<String, Integer> allPizzas){
        int trueTotal = SystemConstants.ORDER_CHARGE_IN_PENCE;
        for (Pizza pizza : pizzasInOrder){
            trueTotal += allPizzas.get(pizza.name());
        }
        return priceTotalInPence != trueTotal;
    }
    /**
     * Checks if the given restaurant is closed on the day of the order
     * @param orderDate LocalDate object of the order date
     * @param restaurant Restaurant object of a restaurant that can make all the pizzas listed
     *                   in the order
     * @return True if the restaurant is closed on the day of the week of the order date
     */
    private boolean restaurantIsClosed(LocalDate orderDate, Restaurant restaurant){
        return Arrays.stream(restaurant.openingDays()).noneMatch(orderDate.getDayOfWeek()::equals);
    }


    /**
     * Checks whether an invalid number of pizzas is ordered
     * @param pizzasInOrder Array of Pizza objects attached to the order
     * @return True if too many (>4) pizzas are ordered
     */
    private boolean invalidNumberOfPizzas(Pizza[] pizzasInOrder){
        return pizzasInOrder.length > 4;
    }

    /**
     * Checks if the credit card expiry is invalid
     * @param creditCardExpiry String of credit card expiry date
     * @param orderDate LocalDate object of the order date
     * @return True if creditCardExpiry represents a date after the orderDate
     */
    private boolean invalidCCExpiry(String creditCardExpiry, LocalDate orderDate){
        //First check for format of MM/YY and month section cannot be >12
        String expiryPattern = "^(0[1-9]|1[0-2])/\\d{2}$";
        Pattern reg = Pattern.compile(expiryPattern);
        Matcher matcher = reg.matcher(creditCardExpiry);
        if (!matcher.matches()){
            return true;
        }
        String[] expiryParts = creditCardExpiry.split("/");
        int month = Integer.parseInt(expiryParts[0]);
        int year = Integer.parseInt((expiryParts[1]));

        //Credit cards are valid until the first day of the next month
        LocalDate expiryDate = LocalDate.of(2000+year,month,1).plusMonths(1);
        return ((expiryDate.isBefore(orderDate)) || (expiryDate.isBefore(LocalDate.now())));

    }

    /**
     * Checks if a credit card number is not valid
     * @param creditCardNumber String of a credit card number
     * @return True if creditCardNumber is not valid (not a string of 16 digits)
     */
    private boolean invalidCCNumber(String creditCardNumber){
        String ccNumPattern = "\\d{16}";
        Pattern reg = Pattern.compile(ccNumPattern);
        Matcher matcher = reg.matcher(creditCardNumber);
        return !matcher.matches();
    }

    //Checks if CVV number is not a string of 16 digits
    /**
     * Checks if a CVV is not valid
     * @param cvv String of a CVV number
     * @return True if CVV is not valid (not a string of 3 digits)
     */
    private boolean invalidCVV(String cvv){
        String cvvPattern = "\\d{3}";
        Pattern reg = Pattern.compile(cvvPattern);
        Matcher matcher = reg.matcher(cvv);
        return !matcher.matches();
    }
}