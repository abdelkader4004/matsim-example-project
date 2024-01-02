package org.matsim.MyDVRP.multi_trucks;


import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.Thread.sleep;

public class BufferConsumer implements Runnable {
    //public static     Queue<Object> buffer = new ArrayBlockingQueue<Object>();
    public static ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<>(10);
    private volatile boolean running = true;
    public void halte() {
        running = false;
    }
    @Override
    public void run() {
        try {
            buffer.put("Hello Buffer");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (running) {
            try {
                System.out.println(buffer.take());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }

}
