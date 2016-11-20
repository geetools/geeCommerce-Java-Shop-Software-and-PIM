package com.geecommerce.core.message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton for executing asynchronous messages using the
 * java.util.concurrent.Executors.newFixedThreadPool().
 * 
 * @see com.geecommerce.core.message.MessageBus
 * 
 * @author Michael Delamere
 */
public enum MessageBusThreadPool {
    INSTANCE;

    private final ExecutorService THREAD_POOL;

    MessageBusThreadPool() {
        this.THREAD_POOL = Executors.newFixedThreadPool(20);
    }

    public void run(final Subscriber subscriber, final Context ctx) {
        Runnable asynchSubscriber = new Runnable() {
            public void run() {
                subscriber.onMessage(ctx);
            }
        };

        THREAD_POOL.execute(asynchSubscriber);
    }
}
