package com.geecommerce.guiwidgets.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface WebSlideShow extends MultiContextModel {
    public Id getId();

    public WebSlideShow setId(Id id);

    public String getName();

    public WebSlideShow setName(String name);

    public List<Slide> getSlides();

    public WebSlideShow addSlide(Slide slide);

    public Slide findSlide(Id slideId);

    public Date getDateFrom();

    public WebSlideShow setDateFrom(Date dateFrom);

    public Date getDateTo();

    public WebSlideShow setDateTo(Date dateTo);

    public ContextObject<Boolean> getEnabled();

    public WebSlideShow setEnabled(ContextObject<Boolean> enabled);

    static final class Col {
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String LABEL = "label";
	public static final String SLIDES = "slides";
	public static final String DATE_FROM = "d_from";
	public static final String DATE_TO = "d_to";
	public static final String ENABLED = "enabled";
    }

}
