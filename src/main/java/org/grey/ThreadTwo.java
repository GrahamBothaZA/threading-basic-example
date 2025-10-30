package org.grey;

import static org.grey.Main.threadResult;

public class ThreadTwo implements Runnable {

    // To create a thread simply implement the Runnable class and override the run() method with the code you want to execute in the thread

    @Override
    public void run() {
        try {
            System.out.println("Thread 2 started");

            Thread.sleep(3000);

            System.out.println("Thread 2 finished");

            threadResult[1] = 1;
        } catch (InterruptedException e) {
            e.printStackTrace();

            threadResult[1] = -1;
        }
    }

}
