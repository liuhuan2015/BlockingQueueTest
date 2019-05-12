package com.liuh.blockingqueuetest;

import android.util.Log;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {

    private static final String TAG = "Consumer";

    BlockingQueue<String> mBlockingQueue;

    public Consumer(BlockingQueue<String> mBlockingQueue) {
        this.mBlockingQueue = mBlockingQueue;
    }

    @Override
    public void run() {
        try {
            //如果队列为空，则阻塞
            String product_name = mBlockingQueue.take();
            Log.e(TAG, "消费了：" + product_name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
