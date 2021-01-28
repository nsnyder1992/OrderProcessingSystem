# Order Processing System

The objective of this project is to describe how to implement a Order Processing System for a Robot Cafeteria. The Cafeteria will start off with 3 workers: a cashier, a chef, and a waiter. The purpose of this project is not to describe operation of the Robots, but how these robots will interact with the Ordering System.

This project will be using the Model View Controller (MVC) design pattern where the Models are where basic data is stored, and controllers are where that data will be manipulated and retrieved. Views will not be specified for this project as the backend abstraction and interactions of the order process system is the point of this project.

Also note that this project will eventually be created in Java, so some references to built-in Java classes and functions are mentioned

**Note:** This project needs to be able to scale as the popularity of the Cafeteria increase.

## Table of Contents

- Workflows
- Bottlenecks
- Models
- Controllers
- Middleware

## Workflows

This section will describe how each robot will interact with the processing system. All Robots will have access to view orders, and check the status of each order, including where it is in the process and how much time until done.

#### Cashier Work Flow with regard to Order Processing

This will allow a User of type "Cashier" to Create and Read an Order and submit it to the OrderQueue. If the order is still in the OrderQueue, the Cashier will be able to Update and Delete an Order. The Cashier Workflow is the following:

![Cashier Work Flow](/images/CashierChart.png)

1. Cashier creates a new instance of the Order class using createOrder() method of the OrderController class
2. uses the addItem(orderId) method to add an item to the Order
3. uses a removeItem(orderId) method to remove an item, if total quantity not < 0
4. uses an addCombo(orderId) method to add items within a combo
5. uses a removeCombo(orderId) method to remove an item, if total quantity not < 0
6. when customer is satisfied the Cashier can send order to order queue
7. If customer decides to change the order and it is still within the orderQueue then the Cashier can update
8. If customer decides to delete the order and it is still within the orderQueue then the Cashier can delete

#### Chef Work Flow with regard to Order Processing

User of type "Chef" will be able to move order from orderQueue to inProcessList and, when done, move it to completedQueue. The Chef will also have access to read RecipeSteps and RecipeIngredients. The Chef Workflow is the following:

![Chef Work Flow](/images/ChefChart.png)

1. If an order is present in the orderQueue, the Chef will take the order at the head of the Queue and place it into the inProcessList
2. The order start time is set
3. Then the Chef prepares order according to RecipeSteps for each item in order
4. Once Recipes for each item on the order is done, then the Chef can remove order from the inProcessList and move to completedQueue
5. The Chef will then take the next order from the head of the Queue and start the process again

#### Waiter Work Flow with regard to Order Processing

A "Waiter" User will be able to move a order from the completedQueue to the deliveredList and if the customer is satisfied delete the order from the list. If the customer is dissatisfied the "Waiter" will put a new order with items with quality issue back to the front of the orderQueue. The Waiter Workflow is the following:

![Waiter Work Flow](/images/WaiterChart1.png)

1. The Waiter waits for an order to appear in the orderQueue.
2. Once there, the waiter will take the first order in the Queue and deliver it to the customer, and place the order in the deliveredList
3. After some amount of time, the waiter will need to check with the customer and see if everything is ok
4. If ok, the Waiter can delete the order from the deliveredList
5. If not ok, the Waiter will need to make a new order made up of the items with quality issues and place it at the beginning of the orderQueue

## Bottlenecks:

In this section the implementation of the above workflows are described and potential bottlenecks are found and some solutions are presented.

    orderQueue = [10, 11, 12, 13] //Java LinkedList of order ids
    inProcessList = [6, 8, 9]     //Java ArrayList  of order ids
    completedQueue = [4, 3, 7]    //Java LinkedList  of order ids
    deliveredList = [2 , 1, 5]    //Java ArrayList  of order ids

Why use a queue for orders placed and completed? Well a queue is best when you want to process/cook the first order before the second, third or last (First In First Out or FIFO). Though in the pseudo-code below these are implement with the intent to use the Java LinkedLink class that have the properties of a doubly linked list, a queue, and a dequeue, making it more powerful than a regular queue data structure.

