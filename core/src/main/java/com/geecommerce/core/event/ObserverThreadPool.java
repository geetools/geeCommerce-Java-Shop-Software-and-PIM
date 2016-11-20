package com.geecommerce.core.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.geecommerce.core.App;
import com.geecommerce.core.AppRegistry;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.DefaultApplicationContext;
import com.geecommerce.core.cron.Environment;

/**
 * Singleton for executing asynchronous observers using the
 * java.util.concurrent.Executors.newFixedThreadPool().
 * 
 * @see com.geecommerce.core.event.Observable
 * 
 * @author Michael Delamere
 */
public enum ObserverThreadPool {
    INSTANCE;

    private final ExecutorService THREAD_POOL;

    ObserverThreadPool() {
        this.THREAD_POOL = Executors.newFixedThreadPool(20);
    }

    public void run(final Observable observable, final Event event, final Observer observer) {
        THREAD_POOL.execute(new ObserverRunnable(observable, event, observer));
    }

    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    }
}

class ObserverRunnable implements Runnable {
    private final Map<String, Object> appRegistry;

    final Observable observable;
    final Observer observer;
    final Event event;

    public ObserverRunnable(final Observable observable, final Event event, final Observer observer) {
        this.appRegistry = new HashMap<String, Object>(AppRegistry.getAll());

        // As the above is only a shallow copy, we at least attempt to deep copy
        // the application context
        // so that the asynchronous thread does not lose this value when the
        // registry of the main thread is cleared.
        ApplicationContext appCtx = new DefaultApplicationContext(App.get().context());
        appRegistry.put(ApplicationContext.class.getName(), appCtx);

        this.observable = observable;
        this.observer = observer;
        this.event = event;
    }

    @Override
    public void run() {
        try {
            String name = Thread.currentThread().getName();

            if (!name.startsWith("cb")) {
                Thread.currentThread().setName("cb-observer-" + name);
            }

            AppRegistry.putAll(appRegistry);

            App.get().registryPut("thread.context", "observer");

            observer.onEvent(event, observable);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            Environment.cleanUp();
        }
    }
}

class ObserverThreadPoolExecutor extends ThreadPoolExecutor {
    public ObserverThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }
}