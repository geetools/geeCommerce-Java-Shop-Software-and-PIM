package com.geecommerce.core.rest.jersey.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.xml.JAXBWriterHelper;
import com.google.inject.Singleton;

@Profile
@Singleton
@Provider
@Produces(MediaType.APPLICATION_XML)
public class ModelListMessageBodyWriter implements MessageBodyWriter<List<Model>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!MediaType.APPLICATION_XML_TYPE.equals(mediaType))
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
    public long getSize(List<Model> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<Model> list, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
        throws IOException, WebApplicationException {
        Charset c = Charset.forName("UTF-8");
        String cName = c.name();

        entityStream.write(
            String.format("<?xml version=\"1.0\" encoding=\"%s\" standalone=\"yes\"?>", cName).getBytes(cName));

        JAXBWriterHelper.write(list, type, annotations, mediaType, httpHeaders, entityStream);
    }
}
