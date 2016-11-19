package com.geecommerce.core.web;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;

public class Sanitizer {
    private static final String AMPERSAND_ENTITY = "&amp;";

    public static final String clean(String value) {
        if (value == null)
            return null;

        // Use the ESAPI library to avoid encoded attacks.
        // value = ESAPI.encoder().canonicalize(value);

        // Avoid null characters
        value = value.replaceAll(Str.NUL, Str.EMPTY);

        // Clean out HTML
        return jsoupClean(value, App.get().isAPIRequest() ? Whitelist.basicWithImages() : Whitelist.none());
    }

    private static String jsoupClean(String html, Whitelist whitelist) {
        ByteArrayInputStream bais = null;

        try {
            String charset = App.get().getSystemCharset();

            // Attempt a charset safe way of parsing the string.
            bais = new ByteArrayInputStream(html.getBytes(charset));
            Document doc = Jsoup.parse(bais, charset, Str.EMPTY);
            doc.outputSettings().charset(charset);
            Cleaner cleaner = new Cleaner(whitelist);
            Document clean = cleaner.clean(doc);
            clean.outputSettings().escapeMode(EscapeMode.xhtml);
            String newHtml = clean.body().html().replace(Str.CARET, Str.EMPTY);

            if (html.contains(Str.AMPERSAND) && !html.contains(Str.SMALLER_THAN)) {
                newHtml = html.replace(AMPERSAND_ENTITY, Str.AMPERSAND);
            }

            return newHtml;
        } catch (Throwable t) {
            // Fallback to basic parser (which may cause i18n character
            // problems) if the above does not work.
            Document dirty = Jsoup.parseBodyFragment(html, Str.EMPTY);
            Cleaner cleaner = new Cleaner(whitelist);
            Document clean = cleaner.clean(dirty);
            clean.outputSettings().escapeMode(EscapeMode.xhtml);
            String newHtml = clean.body().html().replace(Str.CARET, Str.EMPTY);

            if (html.contains(Str.AMPERSAND) && !html.contains(Str.SMALLER_THAN)) {
                newHtml = html.replace(AMPERSAND_ENTITY, Str.AMPERSAND);
            }

            return newHtml;
        } finally {
            IOUtils.closeQuietly(bais);
        }
    }
}
