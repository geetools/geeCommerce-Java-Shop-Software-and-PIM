package com.geecommerce.mediaassets.converter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface DocumentConverter {

    boolean canConvert(String mimeType);

    ByteArrayOutputStream convert(InputStream stream);

}