Some potential bottlenecks are finding where an order is within which queue or list. The Naive approach would be to loop through each trying to find the order, but this doesn't scale very well. One solution would be to add a Hash table, statusMap, with keys being the id of the order, and the value being an array of integers. The value being defined as [queueNum, index], where queueNum identifies what queue or list the order is in (ie "1" = orderQueue, "2" = inProcessList, "3" = completedQueue, "4" = deliveredList) and index being where in the queue or list the order is. So for the example above the statusMap would be:

    statusMap = { 1: [4, 1], 2: [4, 0], 3: [3, 1], 4: [3, 0], 5: [4, 2], 6: [2, 0], 7: [3, 2], 8: [2, 1], 9: [2, 2], 10: [1, 0], 11: [1, 1], 12: [1, 2], 13: [1, 3]}

Why use a array for orders being processed or delivered? Unlike the queues described above, these orders maybe needed to be removed out of order of when they were put in. For example, say an order with a total prep time for order 6 is 10min and the total prep time of order 7 is 2min. The order 7 will be done before the order 6 and will need to be moved to the completedQueue. And while queues are great for FIFO, they aren't great if we need to access a value, especially if we already know the index. This is due to the fact that queues are usually built on top of the linked list paradigm, where to get to a certain value it must traverse the list til that value is found.

**Bottleneck fixes below are not implemented in the pseudo-code below**

Using this {orderId: [queueNum, index]} data structure allows us to cut out finding where a order is in a array fast, but anytime we update the queue or list we will need to update the index parameter, having O(N) time complexity, which is our next bottleneck. Plus, the index doesn't really have any effect on the queue since we have to traverse the queue anyway, so in the pseudo-code below it was decided to not update the index for orders in queues.

To fix this bottleneck, a binary search algorithm could be used to find the orderId in the inProcessList and the deliveredList. This would get rid of the need for the index in the statusMap leaving {orderId: queueNum}, and would only have a O(log(N)) time complexity. This means we will need to sort the ArrayLists as we insert new elements but this is much easier to do as the program will insert one at a time.

    statusMap = { 1: 4, 2: 4, 3: 3, 4: 3, 5: 4, 6: 2, 7: 3, 8: 2, 9: 2, 10: 1, 11: 1, 12: 1, 13: 1} // much easier to look at!

The Next bottleneck is when deleting an order from the orderQueue, as said a queue needs to traverse the entire length until the order is found, then remove it. There maybe a solution to this one, but it was decided to not dive to deeply into this one as submission date was approaching. But one thought would be to start a thread to delete the order as to not hold up the project, but LinkedList, ArrayList, and HashMap are _not_ thread safe. So, maybe using HashTable instead of HashMap and Collections.synchronizedList() to synchronize the List objects.

To be sure there are more bottlenecks to be found, especially when the code is actually being implemented and unit tested, but hopefully this demonstrates what I would do to solve certain bottlenecks in real world problems!

## Models:

In this section the most important models needed for this project will be described, though more tables will be needed to fulfill the full application. These models are how the application will access data from a database.

### Order (Database)

This model represents how each order will be stored in the database

    Attributes:

        id::Integer
        name::String                          // name on order
        dateCreated::Date                     // dateCreated
        cashierId::Integer                    // id of cashier involved
        chefId::Integer                       // id of chef involved
        waiterId::Integer                     // id of waiter involved
        timeStarted::Date                     // Date and time started
        totalPrepTime::Integer                // in seconds
        timeCompleted::Date                   // Date and time completed
        canceled::boolean                     // true if canceled
        qualityIssue::boolean                 // true if sent back by customer

### OrderItems (Database)

This table is a one-to-many relationship between an order and it's items

    Attributes:

        orderId::Integer                      // orderId
        foodItemId::Integer                   // from foodItems table
        isComplete::boolean                   // true if complete

