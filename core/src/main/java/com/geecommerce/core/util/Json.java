package com.geecommerce.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.geecommerce.core.json.genson.AttributeConverter;
import com.geecommerce.core.json.genson.AttributeOptionConverter;
import com.geecommerce.core.json.genson.AttributeValueConverter;
import com.geecommerce.core.json.genson.ContextObjectConverter;
import com.geecommerce.core.json.genson.DefaultGensonBundle;
import com.geecommerce.core.json.genson.DefaultMutatorAccessorResolver;
import com.geecommerce.core.json.genson.IdConverter;
import com.geecommerce.core.json.genson.UpdateConverter;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.BeanMutatorAccessorResolver;

public class Json {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // "2014-10-14T22:00:00.000Z"

    private static final Pattern pattern = Pattern.compile("[\\d]{4}\\-[\\d]{2}\\-[\\d]{2}T[\\d]{2}\\:[\\d]{2}\\:[\\d]{2}(?:\\.[\\d]{3})?(?:[\\-+]{1}[\\d]{4})?[Z]{0,1}");

    public static final String toJson(Object o) {
        try {
            return genson().serialize(o);// .toJson(o);
        } catch (Exception e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        }

        return null;
    }

    public static final <T> T fromJson(String json, Class<T> toType) {
        try {
            return genson().deserialize(json, toType);// fromJson(json, toType);
        } catch (Exception e) {
            e.printStackTrace(); // To change body of catch statement use File |
                                 // Settings | File Templates.
        }

        return null;
    }

    public static final Genson genson() {
        Genson genson = new GensonBuilder()
            .setSkipNull(true)
            .useClassMetadata(false)
            // .setWithDebugInfoPropertyNameResolver(false)
            .useBeanViews(false)
            .withConverters(new IdConverter(), new UpdateConverter(), new AttributeValueConverter(), new AttributeOptionConverter(), new AttributeConverter())
            .withConverterFactory(ContextObjectConverter.factoryInstance)
            // .withContextualFactory(new IgnoreFieldContextualFactory())
            .with(new BeanMutatorAccessorResolver.GensonAnnotationsResolver(), new DefaultMutatorAccessorResolver(), new BeanMutatorAccessorResolver.StandardMutaAccessorResolver())
            .useDateFormat(dateFormat)
            .useDateAsTimestamp(false)
            .withBundle(new DefaultGensonBundle()).create();

        return genson;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }

        return retMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();

        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            map.put(key, value);
        }

        return map;
    }

    @SuppressWarnings("rawtypes")
    public static List toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            list.add(value);
        }

        return list;
    }
}
