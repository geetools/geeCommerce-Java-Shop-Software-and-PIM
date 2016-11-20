package com.geecommerce.core.service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.elasticsearch.annotation.Indexable;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.util.NullableConcurrentHashMap;

@Profile
public class Annotations {
    protected static final String COLUMN_CACHE_NAME = "gc/columns";
    protected static final String ANNOTATIONS_CACHE_NAME = "gc/annotations";

    protected static final String CACHEABLE_CACHE_KEY_SUFFIX = "@Cacheable";
    protected static final String MODEL_CACHE_KEY_SUFFIX = "@Model";

    protected static final String STORE_CONTEXT = "store";
    protected static final String MERCHANT_CONTEXT = "merchant";

    protected static final Map<String, Object> cache = new NullableConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static final <T extends Model> List<ColumnInfo> getColumns(Class<T> modelClass) {
        modelClass = implClass(modelClass);

        List<ColumnInfo> colInfos = (List<ColumnInfo>) cache.get(modelClass.getName());

        if (colInfos == null) {
            colInfos = new ArrayList<>();

            Map<String, Field> fields = Reflect.getFields(modelClass);

            if (fields != null && fields.size() > 0) {
                Set<String> fieldNames = fields.keySet();

                for (String fieldName : fieldNames) {
                    Field field = fields.get(fieldName);

                    if (field.isAnnotationPresent(Column.class)) {
                        Column column = field.getAnnotation(Column.class);

                        if (column != null) {
                            String columnName = Str.isEmpty(column.value()) ? column.name() : column.value();
                            colInfos.add(new ColumnInfo(columnName, field.getType(), field.getGenericType(), fieldName,
                                column.autoPopulate()));
                        }
                    }
                    // else {
                    // String columnName = columnNameFromSrc(modelClass,
                    // fieldName);
                    //
                    // if (columnName != null) {
                    // System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                    // src-columnName:: " + columnName);
                    // colInfos.add(new ColumnInfo(columnName, field.getType(),
                    // field.getGenericType(), fieldName, true));
                    // }
                    // }
                }
            }

            List<ColumnInfo> cachedColInfos = (List<ColumnInfo>) cache.putIfAbsent(modelClass.getName(), colInfos);

            if (cachedColInfos != null)
                colInfos = cachedColInfos;
        }

        return colInfos;
    }

    public static final <T extends Model> String getIndexedCollectionName(Class<T> modelClass) {
        Indexable indexable = getAnnotation(modelClass, Indexable.class);
        String collectionName = indexable.collection();
        return collectionName;
    }

    public static final <T extends Model> String getCollectionName(Class<T> modelClass) {
        com.geecommerce.core.service.annotation.Model m = getModelAnnotation(modelClass);
        String collectionName = Str.isEmpty(m.value()) ? m.collection() : m.value();
        if (StringUtils.isBlank(m.context())) {
            return collectionName;
        } else {
            List<String> contexts = Arrays.asList(m.context().split(","));
            if (contexts.contains(MERCHANT_CONTEXT)) {
                collectionName = collectionName + "_" + App.get().context().getMerchant().getId();
            }
            if (contexts.contains(STORE_CONTEXT)) {
                collectionName = collectionName + "_" + App.get().context().getStore().getId();
            }
            return collectionName;
        }
    }