### FoodItem (Database)

    Attributes:

        id::Integer
        name::String            //"Hamburger", "Chicken Sandwich", "Fries" etc
        prepTime::Integer       //sum of all the non-concurrent steps or the max concurrent step depending on which is longer in the recipe
        menuTypeId::Integer     //from MenuType Model ("Dinner", "Dessert", "Sides", etc)

### Combos (Database)

    Attributes:

        id::Integer
        name::String

### ComboFoods (Database)

One to Many Relationship between Combos and its associated FoodItems

    Attributes:

        comboId::Integer
        foodItemId::Integer

### RecipeSteps (Database)

This will be a One-to-many type relationship model where there will be 1 foodItemId to many steps including prepare, utilities, and ingredients

    Attributes:

        foodItemId::Integer           //from FoodItem model
        stepNum::Integer              //which step we are on
        prepareId::Integer            //from Prepare model (ie "Cook", "Chop", etc)
        utilityId::Integer            //from Utility model (ie "Oven", "Knife", etc)
        ingredientId::Integer         // from Ingredients model (ie "salt", "hamburger patty", etc)
        qty::Integer
        units::String
        mixWithNext::boolean          //if true mix with the next in line, can sequence this to mix as many ingredients at one time
        timeToPrepare::Integer        //seconds
        timeIntervalsToFlip::Integer  //when to interrupt to flip burgers, steaks,etc
        concurrent::boolean           //if other processes can be done concurrently, then set to true. Like waiting on brownies in the oven

Might need to add more things above to account for more actions, but the general idea is to save the RecipeSteps in the database to help calculate optimum amount of time and space within the Cafeteria Kitchen.

### User (Database)

These will be the lowest level of the classes that make up the Recipes and will be mainly used to keep inventory of all the

    Attributes:

        id::Integer
        name::String
        password::hashed<String>
        isRobot::boolean            //if robot true
        securityLevelId::Integer    //from SecurityLevel model

### SecurityLevel (Database)

SecurityLevels will allow the program to determine who is allowed to do what

    Attributes:

        id::Integer
        name::String //"Admin", "Cashier", "Chefs", "Waiters"

## Controllers

This section will describes controllers and their methods

### OrderController

    Instance Variables:

        // All the following can be shared across  instances of the class, so many OrderControllers can be running at one time at scale

        private static orderQueue::LinkedList<Integer>            // this will keep the orders in a FIFO
        private static inProcessList::ArrayList<Integer>          // this will keep the orders that are being processed by the Chefs but might not be FIFO depending on prepare time so this is just a list
        private static completedQueue::LinkedList<Integer>        // this will keep the orders that are Completed waiting for the waiter to deliver
        private static deliveredList::ArrayList<Integer>          // this will keep the orders that are delivered until waiter sees customer leave, in case of customer rejection
        private static statusMap::HashMap<Integer>                // key: orderId, value: queue
                                                                  // queueNum => "1" = orderQueue | "2" = inProcessList | "3" = completedQueue | "4" = deliveredList

**Note:** The above instance variables add Space Complexity, but the idea is to be able to easily and quickly find where a given order is in the process

**Note:** Also the above instance variables of type LinkedList, ArrayList and HashMap are _not_ thread-safe, so if a thread is needed, we will need to find away to make them synchronous. HashTable could be used for HashMap. But, LinkedList and ArrayList have nice built-in methods for this application, so maybe using Collections.synchronizedList() will do the trick.

