package com.geecommerce.core.json.genson;

import java.io.IOException;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.type.Id;

@Profile
public class IdConverter implements Converter<Id> {
    @Override
    public void serialize(Id id, ObjectWriter objectWriter, Context context) throws IOException {
	objectWriter.writeValue(id.str());
    }

    @Override
    public Id deserialize(ObjectReader objectReader, Context context) throws IOException {
	return Id.parseId(objectReader.valueAsString());
    }
}
