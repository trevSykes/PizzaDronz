# PizzaDronz - Informatics Large Practical Programming Coursework

**The Idea:**

PizzaDronz is a fictitious 24-hour pizza drone delivery service that supplies pizzas from local restaurants to Appleton Tower's roof (Appleton Tower being the principal location for Informatics teaching at the University of Edinburgh).

# The Task

I had to build a command line app that took in a date and REST server URL. The app would retrieve the given date's orders as well as supporting resources (the University Central Area, No-Fly Zones, Restaurants). The app validates the order details, routes the drone's flightpath to service the day's valid orders, serialises the validated orders and flightpath to JSON files, and generates a GeoJSon file of the flightpath.

# My experience

I thoroughly enjoyed the process we were guided along for this coursework. It was a valuable opportunity to apply the Software Engineering Practices I learned in Year 2. Initially, we had to formally identify the requirements from the given specification, construct a critical path for the project, and plan implementation strategies. This proved helpful when it came to implementation.


The Jackson API was invaluable when it came to deserialising REST server data and serialising the required output files. For the pathfiner, I utitlised an adjusted A-star search algorithm --- a key concept from my Year 2 course Reasoning and Agents.


My main takeaways from this was how to create a project that used intentional structure and design to meet requirements; as well as learning how to utilise Maven to build a functional Java application.

# Details

Grade: TBD

Programming Language: Java

IDE: Intellij IDEA

Build: Maven




