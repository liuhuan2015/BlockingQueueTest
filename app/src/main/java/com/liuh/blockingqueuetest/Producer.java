package com.liuh.blockingqueuetest;

import android.util.Log;

import java.util.concurrent.BlockingQueue;

/**
 * 生产者
 */
public class Producer implements Runnable {

    private static final String TAG = "Producer";

    BlockingQueue<String> mBlockingQueue;

    public Producer(BlockingQueue<String> queue) {
        this.mBlockingQueue = queue;
    }


    @Override
    public void run() {
        String product_name = "a product - " + Thread.currentThread().getName();
        Log.e(TAG, "I have producted: " + Thread.currentThread().getName());
        try {
            //put 如果队列满了，则阻塞
            mBlockingQueue.put(product_name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
