package com.geecommerce.core.system.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Inject;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "language")
@Model(collection = "_languages", fieldAccess = true)
public class DefaultLanguage extends AbstractModel implements Language {
    private static final long serialVersionUID = -411689558670930520L;

    @Column(GlobalColumn.ID)
    private Id id = null;

    @Column(Col.ISO639_1)
    private String iso6391Code = null;

    @Column(Col.ISO639_2B)
    private String iso6392BCode = null;

    @Column(Col.ISO639_2T)
    private String iso6392TCode = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Inject
    public DefaultLanguage() {

    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public String getIso6391Code() {
        return iso6391Code;
    }

    @Override
    public String getIso6392BCode() {
        return iso6392BCode;
    }

    @Override
    public String getIso6392TCode() {
        return iso6392TCode;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "DefaultLanguage [id=" + id + ", iso6391Code=" + iso6391Code + ", iso6392BCode=" + iso6392BCode + ", iso6392TCode=" + iso6392TCode + ", label=" + label + "]";
    }
}
