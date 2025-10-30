package org.grey;

import static org.grey.Main.threadResult;

public class ThreadOne implements Runnable {

    // To create a thread simply implement the Runnable class and override the run() method with the code you want to execute in the thread

    @Override
    public void run() {
        try {
            System.out.println("Thread 1 - started");

            Thread.sleep(1000);

            System.out.println("Thread 1 - finished");

            threadResult[0] = 1;
        } catch (InterruptedException e) {
            e.printStackTrace();

            threadResult[0] = -1;
        }
    }
}
