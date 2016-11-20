package com.geecommerce.core.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.TypeConverter;
import com.geecommerce.core.util.NullableConcurrentHashMap;

public class Models {
    private static final String CACHE_KEY_PRELOADABLE_MODELS = "preloadable-models";

    private static final Map<String, Set<Class<? extends Model>>> preloadableModelsCache = new NullableConcurrentHashMap<>();
    private static final Map<String, Class<? extends Model>> modelMappingCache = new NullableConcurrentHashMap<>();

    public static final void populate(final Class<? extends Model> modelClass, final Model modelInstance,
        final Map<String, Object> values) {
        if (modelClass == null || values == null || values.size() == 0)
            return;

        if (!Annotations.isAutoPopulateEnabled(modelClass))
            return;

        List<ColumnInfo> columnInfos = Annotations.getColumns(modelClass);

        if (columnInfos == null || columnInfos.size() == 0)
            return;

        Set<String> keys = values.keySet();

        for (String key : keys) {
            ColumnInfo columnInfo = columnInfo(columnInfos, key);

            if (columnInfo != null && columnInfo.isAutoPopulate()) {
                if (Annotations.isFieldAccessEnabled(modelClass)) {
                    Reflect.setField(modelInstance, columnInfo.property(),
                        TypeConverter.convert(columnInfo.type(), columnInfo.genericType(), values.get(key)));
                } else {
                    Reflect.invokeSetter(modelClass, modelInstance, columnInfo.property(),
                        TypeConverter.convert(columnInfo.type(), columnInfo.genericType(), values.get(key)));
                }
            }
        }
    }

    public static final ColumnInfo columnInfo(final List<ColumnInfo> columnInfos, final String name) {
        for (ColumnInfo columnInfo : columnInfos) {
            if (columnInfo.name().equals(name)) {
                return columnInfo;
            }
        }

        return null;
    }

    public static final <T extends Model> Map<String, Object> toMap(final Class<T> modelClass, final T modelInstance) {
        Map<String, Object> values = new HashMap<String, Object>();

        if (modelClass == null || modelInstance == null)
            return values;

        List<ColumnInfo> columnInfos = Annotations.getColumns(modelClass);

        if (columnInfos == null || columnInfos.size() == 0)
            return values;

        for (ColumnInfo columnInfo : columnInfos) {
            if (columnInfo != null) {
                Object value = null;

                if (Annotations.isFieldAccessEnabled(modelClass)) {
                    value = Reflect.getFieldValue(modelInstance, columnInfo.property());
                } else {
                    value = Reflect.invokeGetter(modelClass, modelInstance, columnInfo.property());
                }

                values.put(columnInfo.name(), value);
            }
        }

        return values;
    }

    public static final String columnName(final List<ColumnInfo> columnInfos, final String propertyName) {
        if (columnInfos != null && columnInfos.size() > 0) {
            for (ColumnInfo columnInfo : columnInfos) {
                if (columnInfo.property().equals(propertyName)) {
                    return columnInfo.name();
                }
            }
        }

        return propertyName;
    }

    public static final String fieldName(final List<ColumnInfo> columnInfos, final String columnName) {
        if (columnInfos != null && columnInfos.size() > 0) {
            for (ColumnInfo columnInfo : columnInfos) {
                if (columnInfo.name().equals(columnName)) {
                    return columnInfo.property();
                }
            }
        }

        return columnName;
    }

    public static final <T extends Model> String interfaceName(Class<T> modelClass) {
        Class<T> modelInterface = interfaceType(modelClass);

        return modelInterface == null ? null : modelInterface.getName();
    }

