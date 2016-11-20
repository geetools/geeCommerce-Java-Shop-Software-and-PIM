package com.geecommerce.search.model;

import java.util.List;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

public interface Synonym extends MultiContextModel {

    Synonym setId(Id id);

    List<String> getSynonyms();

    Synonym setSynonyms(List<String> synonyms);

    String getWord();

    Synonym setWord(String word);

    Boolean getCustom();

    Synonym setCustom(Boolean custom);

    class Col {
        public static final String ID = "_id";
        public static final String WORD = "word";
        public static final String SYNONYMS = "synonyms";
        public static final String CUSTOM = "custom";
    }
}
