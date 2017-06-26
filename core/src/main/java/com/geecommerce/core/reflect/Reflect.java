package com.geecommerce.core.reflect;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Constant;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.ChildSupport;
import com.geecommerce.core.service.PageSupport;
import com.geecommerce.core.service.ParentSupport;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.service.api.Pojo;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.type.IdSupport;
import com.geecommerce.core.type.ProductIdSupport;
import com.geecommerce.core.type.Versionable;
import com.geecommerce.core.util.NullableConcurrentHashMap;
import com.geemodule.api.ModuleClassLoader;
import com.geemvc.reflect.ReflectionsProvider;
import com.google.common.collect.Lists;

@Profile
public class Reflect {
    private static final Logger log = LogManager.getLogger(Reflect.class);

    private static final String REFLECTIONS_PROVIDER_CACHE_KEY = "reflections.provider.key";

    private static final String REFLECTIONS_CACHE_KEY = "reflections.key";

    private static final String SYSTEM_REFLECTIONS_PROVIDER_CACHE_KEY = "system.reflections.provider.key";

    private static final String SYSTEM_REFLECTIONS_CACHE_KEY = "system.reflections.key";

    private static final String MERCHANT_CLASSLOADER_CACHE_KEY = "merchant.classloader@%s";

    private static final String MODEL_INTERFACE_CACHE_KEY_SUFFIX = "@modelInterface";

    private static final String INTERFACE_CACHE_KEY_SUFFIX = "@interface";

    private static final String INTERFACES_CACHE_KEY_SUFFIX = "@interfaces";

    private static final String FIELD_COUNT_CACHE_KEY_SUFFIX = "@fieldCount";

    private static final String GETTER_METHOD_CACHE_KEY_PREFIX = "getter/";

    private static final String SETTER_METHOD_CACHE_KEY_PREFIX = "setter/";

    private static final String METHOD_HANDLE_CACHE_KEY_PREFIX = "mh/";

    private static final String ALL_FIELDS_PREFIX = "allFields@";

    private static final String TRANSIENT_SERVICE_FIELDS_PREFIX = "transServFields@";

    private static final String TYPES_ANNOTATED_WITH_PREFIX = "typesAnnotatedWith@";

    private static final String SYSTEM_TYPES_ANNOTATED_WITH_PREFIX = "systemTypesAnnotatedWith@";

    private static final String DECLARED_ANNOTATION_PREFIX = "declaredAnnotation/";

    private static final String DEFAULT_CLASS_PREFIX = "Default";

    private static final String CUSTOM_CLASS_PREFIX = "My";

    private static final String CORE_PACKAGE_PREFIX = "com.geecommerce.core.";

    private static final String METHOD_IS_PREFIX = "is";

    private static final String METHOD_SET_PREFIX = "set";

    private static final String METHOD_GET_PREFIX = "get";

    private static final String REGEX_IS_SETTER = "^set[A-Z_].*";

    private static final String REGEX_IS_GETTER = "^get[A-Z_].*";

    private static final String REGEX_IS_BOOLEAN_GETTER = "^is[A-Z_].*";

    private static final Map<String, Object> cache = new NullableConcurrentHashMap<>();

    public static final Reflections getReflections() {
        Reflections reflections = (Reflections) cache.get(REFLECTIONS_CACHE_KEY);

        App app = App.get();
        ApplicationContext appCtx = app.context();

        if (reflections == null && appCtx == null)
            return getSystemReflections();

        if (reflections == null) {
            ReflectionsProvider reflectionsProvider = getReflectionsProvider();

            reflections = reflectionsProvider.provide();

            Reflections cachedReflections = (Reflections) cache.putIfAbsent(REFLECTIONS_CACHE_KEY, reflections);

            if (cachedReflections != null)
                reflections = cachedReflections;
        }

        return reflections;
    }

    public static final Reflections getSystemReflections() {
        Reflections reflections = (Reflections) cache.get(SYSTEM_REFLECTIONS_CACHE_KEY);

        if (reflections == null) {
            ReflectionsProvider reflectionsProvider = getSystemReflectionsProvider();

            reflections = reflectionsProvider.provide();

            Reflections cachedReflections = (Reflections) cache.putIfAbsent(SYSTEM_REFLECTIONS_CACHE_KEY, reflections);

            if (cachedReflections != null)
                reflections = cachedReflections;
        }

        return reflections;
    }

    public static final ReflectionsProvider getReflectionsProvider() {
        ReflectionsProvider reflectionsProvider = (ReflectionsProvider) cache.get(REFLECTIONS_PROVIDER_CACHE_KEY);

        if (reflectionsProvider == null) {
            reflectionsProvider = new ApplicationReflectionsProvider();

            ReflectionsProvider cachedReflectionsProvider = (ReflectionsProvider) cache
                .putIfAbsent(REFLECTIONS_PROVIDER_CACHE_KEY, reflectionsProvider);

            if (cachedReflectionsProvider != null)
                reflectionsProvider = cachedReflectionsProvider;
        }

        return reflectionsProvider;
    }