    protected static String columnNameFromSrc(Class<? extends Model> modelClass, String fieldName) {
        String dbColName = null;

        try {
            String javaSrc = source(modelClass);

            // Attempt to get the
            Pattern p = Pattern.compile("this." + fieldName + "[ ]?=.*map.get\\(([^\\)]+)\\)");
            Matcher m = p.matcher(javaSrc);

            if (m.find()) {
                String columnConstant = m.group(1);

                Class<?> modelInterface = Reflect.getModelInterface((Class<? extends Model>) modelClass);
                Class<?>[] classes = modelInterface.getClasses();

                String columnFieldConstant = null;
                if (columnConstant.indexOf(Char.DOT) != -1) {
                    columnFieldConstant = columnConstant.substring(columnConstant.indexOf(Char.DOT) + 1).trim();
                } else {
                    columnFieldConstant = columnConstant;
                }

                if (columnConstant.contains("GlobalColumn")) {

                    try {
                        Object o = Class.forName("com.geecommerce.core.service.api.GlobalColumn");

                        Field f = GlobalColumn.class.getField(columnFieldConstant);
                        f.setAccessible(true);
                        dbColName = (String) f.get(o);
                    } catch (ClassNotFoundException | NoSuchFieldException | SecurityException
                        | IllegalArgumentException | IllegalAccessException e) {
                    }

                } else {
                    for (Class<?> c : classes) {
                        try {
                            Object o = c.newInstance();
                            Field f = c.getField(columnFieldConstant);
                            f.setAccessible(true);
                            dbColName = (String) f.get(o);
                        } catch (NoSuchFieldException | SecurityException | InstantiationException
                            | IllegalAccessException e) {
                        }
                    }
                }
            }

            return dbColName;

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    protected static String source(Class<? extends Model> modelClass) {
        try {
            String srcLocation = modelClass.getProtectionDomain().getCodeSource().getLocation().toString();
            String srcPath = null;

            if (srcLocation.contains("target/classes"))
                srcPath = srcLocation.replace("target/classes", "src/main/java");
            else
                srcPath = srcLocation.replace("classes", "src");

            URL url = new URL(srcPath);
            File srcFile = new File(url.getFile(), modelClass.getName().replace(Char.DOT, Char.SLASH) + ".java");

            if (srcFile.exists())
                return new String(Files.readAllBytes(srcFile.toPath()));

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static final <T extends Model> boolean isCacheable(Class<T> modelClass) {
        return getCacheableAnnotation(modelClass) != null;
    }

    public static final <T extends Model> boolean isCacheableInRepository(Class<T> modelClass) {
        Cacheable cacheable = getCacheableAnnotation(modelClass);
        if (cacheable != null)
            return cacheable.repository();
        return false;
    }

    public static final <T extends Model> boolean isReadCounterEnabled(Class<T> modelClass) {
        com.geecommerce.core.service.annotation.Model m = getModelAnnotation(modelClass);
        return m == null ? false : m.readCount();
    }

    public static final <T extends Model> boolean isAutoPopulateEnabled(Class<T> modelClass) {
        com.geecommerce.core.service.annotation.Model m = getModelAnnotation(modelClass);
        return m == null ? false : m.autoPopulate();
    }

    public static final <T extends Model> boolean isFieldAccessEnabled(Class<T> modelClass) {
        com.geecommerce.core.service.annotation.Model m = getModelAnnotation(modelClass);
        return m == null ? false : m.fieldAccess();
    }

    public static final <T extends Model> boolean isOptimisticLockingEnabled(Class<T> modelClass) {
        com.geecommerce.core.service.annotation.Model m = getModelAnnotation(modelClass);
        return m == null ? false : m.optimisticLocking();
    }

    public static final <T extends Model> boolean isHistoryEnabled(Class<T> modelClass) {
        com.geecommerce.core.service.annotation.Model m = getModelAnnotation(modelClass);
        return m == null ? false : m.history();
    }

    private static final <T extends Model> Cacheable getCacheableAnnotation(Class<T> modelClass) {
        return getAnnotation(modelClass, Cacheable.class, false);
        /*
         * modelClass = implClass(modelClass);
         * 
         * String key = new
         * StringBuilder(modelClass.getName()).append(CACHEABLE_CACHE_KEY_SUFFIX
         * ).toString();
         * 
         * Cacheable c = (Cacheable) cache.get(key);
         * 
         * if (c == null) { c = modelClass.getAnnotation(Cacheable.class);
         * 
         * Cacheable cachedAnno = (Cacheable) cache.putIfAbsent(key, c);
         * 
         * if (cachedAnno != null) c = cachedAnno; }
         * 
         * return c;
         */
    }

    private static final <T extends Model, R extends Annotation> R getAnnotation(Class<T> modelClass,
        Class<R> annotationClass) {
        return getAnnotation(modelClass, annotationClass, true);
    }

    private static final <T extends Model, R extends Annotation> R getAnnotation(Class<T> modelClass,
        Class<R> annotationClass, boolean throwErrorIfNotExists) {
        modelClass = implClass(modelClass);

        String key = new StringBuilder(modelClass.getName()).append("@" + annotationClass.getCanonicalName())
            .toString();

        R m = (R) cache.get(key);

        if (m == null) {
            m = modelClass.getAnnotation(annotationClass);
            R cachedAnno = (R) cache.putIfAbsent(key, m);

            if (cachedAnno != null)
                m = cachedAnno;
        }

        if (m == null && throwErrorIfNotExists)
            throw new IllegalStateException("The @" + annotationClass.getCanonicalName()
                + " annotation could not be found in class: " + modelClass.getName());

        return m;

    }

    private static final <T extends Model> com.geecommerce.core.service.annotation.Model getModelAnnotation(
        Class<T> modelClass) {
        return getAnnotation(modelClass, com.geecommerce.core.service.annotation.Model.class);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Model> Class<T> implClass(Class<T> modelClass) {
        if (modelClass.isInterface()) {
            Model modelObject = App.get().model(modelClass);
            modelClass = (Class<T>) modelObject.getClass();
        }

        return modelClass;
    }
}
