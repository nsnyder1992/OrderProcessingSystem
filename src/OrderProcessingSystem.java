import java.util.ArrayList;
import java.util.Random;

class OrderProcessingSystem {

    public void test() {
        OrderController oc = new OrderController();

        // init test params
        int testTimes = 10000;
        int testItems = 100000; // no less than 100

        // init stats
        long time = 0;
        long startTime = 0;
        long endTime = 0;
        long duration = 0;
        long max = 0;
        long min = 0;
        int maxOrder = 0;
        int minOrder = 0;
        int maxOrderStatus = 0;
        int minOrderStatus = 0;
        int maxOrderIndex = 0;
        int minOrderIndex = 0;

        // init random variables
        Random rand = new Random();
        int ten = 10;
        int processRand = (int) (Math.random() * (ten - 2) + 2);
        int completeRand = (int) (Math.random() * (ten - 2) + 2);
        int deliveredRand = (int) (Math.random() * (ten - 2) + 2);
        int deletedRand = (int) (Math.random() * (ten - 2) + 2);
        int searchValue = rand.nextInt(testItems);

        System.out.println("Starting Test!");
        System.out.print("Generating Test Sample...");
        // generate test sample
        for (int i = 0; i < testItems; i++) {
            // create orders
            oc.createOrder(i);

            // process order
            if (i < testItems - (testItems / processRand)) {
                oc.processOrder();

                // complete order
                if (i % completeRand == 0) {
                    oc.completeOrder(i);

                    // deliver order
                    if (i % deliveredRand == 0) {
                        oc.deliveredOrder();

                        if (i % deletedRand == 0)
                            oc.deleteOrder(i, false);
                    }
                }
            }
        }
        System.out.println("Complete!");

        // Test search
        System.out.print("Testing Search Method...");
        for (int i = 0; i < testTimes; i++) {

            searchValue = rand.nextInt(testItems);

            startTime = System.nanoTime();
            int orderIndex = oc.getOrder(searchValue);
            endTime = System.nanoTime();

            time = (endTime - startTime);

            if (time > max) {
                max = time;
                maxOrderStatus = oc.getOrderStatus(searchValue);
                maxOrder = searchValue;
                maxOrderIndex = orderIndex;
            }

            if (time < min) {
                min = time;
                minOrderStatus = oc.getOrderStatus(searchValue);
                minOrder = searchValue;
                minOrderIndex = orderIndex;
            }

            if (i == 0) {
                min = time;
                minOrderStatus = oc.getOrderStatus(searchValue);
                minOrder = searchValue;
                minOrderIndex = orderIndex;
            }

            duration = i == 0 ? time : (time + duration) / 2;// divide by 1000000 for
                                                             // ms;
        }
        System.out.println("Complete!");

        int orderQueueSize = oc.getOrderQueue().size();
        int inProcessSize = oc.getInProcessList().size();
        int completedQueueSize = oc.getCompletedQueue().size();
        int deliveredSize = oc.getDeliveredList().size();
        int deletedOrders = testItems - orderQueueSize - inProcessSize - completedQueueSize - deliveredSize;

        System.out.println("");
        System.out.println("Num of Orders:");
        System.out.println(String.format("Orders in orderQueue: %d", orderQueueSize));
        System.out.println(String.format("Orders in inProcessList: %d", inProcessSize));
        System.out.println(String.format("Orders in completedQueue: %d", completedQueueSize));
        System.out.println(String.format("Orders in deliveredList: %d", deliveredSize));
        System.out.println(String.format("Deleted Orders: %d", deletedOrders));

        System.out.println("");
        System.out.println("Time statistics:");
        System.out.println(String.format("Average Time to find a Random order: %d ns", duration));
        System.out.println(String.format("Maximum Time: %d ns, Order: %d, Order Status: %d, Order Index: %d", max,
                maxOrder, maxOrderStatus, maxOrderIndex));
        System.out.println(String.format("Minium Time: %d ns, Order: %d, Order Status: %d, Order Index: %d", min,
                minOrder, minOrderStatus, minOrderIndex));

    }

    public static void main(String args[]) {
        OrderProcessingSystem ops = new OrderProcessingSystem();
        ops.test();
    }
}