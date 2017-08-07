package com.geecommerce.core.url.parser;

import java.net.MalformedURLException;
import java.net.URL;

import com.geecommerce.core.Str;

public abstract class AbstractURLParser implements URLParser {
    public URL getURL(String requestUrl) {
        URL url = null;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return url;
    }
    
    public String prepareUrlPrefix(String urlPrefix, String requestURL) {
        URL url = getURL(requestURL);

        return new StringBuilder(url.getProtocol()).append("://").append(urlPrefix.replace("${p}", Str.EMPTY)).toString();
    }
}
