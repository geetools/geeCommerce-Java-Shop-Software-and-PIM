package com.geecommerce.core.event;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolMonitor implements Runnable {
    ThreadPoolExecutor executor;

    public ThreadPoolMonitor(ThreadPoolExecutor executor) {
	this.executor = executor;
    }

    @Override
    public void run() {
	try {
	    do {
		System.out.println(String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s", this.executor.getPoolSize(), this.executor.getCorePoolSize(), this.executor.getActiveCount(),
			this.executor.getCompletedTaskCount(), this.executor.getTaskCount(), this.executor.isShutdown(), this.executor.isTerminated()));

		Thread.sleep(10000);
	    } while (true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
