package com.geecommerce.core.json.genson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.owlike.genson.stream.ValueType;

@Profile
public class AttributeOptionConverter implements Converter<AttributeOption> {
    private static final String KEY_ID = "id";
    private static final String KEY_ID2 = "id2";
    private static final String KEY_ATTRIBUTE_ID = "attributeId";
    private static final String KEY_LABEL = "label";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_POSITION = "position";

    @SuppressWarnings("unchecked")
    @Override
    public AttributeOption deserialize(ObjectReader reader, Context ctx) throws IOException {
        AttributeOption ao = App.get().getModel(AttributeOption.class);

        ContextObjectConverter ctxObjConverter = new ContextObjectConverter();

        ObjectReader obj = reader.beginObject();

        while (obj.hasNext()) {
            ValueType type = obj.next();

            if (KEY_ID.equals(obj.name())) {
                ao.setId(Id.valueOf(obj.valueAsString()));
            } else if (KEY_ID2.equals(obj.name())) {
                ao.setId2(Id.valueOf(obj.valueAsString()));
            } else if (KEY_ATTRIBUTE_ID.equals(obj.name())) {
                ao.setAttributeId(Id.valueOf(obj.valueAsString()));
            } else if (KEY_POSITION.equals(obj.name())) {
                ao.setPosition(obj.valueAsInt());
            } else if (KEY_TAGS.equals(obj.name())) {
                List<String> tags = new ArrayList<>();

                if (type == ValueType.ARRAY) {
                    ObjectReader array = obj.beginArray();

                    while (array.hasNext()) {
                        array.next();
                        tags.add(array.valueAsString());
                    }

                    obj.endArray();
                } else {
                    tags.add(obj.valueAsString());
                }

                ao.setTags(tags);
            } else if (KEY_LABEL.equals(obj.name())) {
                ao.setLabel((ContextObject<String>) ctxObjConverter.deserialize(obj, ctx));
            }
        }

        reader.endObject();

        return ao;
    }

    @Override
    public void serialize(AttributeOption attributeOption, ObjectWriter writer, Context ctx) throws IOException {
        if (attributeOption != null) {
            ContextObjectConverter ctxObjConverter = new ContextObjectConverter();

            writer.beginObject();

            if (attributeOption.getId() != null && !ConvertUtil.ignoreProperty(KEY_ID)) {
                writer.writeName(KEY_ID);
                writer.writeValue(attributeOption.getId().str());
            }

            if (attributeOption.getId2() != null && !ConvertUtil.ignoreProperty(KEY_ID2)) {
                writer.writeName(KEY_ID2);
                writer.writeValue(attributeOption.getId2().str());
            }

            if (attributeOption.getAttributeId() != null && !ConvertUtil.ignoreProperty(KEY_ATTRIBUTE_ID)) {
                writer.writeName(KEY_ATTRIBUTE_ID);
                writer.writeValue(attributeOption.getAttributeId().str());
            }

            if (attributeOption.getLabel() != null && !ConvertUtil.ignoreProperty(KEY_LABEL)) {
                writer.writeName(KEY_LABEL);
                ctxObjConverter.serialize(attributeOption.getLabel(), writer, ctx);
            }

            if (attributeOption.getTags() != null && !ConvertUtil.ignoreProperty(KEY_TAGS)) {
                writer.writeName(KEY_TAGS);

                writer.beginArray();

                for (String tag : attributeOption.getTags()) {
                    writer.writeValue(tag);
                }

                writer.endArray();
            }

            if (!ConvertUtil.ignoreProperty(KEY_POSITION)) {
                writer.writeName(KEY_POSITION);
                writer.writeValue(attributeOption.getPosition());
            }

            writer.endObject();
        }
    }
}