    public static final ReflectionsProvider getSystemReflectionsProvider() {
        ReflectionsProvider reflectionsProvider = (ReflectionsProvider) cache
            .get(SYSTEM_REFLECTIONS_PROVIDER_CACHE_KEY);

        if (reflectionsProvider == null) {
            reflectionsProvider = new ApplicationReflectionsProvider();

            ReflectionsProvider cachedReflectionsProvider = (ReflectionsProvider) cache
                .putIfAbsent(SYSTEM_REFLECTIONS_PROVIDER_CACHE_KEY, reflectionsProvider);

            if (cachedReflectionsProvider != null)
                reflectionsProvider = cachedReflectionsProvider;
        }

        return reflectionsProvider;
    }

    public static final boolean hasCorePackagePrefix(Class<?> clazz) {
        if (clazz == null)
            return false;

        return clazz.getName().startsWith(Constant.CORE_PACKAGE_PREFIX);
    }

    public static <T extends Model> boolean isOfType(Class<T> modelClass, String fqn) {
        if (modelClass == null)
            return false;

        Class<T> clazz = getModelInterface(modelClass);

        if (clazz != null && clazz.getName().equals(fqn))
            return true;

        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(modelClass);

        if (interfaces != null && !interfaces.isEmpty()) {
            for (Class<?> interf : interfaces) {
                if (interf.getName().equals(fqn))
                    return true;
            }
        }

        return false;
    }

    public static String toProperty(Method method) {
        String name = method.getName();
        String propName = null;

        if (name.startsWith(METHOD_IS_PREFIX) && isGetter(method)) {
            propName = name.substring(2, 3).toLowerCase() + name.substring(3);
        } else if (name.startsWith(METHOD_GET_PREFIX) && isGetter(method)) {
            propName = name.substring(3, 4).toLowerCase() + name.substring(4);
        } else if (name.startsWith(METHOD_SET_PREFIX) && isSetter(method)) {
            propName = name.substring(3, 4).toLowerCase() + name.substring(4);
        }

        return propName;
    }

    public static boolean isSetter(Method method) {
        return method != null && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
            && (method.getReturnType().equals(void.class)
                || method.getReturnType().isAssignableFrom(method.getDeclaringClass()))
            && method.getParameterTypes().length == 1 && method.getName().length() > 3
            && method.getName().matches(REGEX_IS_SETTER);
    }

    public static boolean isGetter(Method method) {
        return method != null && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
            && !method.getReturnType().equals(void.class) && method.getParameterTypes().length == 0
            && ((boolean.class.equals(method.getReturnType()) && method.getName().length() > 2
                && method.getName().matches(REGEX_IS_BOOLEAN_GETTER))
                || (!boolean.class.equals(method.getReturnType()) && method.getName().length() > 3
                    && method.getName().matches(REGEX_IS_GETTER)));
    }

    public static String toGetter(Field field) {
        String name = field.getName();
        Class<?> type = field.getType();

        String getterName = null;

        if (type.isPrimitive() && type == boolean.class) {
            getterName = new StringBuilder(METHOD_IS_PREFIX).append(name.substring(0, 1).toUpperCase())
                .append(name.substring(1)).toString();
        } else {
            getterName = new StringBuilder(METHOD_GET_PREFIX).append(name.substring(0, 1).toUpperCase())
                .append(name.substring(1)).toString();
        }

        return getterName;
    }

    public static String toSetter(Field field) {
        String name = field.getName();

        return new StringBuilder(METHOD_SET_PREFIX).append(name.substring(0, 1).toUpperCase()).append(name.substring(1))
            .toString();
    }

    public static <T extends Model> MethodHandle getGetter(Class<T> clazz, String propertyName) {
        if (clazz == null || propertyName == null)
            throw new NullPointerException(
                "The parameters 'clazz' and 'propertyName' cannot be null when looking for getter method [clazz="
                    + clazz + ", propertyName=" + propertyName + "]");

        String key = new StringBuilder(GETTER_METHOD_CACHE_KEY_PREFIX).append(clazz.getName()).append(Char.DOT)
            .append(propertyName).toString();

        MethodHandle mh = (MethodHandle) cache.get(key);

        if (mh == null) {
            String getterName = null;

            try {
                Lookup lookup = MethodHandles.lookup();

                Field field = getField(clazz, propertyName);

                getterName = toGetter(field);

                try {
                    Method m = clazz.getMethod(getterName, (Class<?>[]) null);

                    if (isGetter(m)) {
                        m.setAccessible(true);
                        mh = lookup.unreflect(m);
                    }
                } catch (Throwable t) {

                }

                // If the initial lookup did not help, attempt looking up all
                // the property descriptions.
                if (mh == null) {
                    PropertyDescriptor[] propertyDecriptor = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

                    for (PropertyDescriptor pd : propertyDecriptor) {
                        if (pd.getName().equals(propertyName) && pd.getPropertyType().equals(field.getType())) {
                            Method m = pd.getReadMethod();
                            m.setAccessible(true);
                            mh = lookup.unreflect(m);
                        }
                    }
                }

                if (mh == null) {

                    // TODO: check this ...
                    // throw new NoSuchMethodException("Method '" + getterName +
                    // "' not found for [class=" + clazz +
                    // ", propertyName=" + propertyName + "]");
                } else {
                    mh = mh.asType(mh.type().changeParameterType(0, Object.class).changeReturnType(Object.class));
                }
            } catch (Throwable t) {
                throw new RuntimeException("An error occured while invoking getter '" + getterName + "' for [class="
                    + clazz + ", propertyName=" + propertyName + "]", t);
            }

            MethodHandle cachedMethodHandle = (MethodHandle) cache.putIfAbsent(key, mh);

            if (cachedMethodHandle != null)
                mh = cachedMethodHandle;
        }

        return mh;
    }

