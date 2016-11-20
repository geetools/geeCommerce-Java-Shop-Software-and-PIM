package com.geecommerce.core.rest.jersey.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.geecommerce.core.rest.ResponseWrapper;
import com.geecommerce.core.rest.pojo.Error;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.xml.JAXBContextFactory;
import com.geecommerce.core.xml.JAXBWriterHelper;
import com.google.inject.Singleton;

@Profile
@Singleton
@Provider
@Produces(MediaType.APPLICATION_XML)
public class ResponseWrapperMessageBodyWriter implements MessageBodyWriter<ResponseWrapper> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!MediaType.APPLICATION_XML_TYPE.equals(mediaType))
            return false;

        if (!ResponseWrapper.class.isAssignableFrom(type))
            return false;

        return true;
    }

    @Override
    public long getSize(ResponseWrapper t, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeTo(ResponseWrapper responseWrapper, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
        throws IOException, WebApplicationException {
        Charset c = Charset.forName("UTF-8");
        String cName = c.name();

        String elementName = "response";

        entityStream.write(
            String.format("<?xml version=\"1.0\" encoding=\"%s\" standalone=\"yes\"?>", cName).getBytes(cName));
        entityStream.write(String.format("<%s>", elementName).getBytes(cName));

        // -----------------------------------------------------------------------------------
        // Metadata
        // -----------------------------------------------------------------------------------

        String metadataElementName = "metadata";
        entityStream.write(String.format("<%s>", metadataElementName).getBytes(cName));

        Map<String, Object> metadata = responseWrapper.get_metadata();
        Set<String> keys = metadata.keySet();

        for (String key : keys) {
            try {
                Object value = metadata.get(key);

                JAXBContext jaxbContext = JAXBContextFactory.INSTANCE.getJaxBContext(value.getClass());

                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                entityStream.write(String.format("<%s>", key).getBytes(cName));
                entityStream.write(String.valueOf(value).getBytes(cName));

                // marshaller.marshal(String.valueOf(value), entityStream);

                entityStream.write(String.format("</%s>", key).getBytes(cName));

            } catch (Throwable t) {
                t.printStackTrace();
                throw new WebApplicationException(t);
            }
        }

        entityStream.write(String.format("</%s>", metadataElementName).getBytes(cName));

        // -----------------------------------------------------------------------------------
        // Data
        // -----------------------------------------------------------------------------------

        String dataElementName = "data";
        entityStream.write(String.format("<%s>", dataElementName).getBytes(cName));

        Map<String, Object> data = responseWrapper.getData();
        keys = data.keySet();

        for (String key : keys) {
            try {
                Object value = data.get(key);

                if (value instanceof List) {
                    List<?> list = (List<?>) value;

                    if (list != null && list.size() > 0) {
                        Object o = list.get(0);

                        if (Model.class.isAssignableFrom(o.getClass())) {
                            JAXBWriterHelper.write((List<Model>) list, type, o.getClass().getAnnotations(), mediaType,
                                httpHeaders, entityStream);
                        }
                    }
                } else {
                    JAXBContext jaxbContext = JAXBContextFactory.INSTANCE.getJaxBContext(value.getClass());

                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                    marshaller.marshal(value, entityStream);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                throw new WebApplicationException(t);
            }
        }

        entityStream.write(String.format("</%s>", dataElementName).getBytes(cName));

        // -----------------------------------------------------------------------------------
        // Errors
        // -----------------------------------------------------------------------------------

        String errorsElementName = "errors";
        entityStream.write(String.format("<%s>", errorsElementName).getBytes(cName));

        List<Error> errors = responseWrapper.getErrors();

        for (Error error : errors) {
            try {
                JAXBContext jaxbContext = JAXBContextFactory.INSTANCE.getJaxBContext(error.getClass());

                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

                marshaller.marshal(error, entityStream);
            } catch (Throwable t) {
                t.printStackTrace();
                throw new WebApplicationException(t);
            }
        }

        entityStream.write(String.format("</%s>", errorsElementName).getBytes(cName));

        entityStream.write(String.format("</%s>", elementName).getBytes(cName));
    }
}
