package com.geecommerce.core.json.genson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.owlike.genson.stream.ValueType;

@Profile
public class AttributeValueConverter implements Converter<AttributeValue> {
    private static final String KEY_ATTRIBUTE_ID = "attributeId";
    private static final String KEY_ATTRIBUTE = "attribute";
    private static final String KEY_CODE = "code";
    private static final String KEY_VALUE = "value";
    private static final String KEY_OPT_OUT = "optOut";
    private static final String KEY_OPTION_IDS = "optionIds";
    private static final String KEY_OPTIONS = "options";
    private static final String KEY_XOPTION_IDS = "xOptionIds";
    private static final String KEY_SORT_ORDER = "sortOrder";
    private static final String KEY_PROPERTIES = "properties";
    private static final String KEY_ATTRIBUTE_OPTIONS = "attributeOptions";

    @SuppressWarnings("unchecked")
    @Override
    public AttributeValue deserialize(ObjectReader reader, Context ctx) throws IOException {
        AttributeValue av = App.get().getModel(AttributeValue.class);

        ContextObjectConverter ctxObjConverter = new ContextObjectConverter();

        ObjectReader obj = reader.beginObject();

        while (obj.hasNext()) {
            ValueType type = obj.next();

            if (KEY_ATTRIBUTE_ID.equals(obj.name())) {
                av.setAttributeId(Id.valueOf(obj.valueAsString()));
            } else if (KEY_SORT_ORDER.equals(obj.name())) {
                av.setSortOrder(obj.valueAsInt());
            } else if (KEY_VALUE.equals(obj.name())) {
                av.setValue(ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_OPT_OUT.equals(obj.name())) {
                av.setOptOut((ContextObject<Boolean>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_OPTION_IDS.equals(obj.name())) {
                List<Id> optionIds = new ArrayList<>();

                if (type == ValueType.ARRAY) {
                    ObjectReader array = obj.beginArray();

                    while (array.hasNext()) {
                        array.next();

                        if (!Str.isEmpty(array.valueAsString()))
                            optionIds.add(Id.valueOf(array.valueAsString()));
                    }

                    obj.endArray();
                } else if (!Str.isEmpty(obj.valueAsString())) {
                    optionIds.add(Id.valueOf(obj.valueAsString()));
                }

                av.setOptionIds(optionIds);
            } else if (KEY_XOPTION_IDS.equals(obj.name())) {
                av.setXOptionIds((ContextObject<List<Id>>) ctxObjConverter.deserialize(obj, ctx));
            } else if (KEY_PROPERTIES.equals(obj.name())) {
                Map<String, Object> properties = new HashMap<>();

                if (type == ValueType.OBJECT) {
                    ObjectReader property = obj.beginObject();

                    while (property.hasNext()) {
                        property.next();

                        ValueType valueType = property.next();

                        switch (valueType) {
                        case BOOLEAN:
                            properties.put(property.name(), property.valueAsBoolean());
                            break;
                        case DOUBLE:
                            properties.put(property.name(), property.valueAsDouble());
                            break;
                        case INTEGER:
                            properties.put(property.name(), property.valueAsInt());
                            break;
                        case STRING:
                        case ARRAY:
                        case NULL:
                        case OBJECT:
                            properties.put(property.name(), property.valueAsString());
                            break;
                        }
                    }

                    obj.endObject();
                }

                av.setProperties(properties);
            }
        }

        reader.endObject();

        return av;
    }

    @Override
    public void serialize(AttributeValue av, ObjectWriter writer, Context ctx) throws IOException {
        if (av != null) {
            ContextObjectConverter ctxObjConverter = new ContextObjectConverter();
            AttributeConverter attributeConverter = new AttributeConverter();
            AttributeOptionConverter attributeOptionConverter = new AttributeOptionConverter();

            writer.beginObject();

            if (av.getAttributeId() != null && !ConvertUtil.ignoreProperty(KEY_ATTRIBUTE_ID)) {
                writer.writeName(KEY_ATTRIBUTE_ID);
                writer.writeValue(av.getAttributeId().str());
            }

            if (av.getCode() != null && !ConvertUtil.ignoreProperty(KEY_CODE)) {
                writer.writeName(KEY_CODE);
                writer.writeValue(av.getCode());
            }

            HttpServletRequest request = App.get().getServletRequest();

            if (av.getAttributeId() != null && !ConvertUtil.ignoreProperty(KEY_ATTRIBUTE) && !request.getRequestURI().startsWith("/api/v1/products/")
                && !request.getRequestURI().startsWith("/api/v1/web/")) {
                writer.writeName(KEY_ATTRIBUTE);
                attributeConverter.serialize(av.getAttribute(), writer, ctx);
            }

            if (!ConvertUtil.ignoreProperty(KEY_SORT_ORDER)) {
                writer.writeName(KEY_SORT_ORDER);
                writer.writeValue(av.getSortOrder());
            }

            if (av.getValue() != null && !ConvertUtil.ignoreProperty(KEY_VALUE)) {
                writer.writeName(KEY_VALUE);

                if (request.getRequestURI().startsWith("/v1/web/")) {
                    Object val = av.getValue().getVal();

                    if (val instanceof Number) {
                        writer.writeNumber((Number) val);
                    } else if (val instanceof Boolean) {
                        writer.writeBoolean((Boolean) val);
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
                        writer.writeValue(String.valueOf(val));
                    }
                } else {
                    ctxObjConverter.serialize(av.getValue(), writer, ctx);
                }
            }

            if (av.getOptOut() != null && !ConvertUtil.ignoreProperty(KEY_OPT_OUT) && !request.getRequestURI().startsWith("/v1/web/")) {
                writer.writeName(KEY_OPT_OUT);
                ctxObjConverter.serialize(av.getOptOut(), writer, ctx);
            }

            if (av.getOptionIds() != null && !ConvertUtil.ignoreProperty(KEY_OPTION_IDS)) {
                if (request.getRequestURI().startsWith("/v1/web/")) {
                    writer.writeName(KEY_OPTIONS);
                    Map<Id, AttributeOption> options = av.getAttributeOptions();
                    ctx.genson.serialize(options, writer, ctx);
                } else {
                    writer.writeName(KEY_OPTION_IDS);

                    writer.beginArray();

                    for (Id optionId : av.getOptionIds()) {
                        writer.writeValue(optionId.str());
                    }

                    writer.endArray();
                }
            }

            if (av.getXOptionIds() != null && !ConvertUtil.ignoreProperty(KEY_XOPTION_IDS)) {
                writer.writeName(KEY_XOPTION_IDS);
                ctxObjConverter.serialize(av.getXOptionIds(), writer, ctx);
            }

            if (av.getProperties() != null && !ConvertUtil.ignoreProperty(KEY_PROPERTIES)) {
                writer.writeName(KEY_PROPERTIES);

                writer.beginObject();

                Map<String, Object> props = av.getProperties();

                Set<String> keys = props.keySet();

                for (String key : keys) {
                    writer.writeName(key);

                    Object val = props.get(key);

                    if (val instanceof Number) {
                        writer.writeValue((Number) val);
                    } else if (val instanceof Boolean) {
                        writer.writeValue((Boolean) val);
                    } else {
                        writer.writeValue(String.valueOf(val));
                    }
                }

                writer.endObject();
            }

            if (av.getAttributeOptions() != null && !ConvertUtil.ignoreProperty(KEY_ATTRIBUTE_OPTIONS)) {
                writer.writeName(KEY_ATTRIBUTE_OPTIONS);

                writer.beginArray();

                Map<Id, AttributeOption> attributeOptions = av.getAttributeOptions();

                Set<Id> keys = attributeOptions.keySet();

                for (Id key : keys) {
                    AttributeOption ao = attributeOptions.get(key);
                    attributeOptionConverter.serialize(ao, writer, ctx);
                }

                writer.endArray();
            }

            writer.endObject();
        }
    }
}
