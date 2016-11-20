package com.geecommerce.search.model;

import java.util.List;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

@Model("search_synonyms")
public class DefaultSynonym extends AbstractMultiContextModel implements Synonym {

    @Column(Col.ID)
    private Id id;

    @Column(Col.WORD)
    private String word;

    @Column(Col.SYNONYMS)
    private List<String> synonyms;

    @Column(Col.CUSTOM)
    private Boolean custom;

    @Override
    public Synonym setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public List<String> getSynonyms() {
        return synonyms;
    }

    @Override
    public Synonym setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
        return this;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public Synonym setWord(String word) {
        this.word = word;
        return this;
    }

    @Override
    public Boolean getCustom() {
        return custom;
    }

    @Override
    public Synonym setCustom(Boolean custom) {
        this.custom = custom;
        return this;
    }
}
