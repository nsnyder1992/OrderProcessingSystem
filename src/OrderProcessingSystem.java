import java.util.ArrayList;
import java.util.Random;

class OrderProcessingSystem {

    public void test() {
        OrderController oc = new OrderController();

        // init stats
        long duration = 0;
        long max = 0;
        long min = 0;

        // init test params
        int testTimes = 10000;
        int testItems = 100000; // no less than 100

        // init random variables
        Random rand = new Random();
        int ten = 10;
        int processRand = (int) (Math.random() * (ten - 1) + 1);
        int completeRand = (int) (Math.random() * (ten - 1) + 1);
        int deliveredRand = (int) (Math.random() * (ten - 1) + 1);
        int deletedRand = (int) (Math.random() * (ten - 1) + 1);

        System.out.println("Starting Test...");
        // generate test sample
        for (int i = 0; i < testItems; i++) {
            // create orders
            oc.createOrder(i);

            // process order
            if (i < testItems / processRand) {
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

        // Test search
        for (int i = 0; i < testTimes; i++) {

            int searchValue = rand.nextInt(testItems);

            long startTime = System.nanoTime();
            oc.getOrder(searchValue);
            long endTime = System.nanoTime();

            max = (endTime - startTime) > max ? (endTime - startTime) : max;
            min = (endTime - startTime) < min ? (endTime - startTime) : min;
            if (i == 0)
                min = (endTime - startTime);
            duration = i == 0 ? (endTime - startTime) : ((endTime - startTime) + duration) / 2;// divide by 1000000 for
                                                                                               // ms;
        }

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
        System.out.println(String.format("Maximum Time to find a Random order: %d ns", max));
        System.out.println(String.format("Minimum Time to find a Random order: %d ns", min));

    }

    public static void main(String args[]) {
        OrderProcessingSystem ops = new OrderProcessingSystem();
        ops.test();
    }
}