    public static <T extends Model> MethodHandle getMethodHandle(Class<T> clazz, String methodName) {
        return getMethodHandle(clazz, methodName, false);
    }

    public static <T extends Model> MethodHandle getMethodHandle(Class<T> clazz, String methodName, boolean declared) {
        if (clazz == null || methodName == null)
            throw new NullPointerException("The parameters 'clazz' and 'methodName' cannot be null [class=" + clazz
                + ", methodName=" + methodName + "]");

        String key = new StringBuilder(METHOD_HANDLE_CACHE_KEY_PREFIX).append(clazz.getName()).append(Char.DOT)
            .append(methodName).append(Char.QUESTION_MARK).append(declared).toString();

        MethodHandle mh = (MethodHandle) cache.get(key);

        if (mh == null) {
            try {
                Lookup lookup = MethodHandles.lookup();

                try {
                    Method m = null;

                    if (declared) {
                        m = clazz.getDeclaredMethod(methodName, (Class<?>[]) null);
                    } else {
                        m = clazz.getMethod(methodName, (Class<?>[]) null);
                    }

                    m.setAccessible(true);
                    mh = lookup.unreflect(m);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                if (mh != null) {
                    mh = mh.asType(mh.type().changeParameterType(0, Object.class).changeReturnType(Object.class));
                }
            } catch (Throwable t) {
                throw new RuntimeException("An error occured while getting method-handle for [class=" + clazz
                    + ", methodName=" + methodName + "]", t);
            }

            MethodHandle cachedMethodHandle = (MethodHandle) cache.putIfAbsent(key, mh);

            if (cachedMethodHandle != null)
                mh = cachedMethodHandle;
        }

        return mh;
    }

    public static <T extends Model> MethodHandle getSetter(Class<T> clazz, String propertyName) {
        if (clazz == null || propertyName == null)
            throw new NullPointerException(
                "The parameters 'clazz' and 'propertyName' cannot be null when looking for setter method [clazz="
                    + clazz + ", propertyName=" + propertyName + "]");

        String key = new StringBuilder(SETTER_METHOD_CACHE_KEY_PREFIX).append(clazz.getName()).append('.')
            .append(propertyName).toString();

        MethodHandle mh = (MethodHandle) cache.get(key);

        if (mh == null) {
            String setterName = null;

            try {
                Lookup lookup = MethodHandles.lookup();

                Field field = getField(clazz, propertyName);

                setterName = toSetter(field);

                try {
                    Method m = clazz.getMethod(setterName, field.getType());

                    if (isSetter(m)) {
                        m.setAccessible(true);
                        mh = lookup.unreflect(m);
                    }
                } catch (Throwable t) {

                }

                // If the initial lookup did not help, attempt looking up all
                // the property descriptions.
                if (mh == null) {
                    PropertyDescriptor[] propertyDecriptor = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

                    for (PropertyDescriptor pd : propertyDecriptor) {
                        if (pd.getName().equals(propertyName) && pd.getPropertyType().equals(field.getType())) {
                            Method m = pd.getWriteMethod();

                            if (isSetter(m)) {
                                m.setAccessible(true);
                                mh = lookup.unreflect(m);
                            }
                        }
                    }
                }

                if (mh == null) {
                    throw new NoSuchMethodException("Method '" + setterName + "' not found for [class=" + clazz
                        + ", propertyName=" + propertyName + "]");
                } else {
                    mh = mh.asType(mh.type().changeParameterType(0, Object.class).changeParameterType(1, Object.class)
                        .changeReturnType(Object.class));
                }
            } catch (Throwable t) {
                throw new RuntimeException("An error occured while invoking setter '" + setterName + "' for [class="
                    + clazz + ", propertyName=" + propertyName + "]", t);
            }

            MethodHandle cachedMethodHandle = (MethodHandle) cache.putIfAbsent(key, mh);

            if (cachedMethodHandle != null)
                mh = cachedMethodHandle;
        }

        return mh;
    }

    public static <T extends Model> Object invokeGetter(Class<T> clazz, Object instance, String propertyName) {
        MethodHandle mh = getGetter(clazz, propertyName);

        Object value = null;

        if (mh != null) {
            try {
                value = mh.invokeExact(instance);
            } catch (Throwable t) {
                throw new RuntimeException(t.getMessage(), t);
            }
        }

        return value;
    }

    public static <T extends Model> void invokeSetter(Class<T> clazz, Object instance, String propertyName,
        Object value) {
        if (clazz == null || instance == null || propertyName == null)
            throw new NullPointerException(
                "The parameters 'clazz', 'instance' and 'propertyName' cannot be null when invoking setter [clazz="
                    + clazz + ", instance=" + instance + ", propertyName=" + propertyName + "]");

        MethodHandle mh = null;

        try {
            mh = getSetter(clazz, propertyName);
        } catch (Throwable t) {

        }

        if (mh != null) {
            try {
                value = mh.invokeExact(instance, value);
            } catch (Throwable t) {
                throw new RuntimeException(t.getMessage(), t);
            }
        } else {
            setField(instance, propertyName, value);
        }
    }

    public static void setField(Object instance, String propertyName, Object value) {
        if (instance == null || propertyName == null)
            throw new NullPointerException(
                "The parameters 'instance' and 'propertyName' cannot be null when when setting field [instance="
                    + instance + ", propertyName=" + propertyName + "]");

        Class<?> clazz = null;

        try {
            clazz = instance.getClass();

            if (Model.class.isAssignableFrom(clazz) || Pojo.class.isAssignableFrom(clazz)) {
                while (clazz != null && clazz != Object.class) {
                    try {
                        Field field = clazz.getDeclaredField(propertyName);
                        field.setAccessible(true);
                        field.set(instance, value);
                    } catch (Throwable t) {
                    }

                    clazz = clazz.getSuperclass();

                    if (clazz == Object.class)
                        break;
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("An error occured while setting field '" + propertyName + "' for [class=" + clazz
                + ", instance=" + instance + ", propertyName=" + propertyName + ", value=" + value + "]", t);
        }
    }

    public static final boolean fieldExists(String name, Class<?> inClass) {
        return fieldExists(name, inClass, true);
    }

    public static final boolean fieldExists(String name, Class<?> inClass, boolean honourInherited) {
        try {
            while (inClass != null && inClass != Object.class) {
                try {
                    Field field = inClass.getDeclaredField(name);

                    if (field != null)
                        return true;
                } catch (Throwable t) {
                }

                inClass = inClass.getSuperclass();

                if (inClass == Object.class)
                    break;
            }
        } catch (Throwable t) {
        }

        return false;
    }

    public static Object getFieldValue(Object instance, String propertyName) {
        if (instance == null || propertyName == null)
            throw new NullPointerException(
                "The parameters 'instance' and 'propertyName' cannot be null when when getting field [instance="
                    + instance + ", propertyName=" + propertyName + "]");

        Object value = null;
        Class<?> clazz = null;

        try {
            clazz = instance.getClass();

            if (Model.class.isAssignableFrom(clazz) || Pojo.class.isAssignableFrom(clazz)) {
                while (clazz != null && clazz != Object.class) {
                    try {
                        Field field = clazz.getDeclaredField(propertyName);
                        field.setAccessible(true);
                        value = field.get(instance);
                    } catch (Throwable t) {
                    }

                    clazz = clazz.getSuperclass();

                    if (clazz == Object.class)
                        break;
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("An error occured while getting field value '" + propertyName + "' for [class="
                + clazz + ", instance=" + instance + ", propertyName=" + propertyName + "]", t);
        }

        return value;
    }

    public static int getFieldCount(Class<? extends Model> clazz) {
        String key = new StringBuilder(clazz.getName()).append(FIELD_COUNT_CACHE_KEY_SUFFIX).toString();

        Integer numFields = (Integer) cache.get(key);

        if (numFields == null) {
            clazz = implClass(clazz);

            Map<String, Field> fields = getFields(clazz);

            numFields = Integer.valueOf(fields.size());

            Integer cachedFieldCount = (Integer) cache.putIfAbsent(key, numFields);

            if (cachedFieldCount != null)
                numFields = cachedFieldCount;
        }

        return numFields;
    }

    public static final <T extends Model> Field getField(Class<T> clazz, String propertyName) {
        if (clazz == null || propertyName == null)
            return null;

        Map<String, Field> fields = getFields(clazz);

        return fields == null ? null : fields.get(propertyName);
    }

    public static final <T extends Model> Map<String, Field> getFields(Class<T> clazz) {
        return getFields(clazz, true);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Model> Map<String, Field> getFields(Class<T> clazz,
        boolean writeableBeanPropertiesOnly) {
        clazz = implClass(clazz);

        String key = new StringBuilder(ALL_FIELDS_PREFIX).append(clazz.getName()).append(Char.SLASH)
            .append(String.valueOf(writeableBeanPropertiesOnly)).toString();

        Map<String, Field> allFields = (Map<String, Field>) cache.get(key);

        if (allFields == null) {
            allFields = new HashMap<String, Field>();

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (writeableBeanPropertiesOnly) {
                    if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())
                        && !isSeReHe(field.getType())) {
                        allFields.put(field.getName(), field);
                    }
                } else {
                    allFields.put(field.getName(), field);
                }
            }

            Class<?> currentClass = clazz;
            Class<?> superClass = null;
            while ((superClass = currentClass.getSuperclass()) != null
                && !currentClass.getSuperclass().equals(Object.class)) {
                fields = superClass.getDeclaredFields();

                for (Field field : fields) {
                    if (writeableBeanPropertiesOnly) {
                        if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())
                            && !isSeReHe(field.getType())) {
                            allFields.put(field.getName(), field);
                        }
                    } else {
                        allFields.put(field.getName(), field);
                    }
                }

                currentClass = superClass;
            }

            Map<String, Field> cachedAllFields = (Map<String, Field>) cache.putIfAbsent(key, allFields);

            if (cachedAllFields != null)
                allFields = cachedAllFields;
        }

        return allFields;
    }

    @SuppressWarnings("unchecked")
    public static final Map<String, Field> getTransientServiceFields(Class<?> clazz) {
        String key = new StringBuilder(TRANSIENT_SERVICE_FIELDS_PREFIX).append(clazz.getName()).toString();

        Map<String, Field> transientServiceFields = (Map<String, Field>) cache.get(key);

        if (transientServiceFields == null) {
            transientServiceFields = new HashMap<String, Field>();

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers()) && Modifier.isTransient(field.getModifiers())
                    && isSeReHe(field.getType())) {
                    transientServiceFields.put(field.getName(), field);
                }
            }

