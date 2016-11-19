package com.geecommerce.core.json.genson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Factory;
import com.owlike.genson.Genson;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.owlike.genson.stream.ValueType;

@Profile
public class XOptionsConverter implements Converter<ContextObject<List<Id>>> {
    public final static Factory<Converter<ContextObject<List<Id>>>> factoryInstance = new Factory<Converter<ContextObject<List<Id>>>>() {
        @Override
        public Converter<ContextObject<List<Id>>> create(Type type, Genson genson) {
            return new XOptionsConverter();
        }
    };

    @Override
    public ContextObject<List<Id>> deserialize(ObjectReader reader, Context ctx) throws IOException {
        ObjectReader array = reader.beginArray();

        ContextObject<List<Id>> ctxObj = new ContextObject<List<Id>>();

        while (array.hasNext()) {
            array.next();

            ObjectReader value = array.beginObject();

            Map<String, Object> ctxValue = new LinkedHashMap<>();

            while (value.hasNext()) {
                ValueType type = value.next();

                if (ContextObject.MERCHANT.equals(value.name())) {
                    ctxValue.put(ContextObject.MERCHANT, Id.valueOf(value.valueAsString()));
                } else if (ContextObject.STORE.equals(value.name())) {
                    ctxValue.put(ContextObject.STORE, Id.valueOf(value.valueAsString()));
                } else if (ContextObject.LANGUAGE.equals(value.name())) {
                    ctxValue.put(ContextObject.LANGUAGE, value.valueAsString());
                } else if (ContextObject.COUNTRY.equals(value.name())) {
                    ctxValue.put(ContextObject.COUNTRY, value.valueAsString());
                } else if (ContextObject.VIEW.equals(value.name())) {
                    ctxValue.put(ContextObject.VIEW, Id.valueOf(value.valueAsString()));
                } else if (ContextObject.REQUEST_CONTEXT.equals(value.name())) {
                    ctxValue.put(ContextObject.REQUEST_CONTEXT, Id.valueOf(value.valueAsString()));
                } else if (ContextObject.VALUE.equals(value.name())) {
                    List<Id> optionIds = new ArrayList<>();

                    if (type == ValueType.ARRAY) {
                        ObjectReader innerArray = value.beginArray();

                        while (innerArray.hasNext()) {
                            innerArray.next();
                            optionIds.add(Id.valueOf(innerArray.valueAsString()));
                        }

                        value.endArray();
                    } else {
                        optionIds.add(Id.valueOf(value.valueAsString()));
                    }

                    ctxValue.put(ContextObject.VALUE, optionIds);
                }
            }

            if (ctxValue.containsKey(ContextObject.VALUE))
                ctxObj.add(ctxValue);

            array.endObject();
        }

        reader.endArray();

        return ctxObj;
    }

    @Override
    public void serialize(ContextObject<List<Id>> ctxObject, ObjectWriter writer, Context ctx) throws IOException {
        if (ctxObject != null && ctxObject.size() > 0) {
            writer.beginArray();

            for (Map<String, Object> ctxMap : ctxObject) {
                Set<String> keys = ctxMap.keySet();

                writer.beginObject();

                for (String key : keys) {
                    writer.writeName(key);

                    if (key.equals(ContextObject.VALUE)) {
                        Object val = ctxMap.get(key);

                        if (val instanceof Collection) {
                            writer.beginArray();

                            for (Object listItem : (Collection<?>) val) {
                                if (listItem instanceof Number) {
                                    writer.writeValue((Number) listItem);
                                } else if (listItem instanceof Boolean) {
                                    writer.writeValue((Boolean) listItem);
                                } else {
                                    writer.writeValue(String.valueOf(listItem));
                                }
                            }

                            writer.endArray();
                        } else {
                            writer.writeValue(String.valueOf(ctxMap.get(key)));
                        }
                    } else {
                        writer.writeValue(String.valueOf(ctxMap.get(key)));
                    }
                }

                writer.endObject();
            }

            writer.endArray();
        }
    }
}
