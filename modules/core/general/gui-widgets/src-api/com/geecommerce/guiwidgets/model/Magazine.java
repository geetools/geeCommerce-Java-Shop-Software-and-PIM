package com.geecommerce.guiwidgets.model;

import java.util.Date;
import java.util.List;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.owlike.genson.annotation.JsonIgnore;

public interface Magazine extends MultiContextModel {
    public Id getId();

    public Magazine setId(Id id);

    public ContextObject<String> getTitle();

    public Magazine setTitle(ContextObject<String> title);

    public ContextObject<String> getTeaserText();

    public Magazine setTeaserText(ContextObject<String> teaser);

    public MediaAsset getTeaserImage();

    public Magazine setTeaserImage(MediaAsset teaserImage);

    public Date getShowFrom();

    public Magazine setShowFrom(Date showFrom);

    public Date getShowTo();

    public Magazine setShowTo(Date showTo);

    public Date getValidFrom();

    public Magazine setValidFrom(Date validFrom);

    public Date getValidTo();

    public Magazine setValidTo(Date validTo);

    @JsonIgnore
    public Page getPreviewPage();

    public List<Page> getPages();

    public Magazine setPages(List<Page> pages);

    @JsonIgnore
    public Long getLeftDays();

    public ContextObject<Boolean> getEnabled();

    public Magazine setEnabled(ContextObject<Boolean> enabled);

    static final class Col {
        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String TEASER_IMAGE = "teaser_image";
        public static final String TEASER_TEXT = "teaser_text";
        public static final String SHOW_FROM = "show_from";
        public static final String SHOW_TO = "show_to";
        public static final String VALID_FROM = "valid_from";
        public static final String VALID_TO = "valid_to";
        public static final String PAGES = "pages";
        public static final String ENABLED = "enabled";
    }

}
