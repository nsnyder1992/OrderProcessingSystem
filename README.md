# Order Processing System

The objective of this project is to decribe how to implement a Order Processing System for a Robot Cafeteria. The Cafeteria will start off with 3 workers: a cashier, a chef, and a waiter. The purpoose of this project is not the operation of the Robots, only how these robots interact with the Ordering System will be described.

## Models:

In this section each model needed for this system will be described from the bottom up.

### Ingredints
These will be the lowest level of the classes and will be mainly used to keep inventory of all the
Attributes:
- id::Integer
- name::String
- inventoryQty::Integer

### Prepare
Attributes:
- id::Integer
- name::String
- consistant::boolean //if the cook will need to be constantly preparing set to true
  
### Recipe
Attributes:
- id::Integer
- name::String
- items::LinkedList<Integer> //
- steps::ArrayList<ArrayList<Integer>> //ie {{prepareId, itemsId, timeToPrepare},{},{}}

### FoodItem

### Combos

### Orders

### OrderProcessingSystem

That will allow the cashier to add Orders given an orderId and the order. The orderId's will be stored in a queue based on a optimazation Algorthim. The next Order in the queue will be processed by the next availble Robot Chef. 
