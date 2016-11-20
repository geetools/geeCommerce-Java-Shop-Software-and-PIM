package com.geecommerce.core.json.genson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geecommerce.core.Str;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.pojo.Update.UpdateMap;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.owlike.genson.stream.ValueType;

@Profile
public class UpdateConverter implements Converter<Update> {
    private static final String FIELD_PREFIX_CONTEXT_OBJECT = "ctxObj:";
    private static final String KEY_ID = "_id";
    private static final String KEY_FIELDS = "fields";
    private static final String KEY_VARS = "vars";
    private static final String KEY_ATTRIBUTES = "attributes";
    private static final String KEY_OPTIONS = "options";
    private static final String KEY_XOPTIONS = "xOptions";
    private static final String KEY_CODE = "code";
    private static final String KEY_VALUE = "value";
    private static final String KEY_OPTION_IDS = "optionIds";
    private static final String KEY_XOPTION_IDS = "xOptionIds";
    private static final String KEY_OPT_OUTS = "optOuts";
    private static final String KEY_MERCHANT_IDS = "merchantIds";
    private static final String KEY_STORE_IDS = "storeIds";
    private static final String KEY_REQUEST_CONTEXT_IDS = "requestContextIds";
    private static final String KEY_SAVE_AS_NEW_COPY = "saveAsNewCopy";
    private static final String FIELD_PREFIX_CONTEXT_OBJECTS_ARRAY = "ctxObjArray:";

