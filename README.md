# Order Processing System

The objective of this project is to decribe how to implement a Order Processing System for a Robot Cafeteria. The Cafeteria will start off with 3 workers: a cashier, a chef, and a waiter. The purpoose of this project is not to describe operation of the Robots, but how these robots will interact with the Ordering System. This project needs to be able to scale as the popularity of the Cafeteria increase.

## Models:

In this section each model needed for this system will be described. These Models will be stored in a database and then accessed and manipulate via a controller. 

### Ingredients
These will be the lowest level of the classes that make up the Recipes 

Attributes:

    id::Integer
    name::String
    inventoryQty::Integer

### Prepare
These prepare models describe what to do to ingredients to make a FoodItem. For example "Cook". The reason applianceId is needed is sometimes

Attributes:

    id::Integer
    name::String

### Utility
The Utility model will specify what should be used to Prepare the ingredients, ie "Oven", "Knife", etc

Attributes:

    id::Integer
    name::String

### RecipeIngredients
This is a One-to-many type relationship model where there will be 1 Recipe to many ingredients 

Attributes:

    recipeId::Integer
    ingredientId::Integer
    qty::Integer
    units::String

### ReciepeSteps
This will be a One-to-many type relationship model where there will be 1 Reciepe to many steps including prepare, utilities, and ingredients

Attributes:

    recipeId::Integer
    stepNum::Integer          //which step we are on
    prepareId::Integer
    utiltityId::Integer
    ingredientId::Integer
    qty::Integer
    units::String
    mixWithNext::boolean      //if true mix with the next in line, can sequence this to mix as many ingredients at one time
    timeToPrepare::Integer    //seconds
    timeToFlip::Integer       //when to interupt to flip burgers, steaks,etc
    conccurent::boolean       //if other processes can be done conncurrently, then set to true. Like waiting on brownies in the oven

Might need to add more things above to account for more actions, but the general idea is to save the ReciepeSteps in the database to help calculate optimum amount of time and space within the Cafeteria Kitchen. 

### User

These will be the lowest level of the classes that make up the Recipes and will be mainly used to keep inventory of all the

Attributes:

    id::Integer
    name::String
    password::hashed<String>
    isRobot::boolean            //if robot true
    securityLevelId::Integer    //from SecurityLevel model

### SecurityLevel

SecurityLevels will allow the program to determine who is allowed to do what

Attributes:

    id::Integer
    name::String //"Admin", "Cashier", "Chefs", "Waiters"

### MenuType

Attributes:

    id::Integer
    name::String //"Dinner", "Dessert", "Sides", etc

### FoodItem

Attributes:

    id::Integer
    name::String            //"Hamburger", "Chicken Sandwich", etc
    reciepeId::Integer      //from Reciepe Model
    menuTypeId::Integer     //from MenuType Model

### Combos

Attributes:

    id::Integer
    name::String
  
### ComboFoods
One to Many Relationship between Combos and its assoiciated FoodItems

Attributes:

    comboId::Integer
    foodItemId::Integer
  
## Temporay Class Models

This section will decribes classes that will be temporary used with in the application to store information until it is no longer needed

### Order

Attributes:

    private id::Integer
    name::String                          //name on order
    order::HashMap<FoodItem, Interger>    //<key: FoodItem, value: quantity>

Methods:

    Constructor(id) {
        this.id = id;
    }

    setName(String name) {
        this.name = name;
    }
    
    getName(String name) {
       return this.name;
    }
    
    addItem(int qty, String item){
      if item in database and qty not null {
        // update/add key: item, value: qty  += qty in order HashMap
      }
    }

    removeItem(int qty, String item){
        if item in database and qty not null {
          // update/add key: item, value: qty -= qty in order HashMap if qty - qty >= 0 else 0
        }
    }
    
    addCombo(int qty, String combo){
       query = query Combo model where name == combo
       if query not null and qty not null {
          foodItems = get all food items from query
          for item in foodItems {
            // update/add key: item, value: qty  += qty_from_query * qty in order HashMap
          }
       }
     }
    
    removeCombo(int qty, String item){
       query = query Combo model where name == combo
       if query not null and qty not null {
          foodItems = get all food items from query
          for item in foodItems {
            // update/add key: item, value: qty  -= qty_from_query * qty in order HashMap if qty - qty_from_query * qty >= 0 else 0
        }
      }
    }


## Controllers

This section will decribes controllers and their methods

### OrderController

Attributes:
    
    // All the following can be accessed by all class instances, so many OrderControllers can be running at one time at scale
    private static idIndexer::Integer       // this will increase each time a Order is created, reset by crontab method after midnight
    private static orderQueue::LinkedList<Order>           // this will keep the orders in a FIFO 
    private static inProcessList::LinkedList<Order>        // this will keep the orders that are being processed by the Chefs but might not be FIFO depending on prepare time so this is just a list
    private static completedQueue::LinkedList<Order>       // this will keep the orders that are Completed waiting for the waiter to deliver
    private static deliveredList::LinkedList<Order>        // this will keep the orders that are delivered until waiter sees customer leave, in case of customer rejection
    private static statusMap::HashMap<Integer, Integer[2]>  //key: orderId, value: [whichQueue, indexInList]
                                             //whichQueue = "1" = orderQueue | "2" = inProcessList | "3" = completedQueue | "4" = deliveredList