    public static final <T extends Model> Class<T> interfaceType(Class<T> modelClass) {
        return Reflect.getModelInterface(modelClass);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends Model> Class<T> findBy(String name) {
        Class<T> modelInterface = (Class<T>) modelMappingCache.get(name);

        if (modelInterface != null) {
            return modelInterface;
        } else {
            // -----------------------------------------------------------------------
            // First get all model types.
            // -----------------------------------------------------------------------

            Set<Class<?>> types = Reflect.getTypesAnnotatedWith(com.geecommerce.core.service.annotation.Model.class,
                false);

            Class<T> foundModelType = null;
            int foundCount = 0;

            // -----------------------------------------------------------------------
            // Then try to find a simple class name with a unique match with
            // "name".
            // -----------------------------------------------------------------------

            for (Class<?> modelClass : types) {
                modelInterface = interfaceType((Class<T>) modelClass);

                if (modelInterface == null)
                    continue;

                if (name.equals(modelInterface.getSimpleName())) {
                    foundModelType = modelInterface;
                    foundCount++;
                }
            }

            // -----------------------------------------------------------------------
            // If unique model type could be found, return.
            // -----------------------------------------------------------------------

            if (foundModelType != null && foundCount == 1) {
                Class<T> cachedFoundModelType = (Class<T>) modelMappingCache.put(name, (Class<T>) foundModelType);

                if (cachedFoundModelType != null)
                    foundModelType = cachedFoundModelType;

                return (Class<T>) foundModelType;
            }

            foundCount = 0;
            foundModelType = null;

            // ---------------------------------------------------------------------------------
            // Then try to find a simple class name (lowercase) with a unique
            // match with "name".
            // ---------------------------------------------------------------------------------

            for (Class<?> modelClass : types) {
                modelInterface = interfaceType((Class<T>) modelClass);

                if (modelInterface == null)
                    continue;

                if (name.equals(modelInterface.getSimpleName().toLowerCase())) {
                    foundModelType = modelInterface;
                    foundCount++;
                }
            }

            // -----------------------------------------------------------------------
            // If unique model type could be found, return.
            // -----------------------------------------------------------------------

            if (foundModelType != null && foundCount == 1) {
                Class<T> cachedFoundModelType = (Class<T>) modelMappingCache.put(name, (Class<T>) foundModelType);

                if (cachedFoundModelType != null)
                    foundModelType = cachedFoundModelType;

                return (Class<T>) foundModelType;
            }

            foundCount = 0;
            foundModelType = null;

            // -----------------------------------------------------------------------
            // If none could be found try the FQN.
            // -----------------------------------------------------------------------

            for (Class<?> modelClass : types) {
                modelInterface = interfaceType((Class<T>) modelClass);

                if (modelInterface == null)
                    continue;

                if (name.equals(modelInterface.getName())) {
                    foundModelType = modelInterface;
                    foundCount++;
                } else if (name.equals(modelClass.getName())) {
                    foundModelType = modelInterface;
                    foundCount++;
                }
            }

            // -----------------------------------------------------------------------
            // If unique model type could be found, return.
            // -----------------------------------------------------------------------

            if (foundModelType != null && foundCount == 1) {
                Class<T> cachedFoundModelType = (Class<T>) modelMappingCache.put(name, (Class<T>) foundModelType);

                if (cachedFoundModelType != null)
                    foundModelType = cachedFoundModelType;

                return (Class<T>) foundModelType;
            }

            foundCount = 0;
            foundModelType = null;

            // -----------------------------------------------------------------------
            // If still none could be found try the annotation collection name.
            // -----------------------------------------------------------------------

            for (Class<?> modelClass : types) {
                com.geecommerce.core.service.annotation.Model modelAnno = Reflect.getDeclaredAnnotation(modelClass,
                    com.geecommerce.core.service.annotation.Model.class);

                if (modelAnno == null)
                    continue;

                if (name.equals(modelAnno.collection()) || name.equals(modelAnno.value())) {
                    modelInterface = interfaceType((Class<T>) modelClass);

                    if (modelInterface == null)
                        continue;

                    foundModelType = modelInterface;
                    foundCount++;
                }
            }

            // -----------------------------------------------------------------------
            // If unique model type could be found, return.
            // -----------------------------------------------------------------------

            if (foundModelType != null && foundCount == 1) {
                Class<T> cachedFoundModelType = (Class<T>) modelMappingCache.put(name, (Class<T>) foundModelType);

                if (cachedFoundModelType != null)
                    foundModelType = cachedFoundModelType;

                return (Class<T>) foundModelType;
            }
        }

        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T extends Model> Set<Class<T>> thatArePreloadable() {
        Set<Class<T>> preloadableModels = (Set) preloadableModelsCache.get(CACHE_KEY_PRELOADABLE_MODELS);

        if (preloadableModels != null) {
            return preloadableModels;
        } else {
            Set<Class<?>> types = Reflect.getTypesAnnotatedWith(com.geecommerce.core.service.annotation.Model.class,
                false);

            preloadableModels = new HashSet<Class<T>>();

            for (Class<?> modelClass : types) {
                Class<T> modelInterface = interfaceType((Class<T>) modelClass);

                if (modelInterface == null)
                    continue;

                com.geecommerce.core.service.annotation.Model modelAnno = Reflect.getDeclaredAnnotation(modelClass,
                    com.geecommerce.core.service.annotation.Model.class);

                if (modelAnno.preload()) {
                    preloadableModels.add(modelInterface);
                }
            }

            Set<Class<T>> cachedPreloadableModels = (Set) preloadableModelsCache.put(CACHE_KEY_PRELOADABLE_MODELS,
                (Set) preloadableModels);

            if (cachedPreloadableModels != null)
                preloadableModels = cachedPreloadableModels;
        }

        return preloadableModels;
    }
}
