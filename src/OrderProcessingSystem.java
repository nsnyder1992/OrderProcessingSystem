import java.util.ArrayList;
import java.util.Random;

class OrderProcessingSystem {

    public void test() {
        long duration = 0;
        OrderController oc = new OrderController();
        int testTimes = 1000;
        int testItems = 100000; // no less than 100

        for (int i = 0; i < testItems; i++) {
            // create orders
            oc.createOrder(i);

            // process order
            if (i < testItems / 2) {
                oc.processOrder();

                // complete order
                if (i % 4 == 0) {
                    oc.completeOrder(i);

                    // deliver order
                    if (i % 10 == 0) {
                        oc.deliveredOrder();

                        if (i % 2 == 0)
                            oc.deleteOrder(i, false);
                    }
                }
            }
        }

        // Test search
        for (int i = 0; i < testTimes; i++) {

            Random rand = new Random();
            int upperbound = testItems;
            int searchValue = rand.nextInt(upperbound);

            long startTime = System.nanoTime();
            oc.getOrder(searchValue);
            long endTime = System.nanoTime();

            duration = i == 0 ? (endTime - startTime) : ((endTime - startTime) + duration) / 2;// divide by 1000000 for
                                                                                               // ms;

        }

        int orderQueueSize = oc.getOrderQueue().size();
        int inProcessSize = oc.getInProcessList().size();
        int completedQueueSize = oc.getCompletedQueue().size();
        int deliveredSize = oc.getDeliveredList().size();
        int deletedOrders = testItems - orderQueueSize - inProcessSize - completedQueueSize - deliveredSize;

        System.out.println(String.format("Orders in orderQueue: %d", orderQueueSize));
        System.out.println(String.format("Orders in inProcessList: %d", inProcessSize));
        System.out.println(String.format("Orders in completedQueue: %d", completedQueueSize));
        System.out.println(String.format("Orders in deliveredList: %d", deliveredSize));
        System.out.println(String.format("Deleted Orders: %d", deletedOrders));

        System.out.println(String.format("Average Time to find a Random order: %d ns", duration));

    }

    public static void main(String args[]) {
        OrderProcessingSystem ops = new OrderProcessingSystem();
        ops.test();
    }
}