**Note:** the above statusMap adds Space Complexity, but will allow for easy finding where the 

Methods:

    createOrder() {
        idIndexer++;
        return new Order(idIndexer);
     }
     
     getOrderStatus(Order orderId) {
      // return value from statusMap where key == order.id
    }
    
    sendOrder(Order order) {
      // add order to orderQueue
      // add key: order.id, value: [1, orderQueue.length - 1]
    }
    
    processOrder() {
      // add to inProcessQueue
      // remove first order from orderQueue
      // add key: order.id, value: [2, inProcessQueue.length - 1]
    }
    
    completeOrder(Order order) {
      // add to completedQueue
      // remove order from inProcessQueue
      // add key: order.id, value: [3, completedQueue.length - 1]
    }
    
    deliveredOrder(Order order) {
      // add to deliverdList
      // remove order from completeQueue
      // add key: order.id, value: [4, deliverdList.length - 1]      
    }
    
    deleteOrder(Order order) {
      // remove order from deliverdList
      // remove order from status map where key == order.id
     }
     
    updateOrder(Order newOrder) {
      // get status of order in statusMap where key == newOrder.id
      // if status == 1 then update order = newOrder
     }
   
    resetIndexer() {
      idIndexer = 0;
     }

#### Cashier Work Flow with regard to Order Processing
This will allow a User of type "Cashier" to Create and Read an Order and submit it to the OrderQueue. If the order is still in the OrderQueue, and not in the inProcessQueue or completedQueue, the Cashier will be able to Update and Delete an Order. The Cashier Workflow is the following:

1. Cashier creates a new instance of the Order class using createOrder()
2. uses an Order.addItem() method to add an item to the Order
3. uses a Order.removeItem() method to remove an item, if total quantity not < 0
4. uses an Order.addCombo() method to add items within a combo
5. uses a Order.removeCombo() method to remove an item, if total quantity not < 0
6. when customer is satisfied the Cashier can send order to order queue
7. If customer decided to change the order and it is still within the orderQueue then the Cashier can update
8. If customer decided to delete the order and it is still within the orderQueue then the Cashier can update

#### Chef Work Flow with regard to Order Processing
This will allow a User of type "Cashier" to Create and Read an Order and submit it to the OrderQueue. If the order is still in the OrderQueue, and not in the inProcessQueue or completedQueue, the Cashier will be able to Update and Delete an Order. The Cashier Workflow is the following:

1) Cashier creates a new instance of the Order class using createOrder()
2) uses an Order.addItem() method to add an item to the Order
3) uses a Order.removeItem() method to remove an item, if total quantity not < 0
4) uses an Order.addCombo() method to add items within a combo
5) uses a Order.removeCombo() method to remove an item, if total quantity not < 0
6) when customer is satisfied the Cashier can send order to order queue
7) If customer decided to change the order and it is still within the orderQueue then the Cashier can update
8) If customer decided to delete the order and it is still within the orderQueue then the Cashier can update

#### Cashier Work Flow with regard to Order Processing
This will allow a User of type "Cashier" to Create and Read an Order and submit it to the OrderQueue. If the order is still in the OrderQueue, and not in the inProcessQueue or completedQueue, the Cashier will be able to Update and Delete an Order. The Cashier Workflow is the following:

1) Cashier creates a new instance of the Order class using createOrder()
2) uses an Order.addItem() method to add an item to the Order
3) uses a Order.removeItem() method to remove an item, if total quantity not < 0
4) uses an Order.addCombo() method to add items within a combo
5) uses a Order.removeCombo() method to remove an item, if total quantity not < 0
6) when customer is satisfied the Cashier can send order to order queue
7) If customer decided to change the order and it is still within the orderQueue then the Cashier can update
8) If customer decided to delete the order and it is still within the orderQueue then the Cashier can update

### MenuController
This will allow a User of type "Cashier" to Create and Read an Order and submit it to the OrderQueue. If the order is still in the OrderQueue, and not in the inProcessQueue or completedQueue, the Cashier will be able to Update and Delete an Order. 

### OrderQueue

That will allow the cashier to add Orders given an orderId and the order. The orderId's will be stored in a queue based on a optimazation Algorthim. The next Order in the queue will be processed by the next availble Robot Chef. 

### ReciepeController
Attributes:
- ingredients::HashMap<Integer, Integer> //key: ingredientId, value: quantity needed for Reciepe
- steps::ArrayList<ArrayList<Integer>> //ie {{prepareId, utilityId, itemId, itemQty, timeToPrepare, flipTime, flipCount}} 
  
### Middleware
SecurityLevels will allow Cashiers to only add/updadte/remove orders to and from the queue, Chefs to start/prepare/complete orders, and waiters to take orders from a completed orders queue. Admins would be allowed to change