In the below methods not all error/logic checks are shown as this is just an overview of how each will work

    Methods :

        createOrder(boolean backOfQueue) {
          // order = create a Order Model in the database
          // order.cashierId = user.id

          // index = orderQueue.length if backOfQueue else 0
          // add order.id to orderQueue[index]

          // add key: orderId, value: 1 to  statusMap

          // return order.id
        }

        getOrder(int orderId) {
          // return order from database where orderId == orderId
        }

        getName(int orderId) {
          // get the name on the order
        }

        setName(int orderId, String name) {
          // set the name on the order
        }

        setCashierId(int orderId) {
          // order.cashierId = user.id
        }

        setChefId(int orderId) {
          // order.chefId = user.id
        }

        setWaiterId(int orderId) {
          // order.waiterId = user.id
        }

        /********************************************************************
          Getters and setters for all other simple attributes would go here
        ********************************************************************/

        addPrepTime(int orderId, int itemId) {
          // order.totalPrepTime += item.prepTime
        }

        removePrepTime(int orderId, int itemId) {
          // order.totalPrepTime -= item.prepTime
        }

        addItem(int orderId, int qty, int itemId){
          if order in orderQueue {
            // update/add qty of item to the order
            addPrepTime(orderId, itemId)
          }
        }

        removeItem(int orderId, int qty, int itemId){
          if order in orderQueue {
            // update qty of item on the order
            removePrepTime(orderId, itemId)
          }
        }

        addCombo(int orderId, int qty, int comboId){
          if order in orderQueue {
            foodItems = get all foodItems from FoodItems model where comboId == comboId
            for item in foodItems {
              // update/add qty of item on the order
              addPrepTime(orderId, itemId)
            }
          }
        }

        removeCombo(int orderId, int qty, int comboId){
          if order in orderQueue {
            foodItems = get all foodItems from FoodItems model where comboId == comboId
            for item in foodItems {
              // update qty of item on the order
              removePrepTime(orderId, itemId)
            }
          }
        }

        getOrderItems(int orderId) {
          // all items from orderItems where orderId == orderId
        }

        getOrderStatus(int orderId) {
          // get status from statusMap where key == orderId
        }

        getEstTime(int orderId, int[3] status) {
          //This is the naive approach. A more accurate prepTime can be achieve here because it depends on the number of robot chefs, but this shows the basic approach

          if status[0] > 2 return 0 //no time left "complete"

          if status[0] == 1 {
            // queueTime = traverse orderQueue summing orders.totalPrepTime until orderQueue[index] == orderId + totalPrepTime of that order
            // minProcessTime = getMinProcessTime()
            // return queueTime + minProcessTime
          }

          if status[0] == 2 {
            // return sum of item.prepTime for all items *not* complete in the orderItems table
          }

        }

        getMinProcessTime() {
          // find order with min time left and return the time
        }

        processOrder() {
          // remove first order from orderQueue
          // update order.chefId = user.id
          // add order.id to inProcessList using merge sort principles
          // update statusMap key: order.id, value: 2
        }

        itemComplete(int orderId, int itemId) {
          // mark item as complete in orderItems in the database
        }

        completeOrder(int orderId) {
          // find orderId in inProcessList using binary tree algorithm and remove
          // add to end of completedQueue
          // update key: order.id, value: 3
        }

        deliveredOrder() {
          // remove order first order from completedQueue
          // add order.id to deliveredList using merge sort principles
          // add key: order.id, value: 4
        }

        rejectOrder(int orderId) {
          // set order.quality = true in database
          // deleteOrder(orderId, false)
        }

        deleteOrder(int orderId, boolean wasCanceled) {
          // if wasCanceled set order.canceled = true in database
          // find which queue/list and index of order using statusMap
          // update indices of statusMap of corresponding list if in inProcessList or deliveredList
          // remove order from status map where key == order.id
        }

### Other Model Controllers

All Models in the model section above would have a corresponding controller allowing for manipulating, retrieving and processing of data within the database. Since this exercise is more focused on how to process and track orders, I have decided to neglect showing any pseudo-code for them.

### Middleware

This piece of the application will mainly handle operations that need to be processed before anything else in the program can be used, such as authorization

#### Authorization

This part of the program would check the SecurityLevels of each user and only allow them to do certain operations. For example a Cashier would only be allowed to create/read/update/delete orders that are in the orderQueue. Chefs would be the only ones allowed to move orders from the orderQueue to the inProcessList and then later to the completedQueue, etc.
