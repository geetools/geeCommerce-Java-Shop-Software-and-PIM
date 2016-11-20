package com.geecommerce.core.url.parser;

import com.geecommerce.core.enums.UrlType;
import com.geecommerce.core.system.model.DefaultRequestContext;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;

public class TestParser {
    private static final String[] URLS = new String[] { "http://www.testshop.ch/product/view/123/?test=xy",
        "https://www.fr.testshop.ch/product/view/123/?test=xy",
        "http://www.testshop.ch/fr/product/view/123/?test=xy",
        "http://www.ch.testshop.com/fr/product/view/123/?test=xy",
        "http://www.testshop.com/ch-fr/product/view/123/?test=xy",
        "http://www.fr.testshop.com/ch/product/view/123/?test=xy",
        "http://fr.testshop.ch/web/product/view/123/?test=xy",
        "http://ch.testshop.com/fr-web/product/view/123/?test=xy",
        "http://www.testshop.com/web-ch-fr/product/view/123/?test=xy" };

    private static final RequestContext[] CONTEXTS = new RequestContext[] {
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "www.testshop.com/web-fr-ch/", UrlType.URI_PREFIX_ONLY, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "www.testshop.com/ch-fr/", UrlType.V_CL, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "ch.testshop.com/fr-web/", UrlType.C_LV, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "fr.testshop.ch/web/", UrlType.LC_V, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "www.testshop.ch/fr/", UrlType.VC_L_1, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "www.ch.testshop.com/fr/", UrlType.VC_L_2, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "www.fr.testshop.com/ch/", UrlType.VL_C, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "www.fr.testshop.ch", UrlType.DOMAIN_ONLY, 0),
        new DefaultRequestContext(new Id(123L), new Id(456L), new Id(789L), "de", "DE", new Id(1010L),
            "www.testshop.ch", UrlType.DOMAIN_ONLY_WITHOUT_LANGUAGE, 0) };

    /**
     * @param args
     */
    public static void main(String[] args) {
        for (String url : URLS) {
            for (RequestContext context : CONTEXTS) {
                URLParser urlParser = context.getUrlType().getUrlParser();

                if (urlParser.isMatch(url, context)) {
                    System.out.println(
                        urlParser.getClass().getSimpleName() + " => " + urlParser.stripServletPath(url, context));
                    break;
                }
            }
        }
    }
}
