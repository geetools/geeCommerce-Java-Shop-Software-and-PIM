package com.geecommerce.core.json.genson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
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
public class ContextObjectConverter implements Converter<ContextObject<?>> {
    public final static Factory<Converter<ContextObject<?>>> factoryInstance = new Factory<Converter<ContextObject<?>>>() {
        @Override
        public Converter<ContextObject<?>> create(Type type, Genson genson) {
            return new ContextObjectConverter();
        }
    };

    @Override
    public ContextObject<?> deserialize(ObjectReader reader, Context ctx) throws IOException {
        ObjectReader array = reader.beginArray();

        ContextObject<Object> ctxObj = new ContextObject<Object>();

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
                    switch (type) {
                    case BOOLEAN:
                        ctxValue.put(ContextObject.VALUE, value.valueAsBoolean());
                        break;
                    case DOUBLE:
                        ctxValue.put(ContextObject.VALUE, value.valueAsDouble());
                        break;
                    case INTEGER:
                        ctxValue.put(ContextObject.VALUE, value.valueAsInt());
                        break;
                    case STRING:
                    case ARRAY:
                    case NULL:
                    case OBJECT:
                        ctxValue.put(ContextObject.VALUE, value.valueAsString());
                        break;
                    }
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
    public void serialize(ContextObject<?> ctxObject, ObjectWriter writer, Context ctx) throws IOException {
        if (ctxObject != null && ctxObject.size() > 0) {
            writer.beginArray();

            for (Map<String, Object> ctxMap : ctxObject) {
                Set<String> keys = ctxMap.keySet();

                writer.beginObject();

                for (String key : keys) {
                    writer.writeName(key);

                    if (key.equals(ContextObject.VALUE)) {
                        Object val = ctxMap.get(key);

                        if (val instanceof Number) {
                            writer.writeValue((Number) ctxMap.get(key));
                        } else if (val instanceof Boolean) {
                            writer.writeValue((Boolean) ctxMap.get(key));
                        } else if (val instanceof Collection) {
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
