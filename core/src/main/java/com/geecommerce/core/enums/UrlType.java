package com.geecommerce.core.enums;

import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.service.api.ModelEnum;
import com.geecommerce.core.url.parser.URLParser;

public enum UrlType implements ModelEnum {
    // -------------------------------------------------------------------------------------------
    // URL types can be made up of a domain-name and/or a URL-prefix. The short
    // names below
    // are put together with the following rules:
    // 1) Characters BEFORE the underscore represent parts of a domain.
    // 2) Characters AFTER the underscore represent parts of a URL-prefix.
    // 3) V=view, L=language, C=country
    // A request-context could therefore theoretically be composed by extracting
    // parts of the URL.
    // For this there are various URL-parsers as different rules may be applied
    // for representing
    // the store, view, country or language. For example: VC_L_1. In this rule
    // the view and country
    // part is in the domain-name and the language part is in the URL-prefix.
    // -------------------------------------------------------------------------------------------

    // DOMAIN_ONLY: www.fr.myshop.ch (store=myshop, view=www, lang=fr,
    // country=ch)
    DOMAIN_ONLY(1, new com.geecommerce.core.url.parser.DOMAIN_ONLY()),
    // VC_L_1: www.myshop.ch/fr/ (store=myshop, view=www, language=fr,
    // country=ch)
    VC_L_1(2, new com.geecommerce.core.url.parser.VC_L_1()),
    // VC_L_2: www.ch.myshop.com/fr/ (store=myshop, view=www, language=fr,
    // country=ch)
    VC_L_2(3, new com.geecommerce.core.url.parser.VC_L_2()),
    // V_CL: www.myshop.com/ch-fr/ (store=myshop, view=www, language=fr,
    // country=ch)
    V_CL(4, new com.geecommerce.core.url.parser.V_CL()),
    // VL_C: www.fr.myshop.com/ch/ (store=myshop, view=www, language=fr,
    // country=ch)
    VL_C(5, new com.geecommerce.core.url.parser.VL_C()),
    // LC_V: fr.myshop.ch/web/ (store=myshop, view=web, language=fr, country=ch)
    LC_V(6, new com.geecommerce.core.url.parser.LC_V()),
    // C_LV: ch.myshop.com/fr-web/ (store=myshop, view=web, language=fr,
    // country=ch)
    C_LV(7, new com.geecommerce.core.url.parser.C_LV()),
    // URI_PREFIX_ONLY: www.myshop.com/web-ch-fr/ (store=myshop, view=web,
    // language=fr, country=ch)
    URI_PREFIX_ONLY(8, new com.geecommerce.core.url.parser.URI_PREFIX_ONLY()),
    // DOMAIN_ONLY_WITHOUT_LANGUAGE: www.myshop.com (values can be retrieved
    // from the mongodb collection request_contexts)
    DOMAIN_ONLY_WITHOUT_LANGUAGE(9, new com.geecommerce.core.url.parser.DOMAIN_ONLY_WITHOUT_LANGUAGE());

    private int id;
    private URLParser urlParser = null;

    private UrlType(int id, URLParser urlParser) {
        this.id = id;
        this.urlParser = urlParser;
    }

    public final int toId() {
        return this.id;
    }

    public final URLParser getUrlParser() {
        return urlParser;
    }

    public static final UrlType fromId(int id) {
        for (UrlType urlType : values()) {
            if (urlType.toId() == id) {
                return urlType;
            }
        }

        return null;
    }

    public static final Map<String, Integer> toHumanReadableMap() {
        Map<String, Integer> hrMap = new LinkedHashMap<>();

        for (UrlType urlType : values()) {
            hrMap.put(App.get().message(new StringBuilder("enum.").append(UrlType.class.getSimpleName()).append(".")
                .append(urlType.name()).toString()), urlType.id);
        }

        return hrMap;
    }
}
