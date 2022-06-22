# Order Delivery Simulator

## Build and Run

Unzip the file and in the root directory run the following command:
`./mvnw package`
To run this you need to have JDK 8 or above installed and the *JAVA_HOME* environment variable properly set to you JDK install directory. Recommend using [SDKMAN](https://sdkman.io/) which is a great tool for managing multiple JDK versions.
This command will run all the unit tests and build an executable jar file named **delivery-service-0.1.jar** under the **target** folder. I've also included the target folder in the zip file in case you have problems building with maven. You can also import the project into your favorite IDE and run from there.

Once you have the jar file you can run the simulator with the following command (also from the root directory):
`java -jar target/delivery-service-0.1.jar`
You will see the usage after running it since you didn't provide the required arguments:
```
usage: Delivery Service [-h] -s {MATCHED,FIFO} [-r RATE]

Simulate order deliveries and print statistics

named arguments:
  -h, --help             show this help message and exit
  -s {MATCHED,FIFO}, --strategy {MATCHED,FIFO}
                         Dispatching strategy to  use.  Possible values are
                         MATCHED(a courier  may  only  pick  up  a specific
                         order)  and  FIFO(a  courier  picks  up  the  next
                         available order upon arrival)
  -r RATE, --rate RATE   Number of orders  dispatched  per second (default:
                         2)
```
Then, to run it successfully, you need to provide the strategy like this:
`java -jar target/delivery-service-0.1.jar -s FIFO`
This will run the simulator with the First-in-first-out strategy at the default sending rate 2 orders/second.
The last few logs will look like this if your running is successful:
```
17:32:47.077 INFO  - Order Corn Dog waited 2892 milliseconds to be picked up by courier 131
17:32:47.077 INFO  - Courier 131 waited 0 milliseconds to pickup order Corn Dog
17:32:47.078 INFO  - Average food wait time is 794 milliseconds between order ready and pickup
17:32:47.078 INFO  - Average courier wait time is 436 milliseconds between arrival and order pickup
```
You can also run it with a different sending rate by adding the -r option, this time we try the Matched strategy:
`java -jar target/delivery-service-0.1.jar -s MATCHED -r 10`

## Design Thoughts

The problem is basically about having two list of items, orders and couriers, and implementing different strategies to coordinate the interactions between them.
Upon receiving an order, a courier is dispatched immediately.  Then the order is being prepared, and the courier is departing for the order. An order might be ready before a courier has arrived, and a courier might have arrived before an order is ready. So they're constantly waiting for each other, and that's where the coordinator comes in. We have two simple strategies here:

- Matched: A courier is assigned a specific order and may only pick up that order. In this case, there's a one-one mapping between couriers and orders and a courier only needs to wait for one specific order. When a courier arrives, it checks whether the order is ready. If so, it picks up the order immediately, otherwise it just stands there and waiting. And whenever an order is ready, it notifies the corresponding courier. This is a typical use case for the wait-and-notify mechanism in Java and most other common languages that support multithreading.
- First-in-first-out: A courier picks up whatever is available upon arrival. When I saw FIFO, the first thing came to my mind was the producer-consumer pattern. There's a shared queue for producers to put objects and consumers to take objects. In this case orders and couriers can be both producers and consumers (there're no differences between an order consuming a courier and a courier consuming an order). This gives me the insight for a two-queue solution instead of the typical one-queue producer-consumer pattern. When an order is ready, it checks whether there're available couriers in the courier queue and gives the order to the first courier if so. Otherwise it just puts the order in the order queue. When a courier arrives, it does the exactly symmetric operation. The problem only requires couriers to be FIFO and if multiple orders are ready an arbitrary one can be taken. This way both orders and couriers are in strict FIFO order since they both have their own queues. And there're no blockings because both sides have an unbounded buffer.