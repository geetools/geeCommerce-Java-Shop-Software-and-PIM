package com.geecommerce.core.system.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface Language extends Model {
    public Id getId();

    public String getIso6391Code();

    public String getIso6392BCode();

    public String getIso6392TCode();

    public ContextObject<String> getLabel();

    class Col {
        public static final String ISO639_1 = "iso639_1";

        public static final String ISO639_2B = "iso639_2b";

        public static final String ISO639_2T = "iso639_2t";

        public static final String LABEL = "label";
    }
}
