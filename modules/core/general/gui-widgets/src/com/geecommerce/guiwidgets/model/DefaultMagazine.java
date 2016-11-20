package com.geecommerce.guiwidgets.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model("magazines")
public class DefaultMagazine extends AbstractMultiContextModel implements Magazine {
    private static final long serialVersionUID = 1L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.TITLE)
    private ContextObject<String> title = null;

    @Column(Col.TEASER_TEXT)
    private ContextObject<String> teaserText = null;

    @Column(Col.TEASER_IMAGE)
    private Id teaserImageId = null;

    private MediaAsset teaserImage = null;

    @Column(Col.SHOW_FROM)
    private Date showFrom = null;

    @Column(Col.SHOW_TO)
    private Date showTo = null;

    @Column(Col.VALID_FROM)
    private Date validFrom = null;

    @Column(Col.VALID_TO)
    private Date validTo = null;

    @Column(Col.PAGES)
    private List<Page> pages = new ArrayList<Page>();

    @Column(Col.ENABLED)
    private ContextObject<Boolean> enabled;

    private final MediaAssetService mediaAssetService;

    @Inject
    public DefaultMagazine(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Magazine setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Magazine setTitle(ContextObject<String> title) {
        this.title = title;
        return this;
    }

    @Override
    public ContextObject<String> getTitle() {
        return title;
    }

    @Override
    public Magazine setTeaserText(ContextObject<String> teaserText) {
        this.teaserText = teaserText;
        return this;
    }

    @Override
    public ContextObject<String> getTeaserText() {
        return teaserText;
    }

    @Override
    public MediaAsset getTeaserImage() {
        if (teaserImage == null && teaserImageId != null)
            teaserImage = mediaAssetService.get(teaserImageId);
        return teaserImage;
    }

    @Override
    public Magazine setTeaserImage(MediaAsset teaserImage) {
        if (teaserImage == null) {
            this.teaserImage = null;
            this.teaserImageId = null;
        } else {
            this.teaserImage = teaserImage;
            this.teaserImageId = teaserImage.getId();
        }

        return this;
    }

    @Override
    public Date getShowFrom() {
        return showFrom;
    }

    @Override
    public Magazine setShowFrom(Date showFrom) {
        this.showFrom = showFrom;
        return this;
    }

    @Override
    public Date getShowTo() {
        return showTo;
    }

    @Override
    public Magazine setShowTo(Date showTo) {
        this.showTo = showTo;
        return this;
    }

    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    @Override
    public Magazine setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    @Override
    public Date getValidTo() {
        return validTo;
    }

    @Override
    public Magazine setValidTo(Date validTo) {
        this.validTo = validTo;
        return this;
    }

    @JsonIgnore
    @Override
    public Page getPreviewPage() {
        if (getPages() == null || getPages().size() == 0)
            return null;
        for (Page page : getPages()) {
            if (page.getPreview())
                return page;
        }
        return getPages().get(0);
    }

    @Override
    public List<Page> getPages() {
        if (pages == null)
            pages = new ArrayList<>();

        return pages;
    }

    @Override
    public Magazine setPages(List<Page> pages) {
        this.pages = pages;
        return this;
    }

    @JsonIgnore
    @Override
    public Long getLeftDays() {
        long difference = getValidTo().getTime() - new Date().getTime();
        long leftDays = difference / (24 * 60 * 60 * 1000) + 1;
        return leftDays;
    }

    @Override
    public ContextObject<Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public Magazine setEnabled(ContextObject<Boolean> enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        this.id = id_(map.get(Col.ID));

        if (map.get(Col.TITLE) != null)
            this.title = ctxObj_(map.get(Col.TITLE));
        if (map.get(Col.TEASER_TEXT) != null)
            this.teaserText = ctxObj_(map.get(Col.TEASER_TEXT));
        if (map.get(Col.TEASER_IMAGE) != null)
            this.teaserImageId = id_(map.get(Col.TEASER_IMAGE));
        if (map.get(Col.SHOW_FROM) != null)
            this.showFrom = date_(map.get(Col.SHOW_FROM));
        if (map.get(Col.SHOW_TO) != null)
            this.showTo = date_(map.get(Col.SHOW_TO));
        if (map.get(Col.VALID_FROM) != null)
            this.validFrom = date_(map.get(Col.VALID_FROM));
        if (map.get(Col.VALID_TO) != null)
            this.validTo = date_(map.get(Col.VALID_TO));

        this.enabled = ctxObj_(map.get(Col.ENABLED));

        List<Map<String, Object>> pagesList = list_(map.get(Col.PAGES));
        if (pagesList != null) {
            this.pages = new ArrayList<>();
            for (Map<String, Object> page : pagesList) {
                Page p = app.model(Page.class);
                p.fromMap(page);
                this.pages.add(p);
            }

            // Make sure that the pages are sorted in the correct order.
            Collections.sort(pages, (p1, p2) -> p1.getPosition().compareTo(p2.getPosition()));
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.ID, getId());

        if (getTitle() != null)
            map.put(Col.TITLE, getTitle());
        if (getTeaserText() != null)
            map.put(Col.TEASER_TEXT, getTeaserText());
        if (teaserImageId != null)
            map.put(Col.TEASER_IMAGE, teaserImageId);
        if (getShowFrom() != null)
            map.put(Col.SHOW_FROM, getShowFrom());
        if (getShowTo() != null)
            map.put(Col.SHOW_TO, getShowTo());
        if (getValidFrom() != null)
            map.put(Col.VALID_FROM, getValidFrom());
        if (getValidTo() != null)
            map.put(Col.VALID_TO, getValidTo());

        map.put(Col.ENABLED, getEnabled());

        List<Map<String, Object>> pagesList = new ArrayList<>();
        if (getPages() != null) {
            for (Page page : getPages()) {
                pagesList.add(page.toMap());
            }
            map.put(Col.PAGES, pagesList);
        }

        return map;
    }
}
