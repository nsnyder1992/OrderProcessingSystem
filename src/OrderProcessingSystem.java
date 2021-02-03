import java.util.ArrayList;
import java.util.Random;

class OrderProcessingSystem {

    public void test() {
        long duration = 0;
        OrderController oc = new OrderController();
        int testTimes = 1000;
        int testItems = 100000;

        // create orders
        for (int i = 0; i < testItems; i++) {
            oc.createOrder(i);
        }

        // process orders
        for (int i = 0; i < (testItems / 2); i++) {
            oc.processOrder();
        }

        // complete orders
        ArrayList<Integer> inProcessList = oc.getInProcessList();
        for (int i = 0; i < (inProcessList.size() - (testItems / 4)); i += 2) {
            int orderId = inProcessList.get(i);
            oc.completeOrder(orderId);
        }

        // delivered orders
        for (int i = 0; i < (testItems / 100); i++) {
            oc.deliveredOrder();
        }

        // delete orders
        ArrayList<Integer> deliveredList = oc.getDeliveredList();
        ArrayList<Integer> removeList = new ArrayList<>();

        for (int i = 0; i < deliveredList.size(); i += 4) {
            int orderId = deliveredList.get(i);
            removeList.add(orderId);
        }

        for (int orderId : removeList) {
            oc.deleteOrder(orderId, false);
        }

        // Test search
        for (int j = 0; j < testTimes; j++) {

            Random rand = new Random();
            int upperbound = testItems;
            int searchValue = rand.nextInt(upperbound);

            long startTime = System.nanoTime();
            oc.getOrder(searchValue);
            long endTime = System.nanoTime();

            duration = ((endTime - startTime) + duration) / 2;// divide by 1000000 for ms;

        }

        System.out.println(String.format("Average Duration: %d ns", duration));
    }

    public static void main(String args[]) {
        OrderProcessingSystem ops = new OrderProcessingSystem();
        ops.test();
    }
}