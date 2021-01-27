# Order Processing System

The objective of this project is to describe how to implement a Order Processing System for a Robot Cafeteria. The Cafeteria will start off with 3 workers: a cashier, a chef, and a waiter. The purpose of this project is not to describe operation of the Robots, but how these robots will interact with the Ordering System. This project needs to be able to scale as the popularity of the Cafeteria increase.

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
7. If customer decided to change the order and it is still within the orderQueue then the Cashier can update
8. If customer decided to delete the order and it is still within the orderQueue then the Cashier can update

#### Chef Work Flow with regard to Order Processing

User of type "Chef" will be able to move order from orderQueue to inProcessList and, when done, move it to completedQueue. The Chef will also have access to read RecipeSteps and RecipeIngredients. The Chef Workflow is the following:

![Chef Work Flow](/images/ChefChart.png)

1. The Chef will take the next order at the head of the Queue and place it into the inProcessList
2. Then the Chef prepare order according to RecipeSteps and set the start time of the order
3. Once Recipe is done the Chef will remove that order from the

#### Waiter Work Flow with regard to Order Processing

This will allow a User of type "Cashier" to Create and Read an Order and submit it to the OrderQueue. If the order is still in the OrderQueue, and not in the inProcessQueue or completedQueue, the Cashier will be able to Update and Delete an Order. The Cashier Workflow is the following:

![Waiter Work Flow](/images/WaiterChart1.png)

1. Cashier creates a new instance of the Order class using createOrder()
2. uses an Order.addItem() method to add an item to the Order
3. uses a Order.removeItem() method to remove an item, if total quantity not < 0
4. uses an Order.addCombo() method to add items within a combo
5. uses a Order.removeCombo() method to remove an item, if total quantity not < 0
6. when customer is satisfied the Cashier can send order to order queue
7. If customer decided to change the order and it is still within the orderQueue then the Cashier can update
8. If customer decided to delete the order and it is still within the orderQueue then the Cashier can update

## Controllers

This section will describes controllers and their methods

### OrderController

Attributes:

    // All the following can be accessed by all class instances, so many OrderControllers can be running at one time at scale
    private static idIndexer::Integer       // this will increase each time a Order is created, reset by crontab method after midnight
    private static orderQueue::LinkedList<Order>          // this will keep the orders in a FIFO
    private static inProcessList::ArrayList<Order>        // this will keep the orders that are being processed by the Chefs but might not be FIFO depending on prepare time so this is just a list
    private static completedQueue::LinkedList<Order>      // this will keep the orders that are Completed waiting for the waiter to deliver
    private static deliveredList::ArrayList<Order>        // this will keep the orders that are delivered until waiter sees customer leave, in case of customer rejection
    private static statusMap::HashMap<Integer, Integer[2]>  //key: orderId, value: [whichQueue, indexInList]
                                             //whichQueue = "1" = orderQueue | "2" = inProcessList | "3" = completedQueue | "4" = deliveredList

**Note:** The above instance variable statusMap adds Space Complexity, but will allow for easy finding where a given order is in the process
**Note:** Also the above instance variables of type LinkedList, ArrayList and HashMap are not thread-safe, so if a thread is needed, will need to find away to make them synchronous. HashTable could be used for HashMap. But, LinkedList and ArrayList has the options needed for this application, so maybe using Collections.synchronizedList() will do the trick.

Methods:

    createOrder() {
        idIndexer++;
        order = new Order(idIndexer);
        sendOrder(order);
        return order.id;
     }

    setName(Order order, String name) {
        this.name = name;
    }

    getName(Order order, String name) {
       return this.name;
    }

    addItem(Order order, int qty, String item){
      // All queries done using model controllers
      if item in database and qty not null {
        // update/add key: item, value: qty  += qty in order HashMap
      }
    }

    removeItem(Order order, int qty, String item){
        // All queries done using model controllers
        if item in database and qty not null {
          // update/add key: item, value: qty -= qty in order HashMap if qty - qty >= 0 else 0
        }
    }

    addCombo(Order order, int qty, String combo){
        // All queries done using model controllers
        queryId = query get Combo id from Combo model where name == combo

        if queryId not null and qty not null {
          foodItems = get all foodItems from FoodItems model where comboId == queryId
          for item in foodItems {
            // update/add key: item, value: qty  += qty_from_query * qty in order HashMap
          }
       }
     }

    removeCombo(Order order, int qty, String item){
      // All queries done using model controllers
      queryId = query get Combo id from Combo model where name == combo
       if queryId not null and qty not null {
          foodItems = get all foodItems from FoodItems model where comboId == queryId
          for item in foodItems {
            // update/add key: item, value: qty  -= qty_from_query * qty in order HashMap if qty - qty_from_query * qty >= 0 else 0
        }
      }
    }

    getOrderItems(int orderId) {
      // get status from statusMap where key == order.id
      return order
    }

    getOrderStatus(Order orderId) {
      // get status from statusMap where key == order.id
      if status[0] == 3 return "completed"
      estTime = getEstTime(orderId, status)
      return (estTime * 60) + "min let"
    }

    getEstTime(Order orderId, int[] status) {
      //This is the naive approach. A more accurate prepTime can be achieve here because of the concurrent steps, but this show the basic approach
      if status[0] == 1 {
        // traverse orderQueue summing orders.totalPrepTime until order.id == orderId  and time of that order
        getTotalProcessTime()

      }
    }

    getTotalProcessTime() {
      // loop through inProcessList and sum the
    }

    setPrepTime(Order order) {
      //This is the naive approach. A more accurate prepTime can be achieve here because of the concurrent steps, but this show the basic approach
      prepTime = 0
      for item in order.order.keys() {
        qty = order.order[item];
        order.prepTime += qty * Sum(RecipeSteps.prepTime) on RecipeSteps where recipeId == item.recipeId
      }
    }

    sendOrder(Order order) {
      // setPrepTime(order)
      // add order to orderQueue
      // add key: order.id, value: [1, orderQueue.length - 1]
    }

    processOrder() {
      // add to inProcessQueue
      // remove first order from orderQueue
      // add key: order.id, value: [2, inProcessQueue.length - 1]
      // return order
    }

    completeOrder(Order order) {
      // add to completedQueue
      // remove order from inProcessQueue
      // add key: order.id, value: [3, completedQueue.length - 1]
    }

    deliveredOrder() {
      // add to deliveredList
      // remove order from completeQueue
      // add key: order.id, value: [4, deliveredList.length - 1]
      // return order
    }

    deleteOrder(Order order) {
      // remove order from deliveredList
      // remove order from status map where key == order.id
     }

    updateOrder(Order newOrder) {
      // get status of order in statusMap where key == newOrder.id
      // if status == 1 then update order = newOrder
     }

    resetIndexer() {
      idIndexer = 0;
     }

