package org.grey;

import static org.grey.Main.threadResult;

public class ThreadThree implements Runnable {

    // To create a thread simply implement the Runnable class and override the run() method with the code you want to execute in the thread

    @Override
    public void run() {
        try {
            System.out.println("Thread 3 - started");

            Thread.sleep(2000);

            System.out.println("Thread 3 - finished");

            threadResult[2] = 1;
        } catch (InterruptedException e) {
            e.printStackTrace();

            threadResult[2] = -1;
        }
    }
}
