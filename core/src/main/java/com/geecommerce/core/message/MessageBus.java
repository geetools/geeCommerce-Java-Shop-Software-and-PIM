package com.geecommerce.core.message;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.cron.Environment;
import com.geecommerce.core.event.Run;
import com.geecommerce.core.message.annotation.Subscribe;
import com.geecommerce.core.reflect.Reflect;

public class MessageBus {
    private static ThreadLocal<Map<String, Set<Class<Subscriber>>>> MESSAGE_BUS_REGISTRY_THREAD_LOCAL = new ThreadLocal<Map<String, Set<Class<Subscriber>>>>() {
        protected Map<String, Set<Class<Subscriber>>> initialValue() {
            return new HashMap<String, Set<Class<Subscriber>>>();
        }
    };

    @SuppressWarnings("unchecked")
    public static final void registerSubscribers() {
        if (!Environment.isMessageBusEnabled())
            return;

        Map<String, Set<Class<Subscriber>>> mbRegistry = registryMap();

        Set<Class<?>> annotatedTypes = Reflect.getTypesAnnotatedWith(Subscribe.class, false);

        for (Class<?> annotatedType : annotatedTypes) {
            Annotation declaredAnnotation = Reflect.getDeclaredAnnotation(annotatedType, Subscribe.class);

            if (declaredAnnotation != null) {
                Subscribe subcribeAnnotation = ((Subscribe) declaredAnnotation);
                String message = subcribeAnnotation.value();

                if (Str.isEmpty(message))
                    message = subcribeAnnotation.message();

                boolean foundNonEmptyMessages = false;
                String[] messages = null;

                if (Str.isEmpty(message)) {
                    messages = subcribeAnnotation.messages();

                    if (messages == null || messages.length == 0)
                        continue;

                    for (String msg : messages) {
                        if (!Str.isEmpty(msg)) {
                            foundNonEmptyMessages = true;
                            break;
                        }
                    }

                    if (!foundNonEmptyMessages)
                        continue;
                } else {
                    messages = new String[] { message };
                }

                if (Str.isEmpty(message) && !foundNonEmptyMessages)
                    continue;

                for (String msg : messages) {
                    Set<Class<Subscriber>> subscribers = mbRegistry.get(msg);

                    if (subscribers == null) {
                        subscribers = new HashSet<Class<Subscriber>>();
                        mbRegistry.put(msg, subscribers);
                    }

                    if (Subscriber.class.isAssignableFrom(annotatedType))
                        subscribers.add((Class<Subscriber>) annotatedType);
                }
            }
        }
    }

    public static void publish(String message, Context ctx) {
        if (!Environment.isMessageBusEnabled())
            return;

        App app = App.get();

        Set<Class<Subscriber>> subscribers = registryMap().get(message);

        if (subscribers != null && subscribers.size() > 0) {
            for (Class<Subscriber> clazz : subscribers) {
                // We only want asynchronous messages here.
                Subscribe subscripion = clazz.getDeclaredAnnotation(Subscribe.class);

                if (subscripion.run() != Run.ASYNCHRONOUSLY)
                    continue;

                Subscriber subscriber = app.inject(clazz);

                if (subscriber != null) {
                    ctx.setMessage(message);
                    MessageBusThreadPool.INSTANCE.run(subscriber, ctx);
                }
            }

            for (Class<Subscriber> clazz : subscribers) {
                // We only want synchronous messages here.
                Subscribe subscripion = clazz.getDeclaredAnnotation(Subscribe.class);

                if (subscripion.run() == Run.ASYNCHRONOUSLY)
                    continue;

                Subscriber subscriber = app.inject(clazz);

                if (subscriber != null) {
                    ctx.setMessage(message);
                    subscriber.onMessage(ctx);
                }
            }
        }
    }

    protected static final void clear() {
        registryMap().clear();
    }

    public static void cleanupThread() {
        clear();
        MESSAGE_BUS_REGISTRY_THREAD_LOCAL.remove();
    }

    private static final Map<String, Set<Class<Subscriber>>> registryMap() {
        return MESSAGE_BUS_REGISTRY_THREAD_LOCAL.get();
    }
}