    // Test if the input is a valid ISO8601 JSON date.
    private static final Pattern datePattern = Pattern.compile(
        "[\\d]{4}\\-[\\d]{2}\\-[\\d]{2}T[\\d]{2}\\:[\\d]{2}\\:[\\d]{2}(?:\\.[\\d]{3})?(?:[\\-+]{1}[\\d]{4})?[Z]{0,1}");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Update deserialize(ObjectReader reader, Context ctx) throws IOException {
        Id id = null;
        Map<String, Object> fields = null;
        Map<String, Object> vars = null;
        Map<String, ContextObject<?>> attributes = null;
        Map<String, List<Id>> options = null;
        Map<String, ContextObject<List<Id>>> xOptions = null;
        Map<String, ContextObject<Boolean>> optOuts = null;
        List<Id> merchantIds = null;
        List<Id> storeIds = null;
        List<Id> requestContextIds = null;
        boolean saveAsNewCopy = false;

        // Update object
        ObjectReader updateObject = reader.beginObject();

        while (updateObject.hasNext()) {
            updateObject.next();

            String fieldName = updateObject.name();

            if (KEY_ID.equals(fieldName)) {
                String _id = updateObject.valueAsString();

                if (_id != null) {
                    id = Id.valueOf(_id);
                }
            }

            if (KEY_SAVE_AS_NEW_COPY.equals(fieldName)) {
                String _saveAsNewCopy = updateObject.valueAsString();

                if (_saveAsNewCopy != null) {
                    saveAsNewCopy = Boolean.valueOf(_saveAsNewCopy);
                }
            }

            if (KEY_FIELDS.equals(fieldName)) {
                fields = deserializeFields(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_VARS.equals(fieldName)) {
                vars = deserializeVars(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_ATTRIBUTES.equals(fieldName)) {
                attributes = deserializeAttributes(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_OPTIONS.equals(fieldName)) {
                options = deserializeOptions(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_XOPTIONS.equals(fieldName)) {
                xOptions = deserializeXOptions(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_OPT_OUTS.equals(fieldName)) {
                optOuts = deserializeOptOuts(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_MERCHANT_IDS.equals(fieldName)) {
                merchantIds = deserializeIds(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_STORE_IDS.equals(fieldName)) {
                storeIds = deserializeIds(updateObject.beginArray());
                updateObject.endArray();
            }

            if (KEY_REQUEST_CONTEXT_IDS.equals(fieldName)) {
                requestContextIds = deserializeIds(updateObject.beginArray());
                updateObject.endArray();
            }
        }

        reader.endObject();

        return new Update(id, fields, vars, attributes, options, xOptions, optOuts, merchantIds, storeIds,
            requestContextIds, saveAsNewCopy);
    }

    public Map<String, Object> deserializeFields(ObjectReader fieldReader) throws IOException {
        ContextObjectConverter converter = new ContextObjectConverter();

        Map<String, Object> fields = new LinkedHashMap<>();

        while (fieldReader.hasNext()) {
            fieldReader.next();

            ObjectReader fieldObject = fieldReader.beginObject();

            while (fieldObject.hasNext()) {
                fieldObject.next();

                String name = fieldReader.name();

                if (name.startsWith(FIELD_PREFIX_CONTEXT_OBJECT)) {
                    ContextObject<?> ctxObj = converter.deserialize(fieldReader, null);
                    fields.put(name.substring(7), ctxObj);
                } else if (name.startsWith(FIELD_PREFIX_CONTEXT_OBJECTS_ARRAY)) {
                    fields.put(name.substring(12), deserializeCtxArray(fieldReader, converter));
                } else if (fieldObject.getValueType() == ValueType.ARRAY) {
                    fields.put(name, deserializeArray(fieldReader));
                } else {
                    String value = fieldReader.valueAsString();

                    if (value != null) {
                        Matcher m = datePattern.matcher(value);
                        if (m.matches()) {
                            try {
                                fields.put(name, dateFormat.parse(fieldReader.valueAsString()));
                            } catch (ParseException e) {
                                fields.put(name, value);
                            }
                        } else {
                            fields.put(name, value);
                        }
                    } else {
                        fields.put(name, value);
                    }
                    // Valid date.

                }
            }

            fieldReader.endObject();
        }

        return fields;
    }

    public Map<String, Object> deserializeVars(ObjectReader fieldReader) throws IOException {
        ContextObjectConverter converter = new ContextObjectConverter();

        Map<String, Object> vars = new LinkedHashMap<>();

        while (fieldReader.hasNext()) {
            fieldReader.next();

            ObjectReader fieldObject = fieldReader.beginObject();

            while (fieldObject.hasNext()) {
                fieldObject.next();

                String name = fieldReader.name();

                if (name.startsWith(FIELD_PREFIX_CONTEXT_OBJECT)) {
                    ContextObject<?> ctxObj = converter.deserialize(fieldReader, null);
                    vars.put(name.substring(7), ctxObj);
                } else if (name.startsWith(FIELD_PREFIX_CONTEXT_OBJECTS_ARRAY)) {
                    vars.put(name.substring(12), deserializeCtxArray(fieldReader, converter));
                } else if (fieldObject.getValueType() == ValueType.ARRAY) {
                    vars.put(name, deserializeArray(fieldReader));
                } else {
                    String value = fieldReader.valueAsString();

                    if (value != null) {
                        Matcher m = datePattern.matcher(value);
                        if (m.matches()) {
                            try {
                                vars.put(name, dateFormat.parse(fieldReader.valueAsString()));
                            } catch (ParseException e) {
                                vars.put(name, value);
                            }
                        } else {
                            vars.put(name, value);
                        }
                    } else {
                        vars.put(name, value);
                    }
                    // Valid date.

                }
            }

            fieldReader.endObject();
        }

        return vars;
    }

    private List<?> deserializeArray(ObjectReader fieldReader) throws IOException {
        List<Object> list = new ArrayList<>();

        fieldReader.beginArray();

        while (fieldReader.hasNext()) {
            fieldReader.next();
            list.add(fieldReader.valueAsString());
        }

        fieldReader.endArray();

        return list;
    }

    private List<?> deserializeCtxArray(ObjectReader fieldReader, ContextObjectConverter converter) throws IOException {
        ObjectReader ctxArray = fieldReader.beginArray();
        List<ContextObject<?>> list = new ArrayList<>();
        while (ctxArray.hasNext()) {
            ctxArray.next();
            ObjectReader ctxObjects = ctxArray.beginObject();
            while (ctxObjects.hasNext()) {
                ctxObjects.next();
                ContextObject<?> ctxObj = converter.deserialize(ctxObjects, null);
                list.add(ctxObj);
            }
            ctxArray.endObject();
        }
        fieldReader.endArray();
        return list;
    }

    public Map<String, ContextObject<?>> deserializeAttributes(ObjectReader attributesReader) throws IOException {
        Map<String, ContextObject<?>> attributes = new LinkedHashMap<>();

        ContextObjectConverter converter = new ContextObjectConverter();

        while (attributesReader.hasNext()) {
            attributesReader.next();

            ObjectReader attributeObject = attributesReader.beginObject();

            String code = null;
            ContextObject<?> ctxObj = null;

            while (attributeObject.hasNext()) {
                attributeObject.next();

                String key = attributeObject.name();

                if (KEY_CODE.equals(key)) {
                    code = attributeObject.valueAsString();
                } else if (KEY_VALUE.equals(key)) {
                    ctxObj = converter.deserialize(attributeObject, null);
                }
            }

            attributes.put(code, ctxObj);

            // attributes.put(attributesReader.name(),
            // attributesReader.valueAsString());
            attributesReader.endObject();
        }

        return attributes;
    }

    public Map<String, List<Id>> deserializeOptions(ObjectReader attributesReader) throws IOException {
        Map<String, List<Id>> options = new LinkedHashMap<>();

        while (attributesReader.hasNext()) {
            attributesReader.next();

            ObjectReader attributeObject = attributesReader.beginObject();

            String code = null;
            List<Id> optionIds = new ArrayList<>();

            while (attributeObject.hasNext()) {
                ValueType valueType = attributeObject.next();

                String key = attributeObject.name();

                if (KEY_CODE.equals(key)) {
                    code = attributeObject.valueAsString();
                } else if (KEY_OPTION_IDS.equals(key)) {
                    if (ValueType.ARRAY.equals(valueType)) {
                        ObjectReader optionIdsArray = attributesReader.beginArray();

                        while (optionIdsArray.hasNext()) {
                            optionIdsArray.next();

                            if (!Str.isEmpty(optionIdsArray.valueAsString()))
                                optionIds.add(Id.valueOf(optionIdsArray.valueAsString()));
                        }

                        attributesReader.endArray();
                    } else if (!Str.isEmpty(attributeObject.valueAsString())) {
                        optionIds.add(Id.valueOf(attributeObject.valueAsString()));
                    }
                }
            }

            options.put(code, optionIds);

            // attributes.put(attributesReader.name(),
            // attributesReader.valueAsString());
            attributesReader.endObject();
        }

        return options;
    }

    public Map<String, ContextObject<List<Id>>> deserializeXOptions(ObjectReader xOptionsReader) throws IOException {
        XOptionsConverter converter = new XOptionsConverter();

        Map<String, ContextObject<List<Id>>> xOptions = new LinkedHashMap<String, ContextObject<List<Id>>>();

        while (xOptionsReader.hasNext()) {
            xOptionsReader.next();

            ObjectReader ctxObjectReader = xOptionsReader.beginObject();

            String code = null;
            ContextObject<List<Id>> ctxObj = null;

            while (ctxObjectReader.hasNext()) {
                ctxObjectReader.next();

                String key = xOptionsReader.name();

                if (KEY_CODE.equals(key)) {
                    code = xOptionsReader.valueAsString();
                } else if (KEY_XOPTION_IDS.equals(key)) {
                    ctxObj = converter.deserialize(xOptionsReader, null);
                }
            }

            xOptions.put(code, ctxObj);

            xOptionsReader.endObject();
        }

        return xOptions;
    }

    @SuppressWarnings("unchecked")
    public Map<String, ContextObject<Boolean>> deserializeOptOuts(ObjectReader optOutReader) throws IOException {
        Map<String, ContextObject<Boolean>> optOuts = new LinkedHashMap<>();

        ContextObjectConverter converter = new ContextObjectConverter();

        while (optOutReader.hasNext()) {
            optOutReader.next();

            ObjectReader optOutObject = optOutReader.beginObject();

            String code = null;
            ContextObject<Boolean> ctxObj = null;

            while (optOutObject.hasNext()) {
                optOutObject.next();

                String key = optOutObject.name();

                if (KEY_CODE.equals(key)) {
                    code = optOutObject.valueAsString();
                } else if (KEY_VALUE.equals(key)) {
                    ctxObj = (ContextObject<Boolean>) converter.deserialize(optOutObject, null);
                }
            }

            optOuts.put(code, ctxObj);

            optOutReader.endObject();
        }

        return optOuts;
    }

    public List<Id> deserializeIds(ObjectReader fieldReader) throws IOException {
        List<Id> ids = new ArrayList<>();

        while (fieldReader.hasNext()) {
            fieldReader.next();
            ids.add(Id.parseId(fieldReader.valueAsString()));
        }

        return ids;
    }

    @Override
    public void serialize(Update update, ObjectWriter writer, Context ctx) throws IOException {
        UpdateMap fields = update.getFields();

        if (fields != null && fields.size() > 0) {
            Set<String> keys = fields.keySet();

            writer.beginObject();

            for (String key : keys) {
                writer.writeName(key);

                writer.writeValue(String.valueOf(fields.get(key)));
            }

            writer.endObject();
        }
    }
}
