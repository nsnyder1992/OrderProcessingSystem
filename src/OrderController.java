import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

public class OrderController {
    /**
     * This class handles Creating orders by adding/removing items and combos using
     * the fake menu
     */

    // initialize static Queues
    private static LinkedList<Integer> orderQueue = new LinkedList<Integer>();
    private static ArrayList<Integer> inProcessList = new ArrayList<Integer>();
    private static LinkedList<Integer> completedQueue = new LinkedList<Integer>();
    private static ArrayList<Integer> deliveredList = new ArrayList<Integer>();

    // statuses
    private HashMap<Integer, Integer> statusMap = new HashMap<Integer, Integer>();
    // key: orderId,
    // value:
    // "1" = orderQueue |
    // "2" = inProcessList |
    // "3" = completedQueue |
    // "4" = deliveredList

    public HashMap<Integer, Integer> getStatusMap() {
        return statusMap;
    }

    public void createOrder(int orderId) {
        Order order = new Order(orderId);
        orderQueue.add(order.id);
        statusMap.put(order.id, 1); // order.id: "1" for in orderQueue
    }

    public LinkedList<Integer> getOrderQueue() {
        return orderQueue;
    }

    public int getOrder(int orderId) {
        int listId = getOrderStatus(orderId);

        if (listId == 1) {
            return orderQueue.indexOf(orderId);
        } else if (listId == 2) {
            return inProcessList.indexOf(orderId);
        } else if (listId == 3) {
            return completedQueue.indexOf(orderId);
        } else if (listId == 4) {
            return deliveredList.indexOf(orderId);
        }

        return -1;
    }

    public int getOrderStatus(int orderId) {
        if (statusMap.get(orderId) == null)
            return -1;
        return statusMap.get(orderId);
    }

    public void processOrder() {
        int orderId = orderQueue.removeFirst();
        statusMap.put(orderId, 2); // orderId: "2" for inProcessList

        inProcessList.add(orderId);
    }

    public ArrayList<Integer> getInProcessList() {
        return inProcessList;
    }

    public void completeOrder(int orderId) {
        if (getOrderStatus(orderId) != 2)
            return;
        int index = inProcessList.indexOf(orderId);
        inProcessList.remove(index);

        completedQueue.add(orderId);
        statusMap.put(orderId, 3); // orderId: "3" for completedQueue

    }

    public LinkedList<Integer> getCompletedQueue() {
        return completedQueue;
    }

    public void deliveredOrder() {
        int orderId = completedQueue.removeFirst();
        deliveredList.add(orderId);
        statusMap.put(orderId, 4); // orderId: "4" for deliveredList
    }

    public ArrayList<Integer> getDeliveredList() {
        return deliveredList;
    }

    public void rejectOrder(int orderId) {
        // set order.quality = true in database
        deleteOrder(orderId, false);
    }

    public void deleteOrder(int orderId, boolean wasCanceled) {
        if (wasCanceled) {
            // set order.canceled = true in database
        }

        int status = getOrderStatus(orderId);
        int index = getOrder(orderId);

        // update queues/lists
        if (status == 1) {
            orderQueue.remove(index);
        } else if (status == 2) {
            inProcessList.remove(index);
        } else if (status == 3) {
            completedQueue.remove(index);
        } else if (status == 4) {
            deliveredList.remove(index);
        }

        // update status map
        statusMap.remove(orderId);
    }
}
