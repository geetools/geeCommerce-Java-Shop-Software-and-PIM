package com.geecommerce.core.web.filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import javax.servlet.http.HttpSession;

import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StandardSessionFacade;
import org.apache.catalina.util.CustomObjectInputStream;

public class JavaSerializer {
    public static byte[] serializeFrom(HttpSession session) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	if (session != null) {
	    Field facadeSessionField = StandardSessionFacade.class.getDeclaredField("session");
	    facadeSessionField.setAccessible(true);
	    StandardSession standardSession = (StandardSession) facadeSessionField.get(session);

	    if (standardSession != null) {
		// StandardSessionFacade standardSession = (StandardSessionFacade) session;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
		oos.writeLong(standardSession.getCreationTime());
		standardSession.writeObjectData(oos);

		oos.close();

		return bos.toByteArray();
	    }
	}

	return null;
    }

    public static HttpSession deserializeInto(byte[] data, HttpSession session, ClassLoader loader) throws IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	if (data != null && data.length > 0 && session != null) {
	    Field facadeSessionField = StandardSessionFacade.class.getDeclaredField("session");
	    facadeSessionField.setAccessible(true);
	    StandardSession standardSession = (StandardSession) facadeSessionField.get(session);

	    // StandardSessionFacade standardSession = (StandardSessionFacade) session;

	    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));

	    ObjectInputStream ois = new CustomObjectInputStream(bis, loader);
	    standardSession.setCreationTime(ois.readLong());
	    standardSession.readObjectData(ois);
	}

	return session;
    }
}