            Class<?> currentClass = clazz;
            Class<?> superClass = null;
            while ((superClass = currentClass.getSuperclass()) != null
                && !currentClass.getSuperclass().equals(Object.class)) {
                fields = superClass.getDeclaredFields();

                for (Field field : fields) {
                    if (!Modifier.isStatic(field.getModifiers()) && Modifier.isTransient(field.getModifiers())
                        && isSeReHe(field.getType())) {
                        transientServiceFields.put(field.getName(), field);
                    }
                }

                currentClass = superClass;
            }

            Map<String, Field> cachedTransientServiceFields = (Map<String, Field>) cache.putIfAbsent(key,
                transientServiceFields);

            if (cachedTransientServiceFields != null)
                transientServiceFields = cachedTransientServiceFields;
        }

        return transientServiceFields;
    }

    @SuppressWarnings("unchecked")
    public static final Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation,
        boolean honorInherited) {
        String key = new StringBuilder(TYPES_ANNOTATED_WITH_PREFIX).append(annotation.getName()).append(Char.SLASH)
            .append(String.valueOf(honorInherited)).toString();

        Set<Class<?>> annotatedTypes = (Set<Class<?>>) cache.get(key);

        if (annotatedTypes == null) {
            annotatedTypes = getReflections().getTypesAnnotatedWith(annotation, false);
            Set<Class<?>> cachedAnnotatedTypes = (Set<Class<?>>) cache.putIfAbsent(key, annotatedTypes);

            if (cachedAnnotatedTypes != null)
                annotatedTypes = cachedAnnotatedTypes;
        }

        return annotatedTypes;
    }

    @SuppressWarnings("unchecked")
    public static final Set<Class<?>> getSystemTypesAnnotatedWith(final Class<? extends Annotation> annotation,
        boolean honorInherited) {
        String key = new StringBuilder(SYSTEM_TYPES_ANNOTATED_WITH_PREFIX).append(annotation.getName())
            .append(Char.SLASH).append(String.valueOf(honorInherited)).toString();

        Set<Class<?>> annotatedTypes = (Set<Class<?>>) cache.get(key);

        if (annotatedTypes == null) {
            annotatedTypes = getSystemReflections().getTypesAnnotatedWith(annotation, false);
            Set<Class<?>> cachedAnnotatedTypes = (Set<Class<?>>) cache.putIfAbsent(key, annotatedTypes);

            if (cachedAnnotatedTypes != null)
                annotatedTypes = cachedAnnotatedTypes;
        }

        return annotatedTypes;
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    public static <A extends Annotation> A getDeclaredAnnotation(Class<?> annotatedClass, Class<A> annotationClass) {
        if (annotatedClass == null || annotationClass == null)
            throw new NullPointerException();

        String key = new StringBuilder(DECLARED_ANNOTATION_PREFIX).append(annotationClass.getName()).append(Char.AT)
            .append(annotatedClass.getName()).toString();

        Annotation declaredAnnotation = (Annotation) cache.get(key);

        if (declaredAnnotation == null) {
            // First try standard way of finding declared annotation.
            declaredAnnotation = annotatedClass.getDeclaredAnnotation(annotationClass);

            Class<? extends Annotation> annoClass = null;

            if (declaredAnnotation == null) {
                try {
                    // Make sure that we are using the same ClassLoader as the
                    // declaring class for the annotation type
                    // we are looking for.
                    annoClass = (Class<A>) annotatedClass.getClassLoader().loadClass(annotationClass.getName());
                } catch (ClassNotFoundException e) {
                }

                declaredAnnotation = annotatedClass.getDeclaredAnnotation(annoClass);
            }

            Set<Class<?>> interfaces = new HashSet<>();

            // if no annotation has been found yet and fromClass exists, we try
            // to find annotation in super classes.
            while (annotatedClass != null && annotatedClass != Object.class && declaredAnnotation == null) {
                try {
                    interfaces.addAll(Arrays.asList(annotatedClass.getInterfaces()));

                    // Make sure that we are using the same ClassLoader as the
                    // method's declaring class.
                    annoClass = (Class<A>) annotatedClass.getClassLoader().loadClass(annotationClass.getName());

                    declaredAnnotation = annotatedClass.getDeclaredAnnotation(annoClass);

                    if (declaredAnnotation != null)
                        break;
                } catch (Throwable t) {
                }

                annotatedClass = annotatedClass.getSuperclass();

                if (annotatedClass == Object.class)
                    break;
            }

            // If we could not find the annotation in the implementation
            // classes, we try the interfaces.
            if (declaredAnnotation == null) {
                for (Class<?> interf : interfaces) {
                    try {
                        // Make sure that we are using the same ClassLoader as
                        // the method's declaring class.
                        annoClass = (Class<A>) interf.getClassLoader().loadClass(annotationClass.getName());

                        declaredAnnotation = interf.getDeclaredAnnotation(annoClass);

                        if (declaredAnnotation != null)
                            break;
                    } catch (Throwable t) {
                    }
                }
            }

            Annotation cachedDeclaredAnnotation = (Annotation) cache.putIfAbsent(key, declaredAnnotation);

            if (cachedDeclaredAnnotation != null)
                declaredAnnotation = cachedDeclaredAnnotation;
        }

        return (A) declaredAnnotation;
    }

    @SuppressWarnings("hiding")
    public static <A extends Annotation> A getDeclaredAnnotation(Method method, Class<A> annotationClass) {
        return getDeclaredAnnotation(method, null, annotationClass);
    }

    @SuppressWarnings({ "unchecked", "hiding" })
    public static <A extends Annotation> A getDeclaredAnnotation(Method method, Class<?> fromClass,
        Class<A> annotationClass) {
        if (method == null || annotationClass == null)
            return null;

        String key = new StringBuilder(DECLARED_ANNOTATION_PREFIX)
            .append(fromClass == null ? method.getDeclaringClass().getName() : fromClass.getName()).append(Char.DOT)
            .append(method.getName()).append(Char.AT).append(annotationClass.getName()).toString();

        Annotation declaredAnnotation = (Annotation) cache.get(key);

        if (declaredAnnotation == null) {
            // First try standard way of finding declared annotation.
            declaredAnnotation = method.getDeclaredAnnotation(annotationClass);

            Class<? extends Annotation> annoClass = null;

            if (declaredAnnotation == null) {
                try {
                    // Make sure that we are using the same ClassLoader as the
                    // declaring class for the annotation type
                    // we are looking for.
                    annoClass = (Class<A>) method.getDeclaringClass().getClassLoader()
                        .loadClass(annotationClass.getName());
                } catch (ClassNotFoundException e) {
                }

                declaredAnnotation = method.getDeclaredAnnotation(annoClass);
            }

            Set<Class<?>> interfaces = new HashSet<>();

            // if no annotation has been found yet and fromClass exists, we try
            // to find annotation in super classes.
            while (fromClass != null && fromClass != Object.class && declaredAnnotation == null) {
                try {
                    interfaces.addAll(Arrays.asList(fromClass.getInterfaces()));

                    // Make sure that we are using the same ClassLoader as the
                    // method's declaring class.
                    annoClass = (Class<A>) fromClass.getClassLoader().loadClass(annotationClass.getName());

                    // Find same method in super class.
                    method = fromClass.getMethod(method.getName(), method.getParameterTypes());

                    declaredAnnotation = method.getDeclaredAnnotation(annoClass);

                    if (declaredAnnotation != null)
                        break;
                } catch (Throwable t) {
                }

                fromClass = fromClass.getSuperclass();

                if (fromClass == Object.class)
                    break;
            }

            // If we could not find the annotation in the implementation
            // classes, we try the interfaces.
            if (declaredAnnotation == null) {
                for (Class<?> interf : interfaces) {
                    try {
                        // Make sure that we are using the same ClassLoader as
                        // the method's declaring class.
                        annoClass = (Class<A>) interf.getClassLoader().loadClass(annotationClass.getName());

                        // Find same method in super class.
                        method = interf.getMethod(method.getName(), method.getParameterTypes());

                        declaredAnnotation = method.getDeclaredAnnotation(annoClass);

                        if (declaredAnnotation != null)
                            break;
                    } catch (Throwable t) {
                    }
                }
            }

            Annotation cachedDeclaredAnnotation = (Annotation) cache.putIfAbsent(key, declaredAnnotation);

            if (cachedDeclaredAnnotation != null)
                declaredAnnotation = cachedDeclaredAnnotation;
        }

        return (A) declaredAnnotation;
    }

    public static boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass) {
        if (method == null || annotationClass == null)
            return false;

        return getDeclaredAnnotation(method, null, annotationClass) != null;
    }

    public static boolean isAnnotationPresent(Method method, Class<?> fromClass,
        Class<? extends Annotation> annotationClass) {
        if (method == null || annotationClass == null)
            return false;

        return getDeclaredAnnotation(method, fromClass, annotationClass) != null;
    }

    public static final boolean isSeReHe(Class<?> clazz) {
        return clazz != null && (Service.class.isAssignableFrom(clazz) || Repository.class.isAssignableFrom(clazz)
            || Helper.class.isAssignableFrom(clazz));
    }

    public static List<Class<?>> getGenericType(Type genericType) {
        List<Class<?>> ret = null;

        if (genericType instanceof ParameterizedType) {
            Type[] argTypes = ((ParameterizedType) genericType).getActualTypeArguments();

            if (argTypes != null && argTypes.length > 0) {
                ret = new ArrayList<>();

                for (Type argType : argTypes) {
                    if (argType instanceof Class<?>) {
                        ret.add((Class<?>) argType);
                    } else if (argType instanceof ParameterizedType) {
                        ret.add((Class<?>) ((ParameterizedType) argType).getRawType());

                        Type[] nestedArgTypes = ((ParameterizedType) argType).getActualTypeArguments();

                        for (Type nestedArgType : nestedArgTypes) {
                            if (nestedArgType instanceof Class<?>) {
                                ret.add((Class<?>) nestedArgType);
                            } else if (nestedArgType instanceof ParameterizedType) {
                                ret.add((Class<?>) ((ParameterizedType) nestedArgType).getRawType());
                            }
                        }
                    }
                }
            }
        }

        return ret;
    }

    public static <T> T newInstance(Class<T> type) throws InstantiationException, IllegalAccessException {
        return type.newInstance();
    }

    // Exclude global interfaces.
    protected static List<Class<?>> interfaceBlackList = Lists.newArrayList(Model.class, MultiContextModel.class,
        IdSupport.class, ProductIdSupport.class, Versionable.class, Serializable.class, AttributeSupport.class,
        ParentSupport.class, ChildSupport.class, PageSupport.class, TargetSupport.class);

    public static Class<?> unwrap(Class<?> clazz) {
        boolean isEnhanced = clazz.getName().contains("EnhancerByGuice");
        return isEnhanced ? clazz.getSuperclass() : clazz;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> Class<T> getModelInterface(Class<T> clazz) {
        if (clazz == null)
            return null;

        clazz = (Class<T>) unwrap(clazz);

        if (clazz.isInterface() && !interfaceBlackList.contains(clazz.getClass()))
            return clazz;

        String className = clazz.getName();

        String key = new StringBuilder(className).append(MODEL_INTERFACE_CACHE_KEY_SUFFIX).toString();

        Class<?> foundInterface = (Class<?>) cache.get(key);

        if (foundInterface == null) {
            Class<?>[] interfaces = clazz.getInterfaces();

            if (interfaces != null && interfaces.length > 0) {
                // Try exact match first.
                for (Class<?> interf : interfaces) {
                    String interfaceName = interf.getName();
                    String defaultClassName = className.replace(DEFAULT_CLASS_PREFIX, Str.EMPTY);

                    if (defaultClassName.equals(interfaceName) && Model.class.isAssignableFrom(interf)) {
                        foundInterface = interf;
                        Class<?> cachedFoundInterface = (Class<?>) cache.putIfAbsent(key, foundInterface);

                        if (cachedFoundInterface != null)
                            foundInterface = cachedFoundInterface;

                        break;
                    }
                }

                // If we don't find anything, try custom class.
                if (foundInterface == null) {
                    for (Class<?> interf : interfaces) {
                        String interfaceName = interf.getName();
                        String customClassName = className.replace(CUSTOM_CLASS_PREFIX, Str.EMPTY);

                        if (customClassName.equals(interfaceName) && Model.class.isAssignableFrom(interf)) {
                            foundInterface = interf;
                            Class<?> cachedFoundInterface = (Class<?>) cache.putIfAbsent(key, foundInterface);

                            if (cachedFoundInterface != null)
                                foundInterface = cachedFoundInterface;

                            break;
                        }
                    }
                }

                // If we still don't find anything, try a close match.
                if (foundInterface == null) {
                    for (Class<?> interf : interfaces) {
                        String interfaceName = interf.getSimpleName();

                        if (className.endsWith(interfaceName) && Model.class.isAssignableFrom(interf)) {
                            foundInterface = interf;
                            Class<?> cachedFoundInterface = (Class<?>) cache.putIfAbsent(key, foundInterface);

                            if (cachedFoundInterface != null)
                                foundInterface = cachedFoundInterface;

                            break;
                        }
                    }
                }
            }
        }

        return (Class<T>) foundInterface;
    }

    public static Class<?> getInterface(Class<?> clazz) {
        if (clazz == null)
            return null;

        if (!Dao.class.isAssignableFrom(clazz) && !Helper.class.isAssignableFrom(clazz)
            && !Model.class.isAssignableFrom(clazz) && !Pojo.class.isAssignableFrom(clazz)
            && !Repository.class.isAssignableFrom(clazz) && !Service.class.isAssignableFrom(clazz)) {
            return null;
        }

        String key = new StringBuilder(clazz.getName()).append(INTERFACE_CACHE_KEY_SUFFIX).toString();

        String className = clazz.getName();

        Class<?> foundInterface = (Class<?>) cache.get(key);

        if (foundInterface == null) {
            Class<?>[] interfaces = clazz.getInterfaces();

            if (interfaces != null && interfaces.length > 0) {
                // Try exact match first.
                for (Class<?> interf : interfaces) {
                    String interfaceName = interf.getName();
                    String strippedClassName = className.replace(DEFAULT_CLASS_PREFIX, Str.EMPTY);

                    if (strippedClassName.equals(interfaceName)) {
                        foundInterface = interf;
                        Class<?> cachedFoundInterface = (Class<?>) cache.putIfAbsent(key, foundInterface);

                        if (cachedFoundInterface != null)
                            foundInterface = cachedFoundInterface;

                        break;
                    }
                }

                // If we don't find anything, try a close match.
                if (foundInterface == null) {
                    for (Class<?> interf : interfaces) {
                        String interfaceName = interf.getSimpleName();

                        if (className.endsWith(interfaceName)) {
                            foundInterface = interf;
                            Class<?> cachedFoundInterface = (Class<?>) cache.putIfAbsent(key, foundInterface);

                            if (cachedFoundInterface != null)
                                foundInterface = cachedFoundInterface;

                            break;
                        }
                    }
                }
            }
        }

        return foundInterface;
    }

    public static Set<Class<?>> getInterfaces(Class<?> clazz) {
        return getInterfaces(clazz, false);
    }

    @SuppressWarnings("unchecked")
    public static Set<Class<?>> getInterfaces(Class<?> clazz, boolean includeSubTypes) {
        if (clazz == null)
            return null;

        String key = new StringBuilder(clazz.getName()).append(INTERFACES_CACHE_KEY_SUFFIX).toString();

        Set<Class<?>> foundInterfaces = (Set<Class<?>>) cache.get(key);

        if (foundInterfaces == null) {
            foundInterfaces = new LinkedHashSet<Class<?>>();

            // We only want the interface of specific types here.
            if (!Dao.class.isAssignableFrom(clazz) && !Helper.class.isAssignableFrom(clazz)
                && !Model.class.isAssignableFrom(clazz) && !Pojo.class.isAssignableFrom(clazz)
                && !Repository.class.isAssignableFrom(clazz) && !Service.class.isAssignableFrom(clazz)) {
                return foundInterfaces;
            }

            Set<Class<?>> checkInterfaces = new LinkedHashSet<Class<?>>();

            // Get all the interfaces (including inherited ones) starting at the
            // sub-types if desired.
            if (includeSubTypes) {
                Set<?> subTypes = getReflections().getSubTypesOf(clazz);

                if (subTypes != null && subTypes.size() > 0) {
                    for (Object subType : subTypes) {
                        checkInterfaces.addAll(ClassUtils.getAllInterfaces((Class<?>) subType));
                    }
                }
                // If there are no sub-types, we are already at the top-level.
                else {
                    checkInterfaces.addAll(ClassUtils.getAllInterfaces(clazz));
                }
            }
            // Get all the interfaces (including inherited ones) from the passed
            // in class.
            else {
                checkInterfaces.addAll(ClassUtils.getAllInterfaces(clazz));
            }

            // Now check all the interfaces to make sure that we only return
            // particular types and no core classes.
            if (checkInterfaces != null && checkInterfaces.size() > 0) {
                // Try exact match first.
                for (Class<?> interf : checkInterfaces) {
                    if (isCoreClass(interf))
                        continue;

                    if (Dao.class.isAssignableFrom(interf) || Helper.class.isAssignableFrom(interf)
                        || Model.class.isAssignableFrom(interf) || Pojo.class.isAssignableFrom(interf)
                        || Repository.class.isAssignableFrom(interf) || Service.class.isAssignableFrom(interf)) {
                        foundInterfaces.add(interf);
                    }
                }
            }

            Set<Class<?>> cachedFoundInterfaces = (Set<Class<?>>) cache.putIfAbsent(key, foundInterfaces);

            if (cachedFoundInterfaces != null)
                foundInterfaces = cachedFoundInterfaces;
        }

        return foundInterfaces;
    }

    public static final boolean isCoreClass(Class<?> clazz) {
        if (clazz == null)
            return false;

        return clazz.getName().startsWith(CORE_PACKAGE_PREFIX);
    }

    public static final <T extends Model> String getModelVarName(Class<T> clazz) {
        if (clazz == null)
            return null;

        String varName = null;

        Class<T> modelInterface = getModelInterface(clazz);

        if (modelInterface != null && modelInterface.isInterface()) {
            String simpleName = modelInterface.getSimpleName();
            varName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        }

        return varName;
    }

    public static final String getTypeVarName(Class<?> clazz) {
        if (clazz == null)
            return null;

        String simpleName = clazz.getSimpleName();

        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    public static ClassLoader getModuleClassLoader(Class<?> clazz) {
        if (clazz == null)
            return null;

        ClassLoader classLoader = clazz.getClassLoader();

        if (classLoader instanceof ModuleClassLoader)
            return classLoader;

        while (clazz != Object.class && !(classLoader instanceof ModuleClassLoader)) {
            clazz = clazz.getSuperclass();

            if (clazz == null || clazz == Object.class)
                break;

            classLoader = clazz.getClassLoader();
        }

        return classLoader instanceof ModuleClassLoader ? classLoader : null;
    }

    public static ClassLoader getMerchantClassLoader() throws MalformedURLException {
        App app = App.get();
        ApplicationContext appCtx = app.context();

        if (appCtx != null) {
            Merchant m = appCtx.getMerchant();
            String cacheKey = String.format(MERCHANT_CLASSLOADER_CACHE_KEY, m.getId());

            MerchantClassLoader mc = (MerchantClassLoader) cache.get(cacheKey);

            if (mc == null) {
                mc = new MerchantClassLoader(m);

                MerchantClassLoader cached = (MerchantClassLoader) cache.putIfAbsent(cacheKey, mc);

                if (cached != null)
                    mc = cached;
            }

            return mc;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Model> Class<T> implClass(Class<T> modelClass) {
        if (modelClass.isInterface()) {
            App app = App.get();
            Model modelObject = app.model(modelClass);
            modelClass = (Class<T>) modelObject.getClass();
        }

        return modelClass;
    }
}