### OrderQueueController

### OrderListController

### Other Model Controllers

All Models in the model section below would have a corresponding controller allowing for Creating, Reading, Updating, and Deleting.

### Middleware

#### Authorization

This part of the program would check the SecurityLevels of each user and only allow them to do certain operations. For example a Cashiers would only be allowed to create/read/update/delete orders that are in the orderQueue. Chefs would be the only ones allowed to move orders from the order queue to the inProcessQueue and then later to the Completed Queue.

## Models:

In this section each model needed for this system will be described. These models are how the application will access data, some from a database, others will store data temporarily for the application. They will be denoted as such in their title

### Order (Temporary)

This model represents how each order will store data

Attributes:

    private id::Integer
    name::String                          // name on order
    order::HashMap<FoodItem, Integer>     // <key: FoodItem, value: quantity>
    dateCreated::String                   // dateCreated
    timeStarted::Integer                  // in seconds since UNIX epoch
    totalPrepTime::Integer                // in seconds

Methods:

    Constructor(id) {
        this.id = id;
    }

### OrderDB (Database)

This model represents how each order will be stored in the database

Attributes:

    id::Integer
    orderId::Integer                      // orderId given by OrderController
    name::String                          // name on order
    dateCreated::String                   // dateCreated
    cashierId::Integer                    // id of cashier involved
    chefId::Integer                       // id of chef involved
    waiterId::Integer                     // id of waiter involved
    timeStarted::Integer                  // in seconds since UNIX epoch
    totalPrepTime::Integer                // in seconds
    timeCompleted::Integer                // in seconds
    canceled::boolean                     // true if canceled
    qualityIssue::boolean                 // true if sent back by customer

### Ingredients (Database)

These will be the lowest level of the classes that make up the Recipes

Attributes:

    id::Integer
    name::String
    inventoryQty::Integer

### Prepare (Database)

These prepare models describe what to do to ingredients to make a FoodItem. For example "Cook". The reason applianceId is needed is sometimes

Attributes:

    id::Integer
    name::String

### Utility (Database)

The Utility model will specify what should be used to Prepare the ingredients, ie "Oven", "Knife", etc

Attributes:

    id::Integer
    name::String

### RecipeIngredients (Database)

This is a One-to-many type relationship model where there will be 1 Recipe to many ingredients

Attributes:

    recipeId::Integer
    ingredientId::Integer
    qty::Integer
    units::String

### RecipeSteps (Database)

This will be a One-to-many type relationship model where there will be 1 Recipe to many steps including prepare, utilities, and ingredients

Attributes:

    recipeId::Integer
    stepNum::Integer          //which step we are on
    prepareId::Integer
    utilityId::Integer
    ingredientId::Integer
    qty::Integer
    units::String
    mixWithNext::boolean      //if true mix with the next in line, can sequence this to mix as many ingredients at one time
    timeToPrepare::Integer    //seconds
    timeToFlip::Integer       //when to interrupt to flip burgers, steaks,etc
    concurrent::boolean       //if other processes can be done concurrently, then set to true. Like waiting on brownies in the oven

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

### MenuType (Database)

Attributes:

    id::Integer
    name::String //"Dinner", "Dessert", "Sides", etc

### FoodItem (Database)

Attributes:

    id::Integer
    name::String            //"Hamburger", "Chicken Sandwich", etc
    recipeId::Integer      //from Recipe Model
    menuTypeId::Integer     //from MenuType Model

### Combos (Database)

Attributes:

    id::Integer
    name::String

### ComboFoods (Database)

One to Many Relationship between Combos and its associated FoodItems

Attributes:

    comboId::Integer
    foodItemId::Integer
