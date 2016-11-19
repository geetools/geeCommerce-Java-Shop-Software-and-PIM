package com.geecommerce.core.rest.jersey.dispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.Model;
import com.sun.jersey.api.model.AbstractModelComponent;
import com.sun.jersey.api.model.AbstractModelVisitor;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;

public class ResourceMethodWrapper extends AbstractResourceMethod {
    private final AbstractResourceMethod abstractResourceMethod;

    public ResourceMethodWrapper(AbstractResourceMethod abstractResourceMethod) {
        super(abstractResourceMethod.getResource(), abstractResourceMethod.getMethod(), abstractResourceMethod.getReturnType(), abstractResourceMethod.getGenericReturnType(),
            abstractResourceMethod.getHttpMethod(), abstractResourceMethod
                .getAnnotations());
        this.abstractResourceMethod = abstractResourceMethod;
    }

    @Override
    public AbstractResource getResource() {
        return abstractResourceMethod.getDeclaringResource();
    }

    @Override
    public Method getMethod() {
        return abstractResourceMethod.getMethod();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return abstractResourceMethod.getAnnotation(annotationType);
    }

    @Override
    public Annotation[] getAnnotations() {
        return abstractResourceMethod.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return abstractResourceMethod.getDeclaredAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return abstractResourceMethod.isAnnotationPresent(annotationType);
    }

    @Override
    public AbstractResource getDeclaringResource() {
        return abstractResourceMethod.getDeclaringResource();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Class getReturnType() {
        Class returnType = abstractResourceMethod.getReturnType();

        if (returnType.isInterface() && Model.class.isAssignableFrom(returnType)) {
            Model m = App.get().getModel(returnType);

            return m != null ? m.getClass() : returnType;
        } else {
            return returnType;
        }
    }

    @Override
    public Type getGenericReturnType() {
        return Model.class.isAssignableFrom(abstractResourceMethod.getReturnType()) ? null : abstractResourceMethod.getGenericReturnType();
    }

    @Override
    public List<MediaType> getSupportedInputTypes() {
        return abstractResourceMethod.getSupportedInputTypes();
    }

    @Override
    public void setAreInputTypesDeclared(boolean declared) {
        abstractResourceMethod.setAreInputTypesDeclared(declared);
    }

    @Override
    public boolean areInputTypesDeclared() {
        return abstractResourceMethod.areInputTypesDeclared();
    }

    @Override
    public List<MediaType> getSupportedOutputTypes() {
        return abstractResourceMethod.getSupportedOutputTypes();
    }

    @Override
    public void setAreOutputTypesDeclared(boolean declared) {
        abstractResourceMethod.setAreOutputTypesDeclared(declared);
    }

    @Override
    public boolean areOutputTypesDeclared() {
        return abstractResourceMethod.areOutputTypesDeclared();
    }

    @Override
    public String getHttpMethod() {
        return abstractResourceMethod.getHttpMethod();
    }

    @Override
    public boolean hasEntity() {
        return abstractResourceMethod.hasEntity();
    }

    @Override
    public List<Parameter> getParameters() {
        return abstractResourceMethod.getParameters();
    }

    @Override
    public void accept(AbstractModelVisitor visitor) {
        abstractResourceMethod.accept(visitor);
    }

    @Override
    public List<AbstractModelComponent> getComponents() {
        return abstractResourceMethod.getComponents();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
