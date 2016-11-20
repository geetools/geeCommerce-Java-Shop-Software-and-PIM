package com.geecommerce.shipping.dhl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.dhl.datatypes.Request;
import com.dhl.datatypes.ServiceHeader;
import com.geecommerce.core.App;
import com.geecommerce.shipping.dhl.configuration.Key;
import com.google.inject.Inject;

public class DhlService {
    @Inject
    protected App app;

    protected String getHttpUrl() {
        if (app.cpBool_(Key.USE_SANDBOX, true)) {
            return app.cpStr_(Key.SANDBOX + Key.URL);
        } else {
            return app.cpStr_(Key.PRODUCTION + Key.URL);
        }
    }

    public HttpURLConnection createUrlConnection() throws IOException {
        URL servletURL = null;
        servletURL = new URL(getHttpUrl());
        HttpURLConnection servletConnection = null;
        servletConnection = (HttpURLConnection) servletURL.openConnection();
        servletConnection.setDoOutput(true); // to allow us to write to the
        // URL
        servletConnection.setDoInput(true);
        servletConnection.setUseCaches(false);
        servletConnection.setRequestMethod("POST");
        return servletConnection;

    }

    public Request getRequest() throws DatatypeConfigurationException {
        ServiceHeader serviceHeader = new ServiceHeader();
        serviceHeader.setMessageTime(getMessageTime());

        if (app.cpBool_(Key.USE_SANDBOX, true)) {
            serviceHeader.setSiteID(app.cpStr_(Key.SANDBOX + Key.SITE_ID));
            serviceHeader.setPassword(app.cpStr_(Key.SANDBOX + Key.PASSWORD));
        } else {
            serviceHeader.setSiteID(app.cpStr_(Key.PRODUCTION + Key.SITE_ID));
            serviceHeader.setPassword(app.cpStr_(Key.PRODUCTION + Key.PASSWORD));
        }

        Request request = new Request();
        request.setServiceHeader(serviceHeader);
        return request;
    }

    protected XMLGregorianCalendar getMessageTime() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    }

    public void sendRequestToXmlPi(String requestXml, HttpURLConnection servletConnection) throws IOException {
        servletConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String len = Integer.toString(requestXml.getBytes().length);
        servletConnection.setRequestProperty("Content-Length", len);
        servletConnection.connect();
        OutputStreamWriter wr = new OutputStreamWriter(servletConnection.getOutputStream());
        wr.write(requestXml);
        wr.flush();
        wr.close();
    }

    public String readResponseFromXmlPi(HttpURLConnection servletConnection) throws IOException {

        InputStream inputStream = null;
        inputStream = servletConnection.getInputStream();
        StringBuffer response = new StringBuffer();
        int printResponse;
        // Reading the response into StringBuffer
        while ((printResponse = inputStream.read()) != -1) {
            response.append((char) printResponse);
        }
        inputStream.close();
        return response.toString();
    }

    public String marshal(Object object) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("com.dhl");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setSchema(null);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(object, stringWriter);
        return stringWriter.getBuffer().toString();
    }

    public Object unmarshal(String xml, Class[] classes) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(classes);// JAXBContext.newInstance(name);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(null);
        return unmarshaller.unmarshal(new StringReader(xml));
    }

    public boolean isErrorResponse(String xml, String errorNodeName) {
        String rootElement = getCompleteRootElement(xml);
        if (rootElement.equals(errorNodeName))
            return true;
        return false;
    }

    protected String getCompleteRootElement(String message) {
        String rootElement = null;
        StringTokenizer st = new StringTokenizer(message.trim(), "<>", true);

        String value = null;
        int index = 0;
        while (st.hasMoreTokens()) {
            value = st.nextToken();

            if (value.equals("<")) {
                rootElement = st.nextToken();

                if (!rootElement.startsWith("?") && !rootElement.startsWith("!")) {
                    index = rootElement.indexOf(" ");
                    if (index != -1) {
                        rootElement = rootElement.substring(0, index);
                    }
                    return rootElement;
                }
            }
        }
        return rootElement;
    }
}
