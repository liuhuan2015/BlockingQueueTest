
#### 一、生产者消费者 --> BlockingQueue

BlockingQueue 是一个接口，它的实现类主要有 ArrayBlockingQueue（基于数组，有界队列）、LinkedBlockingQueue（无界队列）。

使用 BlockingQueue 可以让服务线程在队列为空时，进行等待，当有新的消息进入队列后，自动将线程唤醒。


压入元素：offer()，如果队列满了，返回false； put()，如果队列满了，它会一直等待，直到有空闲的位置

弹出元素：poll()，如果队列为空直接返回null；take()，会等到队列中有可用元素时取出，如果没有，就一直等待。

#### 二、Android中为什么主线程不会因为Looper.loop()里的死循环卡死？

>http://gityuan.com

1. android中为什么要在主线程中位维持一个死循环？

每个app运行时，首先会创建一个进程，该进程是由 Zygote fork 出来的，用于承载app上运行的各种Activity/Service组件，
大多数情况下，一个app就运行在一个进程中，除非在AndroidManifest.xml中配置Android:process属性，或者通过native代码
fork进程。

线程对于app来说非常常见，比如每次new Thread().start都会创建一个新的线程。该线程与app所在进程之间资源共享，
从Linux角度来看，进程和线程除了是否共享资源外，并没有本质的区别，都是一个task_struct结构体。
在cpu看来，进程和线程无非就是一段可执行的代码，cpu通过CFS调度算法，保证每个task都尽可能公平的享有cpu时间片。

线程是一段可执行的代码，当这段可执行的代码执行完毕，线程生命周期便终止了，线程退出。但是对于android中的主线程，
我们是绝不希望它运行一段时间后，自己就退出的，那么如何保证它能一直存活呢？
简单的做法就是：让这段可执行的代码一直执行下去，使用死循环便能实现这一点。当然并非简单的死循环，无消息时会休眠。

真正会卡死主线程的操作是在回调方法onCreate/onStart/onResume等操作时间过长，会导致掉帧，
甚至发生ANR，looper.loop本身不会导致应用卡死

2. 为什么没有看到哪里有相关代码为这个死循环准备一个新线程去运转？

在进入死循环之前便会创建新的binder线程，在ActivityThread.main()中：
```java
    public static void main(String[] args) {

      ...

      //创建Looper和MessageQueue对象，用于处理主线程的消息
      Looper.prepareMainLooper();

      //创建ActivityThread对象
      ActivityThread thread = new ActivityThread();

      //建立Binder通道（创建新线程）
      thread.attach(false, startSeq);

      //消息循环运行
      Looper.loop();

      throw new RuntimeException("Main thread loop unexpectedly exited");
    }
```

thread.attach(false, startSeq); 便会创建一个Binder线程，该Binder线程通过Handler将Message发送给主线程。

ActivityThread实际上并非线程，不像HandlerThread类，ActivityThread并没有真正继承Thread类，
只是往往运行在主线程，给人以线程的感觉。其实承载ActivityThread的主线程就是由Zygote fork而创建的进程。

**主线程的死循环一直运行是不是特别消耗cpu资源？**

这里涉及到 Linux 的 pipe/epoll机制，类似阻塞队列的形式，主线程大多数时候都是出于休眠状态，并不会消耗大量的cpu资源。

3. Activity的生命周期方法是怎么实现在死循环体外能够执行起来的？

ActivityThread中有一个内部类 H 继承于 Handler，通过Handler消息机制，可以在一个进程中的不同线程间进行通信。

Activity的生命周期都是依靠主线程的Looper.loop，当收到不同Message时则采用相应措施：
在H.handleMessage(msg)方法中，根据接收到不同的msg，执行相应的生命周期。

比如收到msg=H.LAUNCH_ACTIVITY，则调用ActivityThread.handleLaunchActivity()方法，最终会通过反射机制，
创建Activity实例，然后再执行Activity.onCreate()等方法；
再比如收到msg=H.PAUSE_ACTIVITY，则调用ActivityThread.handlePauseActivity()方法，
最终会执行Activity.onPause()等方法。

这其中涉及到进程和线程间的通讯过程，还是比较复杂的。










