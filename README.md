# Order Processing System

The objective of this project is to decribe how to implement a Order Processing System for a Robot Cafeteria. The Cafeteria will start off with 3 workers: a cashier, a chef, and a waiter. The purpoose of this project is not to describe operation of the Robots, but how these robots will interact with the Ordering System. This project needs to be able to scale as the popularity of the Cafeteria increase.

## Models:

In this section each model needed for this system will be described. These Models will be stored in a database and then accessed and manipulate via a controller. 

### Ingredients
These will be the lowest level of the classes that make up the Recipes and will be mainly used to keep inventory of all the
Attributes:
- id::Integer
- name::String
- inventoryQty::Integer

### Prepare
These prepare models describe what to do to ingredients to make a FoodItem. For example "Cook". The reason applianceId is needed is sometimes
Attributes:
- id::Integer
- name::String

### Utility
The Utility model will specify what should be used to Prepare the ingredients, ie "Oven", "Knife", etc
Attributes:
- id::Integer
- name::String

### RecipeIngredients
This is a One-to-many type relationship model where there will be 1 Recipe to many ingredients 
Attributes:
- recipeId::Integer
- ingredientId::Integer
- qty::Integer
- units::String

### ReciepeSteps
This will be a One-to-many type relationship model where there will be 1 Reciepe to many steps including prepare, utilities, and ingredients
- recipeId::Integer
- stepNum::Integer //which step we are on
- prepareId::Integer
- utiltityId::Integer
- ingredientId::Integer
- mixWithNext::boolean //if true mix with the next in line, can sequence this to mix as many ingredients at one time
- timeToPrepare::Integer //seconds
- timeToFlip::Integer //when to interupt to flip burgers, steaks,etc
- conccurent::boolean //if other processes can be done conncurrently, then set to true. Like waiting on brownies in the oven

Might need to add more things above to account for more actions, but the general idea is to save the ReciepeSteps in the database to help calculate optimum amount of time and space within the Cafeteria Kitchen. 

### User
These will be the lowest level of the classes that make up the Recipes and will be mainly used to keep inventory of all the
Attributes:
- id::Integer
- name::String
- password::hashed<String>
- isRobot::boolean //if robot true
- securityLevelId::Integer //from SecurityLevel model

### SecurityLevel
SecurityLevels will allow the program to determine who is allowed to do what
Attributes:
- id::Integer
- name::String //"Admin", "Cashier", "Chefs", "Waiters"

### MenuType
Attributes:
  -id::Integer
  -name::String //"Dinner", "Dessert", "Sides", etc

### FoodItem
Attributes:
  -id::Integer
  -name::String //"Hamburger", "Chicken Sandwich", etc
  -reciepeId::Integer //from Reciepe Model
  -menuTypeId::Integer //from MenuType Model

### Combos
Attributes:
  - id::Integer
  - name::String
  
### ComboFoods
One to Many Relationship between Combos and its assoiciated 
Attributes:
  -comboId::Integer
  -foodId::Integer


## Controllers
This section will decribe controllers and their methods

### OrderController

### OrderProcessingSystem

That will allow the cashier to add Orders given an orderId and the order. The orderId's will be stored in a queue based on a optimazation Algorthim. The next Order in the queue will be processed by the next availble Robot Chef. 

### ReciepeController
Attributes:
- ingredients::HashMap<Integer, Integer> //key: ingredientId, value: quantity needed for Reciepe
- steps::ArrayList<ArrayList<Integer>> //ie {{prepareId, utilityId, itemId, itemQty, timeToPrepare, flipTime, flipCount}} 
  
### Middleware
SecurityLevels will allow Cashiers to only add/updadte/remove orders to and from the queue, Chefs to start/prepare/complete orders, and waiters to take orders from a completed orders queue. Admins would be allowed to change

