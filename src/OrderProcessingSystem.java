import java.util.ArrayList;
import java.util.Random;

class OrderProcessingSystem {

    public void test() {
        long duration = 0;
        OrderController oc = new OrderController();
        int testTimes = 1000;
        int testItems = 100000;

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