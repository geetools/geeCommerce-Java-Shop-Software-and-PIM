package com.geecommerce.core.rest.jersey.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.inject.Singleton;
import com.geecommerce.core.rest.jersey.adapter.UpdateAdapter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.service.annotation.Profile;
import com.geecommerce.core.xml.JAXBContextFactory;

@Profile
@Singleton
@Provider
@Consumes(MediaType.APPLICATION_XML)
public class UpdateMessageBodyReader implements MessageBodyReader<Update> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	return type == Update.class;
    }

    @Override
    public Update readFrom(Class<Update> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
	try {
	    JAXBContext jaxbContext = JAXBContextFactory.INSTANCE.getJaxBContext(Update.class);
	    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    unmarshaller.setAdapter(UpdateAdapter.class, new UpdateAdapter());

	    Update updateBean = (Update) unmarshaller.unmarshal(entityStream);
	    return updateBean;
	} catch (JAXBException e) {
	    throw new WebApplicationException(e);
	}
    }

}
