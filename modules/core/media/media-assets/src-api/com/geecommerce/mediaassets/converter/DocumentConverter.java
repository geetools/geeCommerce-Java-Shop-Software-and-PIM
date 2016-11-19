package com.geecommerce.mediaassets.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public interface DocumentConverter {

    boolean canConvert(String mimeType);

    ByteArrayOutputStream convert(InputStream stream);

}
