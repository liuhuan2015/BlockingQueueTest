package com.liuh.blockingqueuetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MainActivity extends AppCompatActivity {
    //阻塞队列，容量为2
    BlockingQueue<String> mBlockingQueue = new LinkedBlockingDeque<>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testProducerAndConsumer(View view) {

        for (int i = 0; i < 5; i++) {
            new Thread(new Producer(mBlockingQueue)).start();

            new Thread(new Consumer(mBlockingQueue)).start();
        }
    }
}
