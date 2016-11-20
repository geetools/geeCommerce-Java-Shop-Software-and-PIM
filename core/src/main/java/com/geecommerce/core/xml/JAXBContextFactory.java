package com.geecommerce.core.xml;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public enum JAXBContextFactory {
    INSTANCE;

    private static final Map<String, JAXBContext> instances = new ConcurrentHashMap<String, JAXBContext>();

    public JAXBContext getJaxBContext(final String contextPath) throws JAXBException {
        JAXBContext context = instances.get(contextPath);

        if (context == null) {
            context = JAXBContext.newInstance(contextPath);
            instances.put(contextPath, context);
        }

        return context;
    }

    public JAXBContext getJaxBContext(final Class<?> contextPath) throws JAXBException {
        JAXBContext context = instances.get(contextPath.getName());

        if (context == null) {
            context = JAXBContext.newInstance(contextPath);
            instances.put(contextPath.getName(), context);
        }

        return context;
    }
}
