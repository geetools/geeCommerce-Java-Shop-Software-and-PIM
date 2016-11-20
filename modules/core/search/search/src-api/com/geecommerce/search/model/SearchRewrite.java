package com.geecommerce.search.model;

import java.util.List;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

/**
 * Created by Andrey on 05.10.2015.
 */
public interface SearchRewrite extends MultiContextModel {

    SearchRewrite setId(Id id);

    List<String> getKeywords();

    SearchRewrite setKeywords(List<String> keywords);

    String getTargetUri();

    SearchRewrite setTargetUri(String targetUri);

    class Col {
        public static final String ID = "_id";
        public static final String KEYWORDS = "keywords";
        public static final String TARGET_URI = "target_uri";
    }
}
