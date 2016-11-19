package com.geecommerce.core.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.jactiveresource.Inflector;

import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Model;

public class JAXBWriterHelper {
    public static void write(List<Model> list, Class<?> type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws UnsupportedEncodingException, IOException {
	Charset c = Charset.forName("UTF-8");
	String cName = c.name();

	String elementName = getWrapperName(list, annotations);
	entityStream.write(String.format("<%s>", elementName).getBytes(cName));

	for (Model m : list) {
	    try {
		JAXBContext jaxbContext = JAXBContextFactory.INSTANCE.getJaxBContext(m.getClass());

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

		marshaller.marshal(m, entityStream);
	    } catch (JAXBException e) {
		throw new WebApplicationException(e);
	    }
	}

	entityStream.write(String.format("</%s>", elementName).getBytes(cName));
    }

    private static String getWrapperName(List<Model> list, Annotation[] annotations) {
	String name = null;

	XmlRootElement xmlRootElement = findXmlRootElementAnnotation(annotations);

	Model model = null;

	if (xmlRootElement == null && list != null && list.size() > 0) {
	    model = list.get(0);

	    xmlRootElement = model.getClass().getAnnotation(XmlRootElement.class);
	}

	if (xmlRootElement != null) {
	    name = xmlRootElement.name();
	}

	if (name == null && model != null) {
	    Class<? extends Model> modelInterface = Reflect.getModelInterface(model.getClass());

	    if (modelInterface != null) {
		name = modelInterface.getSimpleName();
	    }

	    if (name == null) {
		name = model.getClass().getSimpleName();
		name = name.replaceFirst("Default", "");
	    }
	}

	return name == null ? "data" : Inflector.pluralize(new StringBuilder(name.substring(0, 1).toLowerCase()).append(name.substring(1)).toString());
    }

    private static XmlRootElement findXmlRootElementAnnotation(Annotation[] annotations) {
	Annotation foundAnnotation = null;

	for (Annotation annotation : annotations) {
	    if (XmlRootElement.class == annotation.getClass()) {
		foundAnnotation = annotation;
		break;
	    }
	}

	return (XmlRootElement) foundAnnotation;
    }
}
