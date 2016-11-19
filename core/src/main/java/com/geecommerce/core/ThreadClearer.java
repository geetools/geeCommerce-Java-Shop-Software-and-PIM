package com.geecommerce.core;

import java.util.Set;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;

import com.geecommerce.core.interceptor.threadlocal.MethodInterceptorContext;
import com.geecommerce.core.message.MessageBus;
import com.geemodule.GeemoduleRegistry;

public class ThreadClearer {

    @SuppressWarnings("deprecation")
    public static void clear() {
	// CB ThreadLocals
	MethodInterceptorContext.cleanupThread();
	AppRegistry.cleanupThread();
	MessageBus.cleanupThread();
	GeemoduleRegistry.cleanupThread();

	// Clear Log4j which is causing memory leaks
	ThreadContext.clearAll();

	// Also clear this one to be on the safe side
	MDC.clear();

	Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
	Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

	for (Thread t : threadArray) {
	    if (t.getName().contains("Abandoned connection cleanup thread") || t.getName().matches("com\\.google.*Finalizer")) {
		synchronized (t) {
		    t.stop();
		}
	    }
	}
    }
}
