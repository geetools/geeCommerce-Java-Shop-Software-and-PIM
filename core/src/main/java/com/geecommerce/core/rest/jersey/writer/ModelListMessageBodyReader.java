package com.geecommerce.core.rest.jersey.writer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.geecommerce.core.service.api.Model;

//@Profile
//@Singleton
//@Provider
//@Consumes(MediaType.APPLICATION_JSON)
public class ModelListMessageBodyReader implements MessageBodyReader<List<Model>> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!MediaType.APPLICATION_JSON_TYPE.equals(mediaType))
            return false;

        if (!Collection.class.isAssignableFrom(type))
            return false;

        ParameterizedType pType = ((ParameterizedType) genericType);
        Type[] actualTypArgs = pType.getActualTypeArguments();

        if (actualTypArgs == null || actualTypArgs.length != 1)
            return false;

        if (!Model.class.isAssignableFrom((Class<?>) actualTypArgs[0]))
            return false;

        return true;
    }

    @Override
    public List<Model> readFrom(Class<List<Model>> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
        throws IOException, WebApplicationException {

        return null;
    }
}
