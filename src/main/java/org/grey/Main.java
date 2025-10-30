package org.grey;


public class Main {
    public static void main(String[] args) throws InterruptedException {

        // Threading allows a program to run multiple tasks simultaneously
        // This is handy for tasks that take a long time to execute
        //
        // There are two ways to use threads
        //   Option 1. Extend the Thread class (simpler)
        //   Option 2. Implement the Runnable interface (better - because you can still inherit other classes)
        
        // In this example we will use the Runnable interface to call three different threads and only continue the program when all three threads are successful

        System.out.println("Starting...");

        Thread threadOne = new Thread(new ThreadOne());
        Thread threadTwo = new Thread(new ThreadTwo());
        Thread threadThree = new Thread(new ThreadThree());

        threadOne.start();
        threadTwo.start();
        threadThree.start();

        boolean threadsAllDone = false;

        while (!threadsAllDone) {
            Thread.sleep(200);
            System.out.printf("Thread 1 status : %d, Thread 2 status : %d, Thread 3 status %d\n", threadResult[0], threadResult[1], threadResult[2]);

            // With a simple check we can see when all the separate threads are done
            if (threadResult[0] != 0 && threadResult[1] != 0 && threadResult[2] != 0) {
                threadsAllDone = true;
            }
        }
    }

    //  By marking a variable as volatile it is visible across all threads
    volatile static protected int[] threadResult = new int[3];
}