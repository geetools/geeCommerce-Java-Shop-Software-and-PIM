package com.geecommerce.guiwidgets.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model(collection = "web_slideshow", optimisticLocking = true)
public class DefaultWebSlideShow extends AbstractMultiContextModel implements WebSlideShow {

    private Id id = null;

    @Column(Col.NAME)
    private String name = null;

    @Column(Col.DATE_FROM)
    private Date dateFrom = null;

    @Column(Col.ENABLED)
    private ContextObject<Boolean> enabled;

    @Column(Col.DATE_TO)
    private Date dateTo = null;

    @Column(name = Col.SLIDES, autoPopulate = false)
    private List<Slide> slides = new ArrayList<>();

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public WebSlideShow setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WebSlideShow setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public List<Slide> getSlides() {
        if (slides == null)
            slides = new ArrayList<>();
        return slides;
    }

    @Override
    public WebSlideShow addSlide(Slide slide) {
        slides.add(slide);
        return this;
    }

    @Override
    public Slide findSlide(Id slideId) {
        if (this.slides != null && !this.slides.isEmpty()) {
            for (Slide slide : this.slides) {
                if (slideId.equals(slide.getId()))
                    return slide;
            }
        }
        return null;
    }

    @Override
    public Date getDateFrom() {
        return dateFrom;
    }

    @Override
    public WebSlideShow setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
        return this;
    }

    @Override
    public Date getDateTo() {
        return dateTo;
    }

    @Override
    public WebSlideShow setDateTo(Date dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    @Override
    public ContextObject<Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public WebSlideShow setEnabled(ContextObject<Boolean> enabled) {
        this.enabled = enabled;
        return this;
    }

    protected boolean slideExists(Id slideId) {
        if (this.slides != null && !this.slides.isEmpty()) {
            for (Slide slide : this.slides) {
                if (slideId.equals(slide.getId()))
                    return true;
            }
        }
        return false;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));
        this.name = str_(map.get(Col.NAME));
        this.dateFrom = date_(map.get(Col.DATE_FROM));
        this.dateTo = date_(map.get(Col.DATE_TO));
        this.enabled = ctxObj_(map.get(Col.ENABLED));

        List<Map<String, Object>> slidesList = list_(map.get(Col.SLIDES));
        if (slidesList != null) {
            this.slides = new ArrayList<>();
            for (Map<String, Object> slide : slidesList) {
                Slide s = app.model(Slide.class);
                s.fromMap(slide);
                this.slides.add(s);
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());
        map.put(Col.ID, getId());
        map.put(Col.NAME, getName());
        map.put(Col.DATE_FROM, getDateFrom());
        map.put(Col.DATE_TO, getDateTo());
        map.put(Col.ENABLED, getEnabled());

        List<Map<String, Object>> slidesList = new ArrayList<>();
        if (getSlides() != null) {
            for (Slide slide : getSlides()) {
                slidesList.add(slide.toMap());
            }
            map.put(Col.SLIDES, slidesList);
        }

        return map;
    }
}
