package com.geecommerce.core.system.attribute.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface AttributeOption extends Model {
    public Id getId();

    public AttributeOption setId(Id id);

    public Id getId2();

    public AttributeOption setId2(Id id2);

    public Id getAttributeId();

    public AttributeOption setAttributeId(Id attributeId);

    public ContextObject<String> getLabel();

    public AttributeOption setLabel(ContextObject<String> label);

    public List<String> getTags();

    public AttributeOption addTag(String tag);

    public AttributeOption setTags(List<String> tags);

    public String getThumbnailColor();

    public void setThumbnailColor(String thumbnailColor);

    public String getThumbnailStyle();

    public void setThumbnailStyle(String thumbnailStyle);

    public int getPosition();

    public AttributeOption setPosition(int position);

    public AttributeOption belongsTo(Attribute attribute);

    @JsonIgnore
    public Attribute getAttribute();

    static final class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";
        public static final String ATTRIBUTE_ID = "attr_id";
        public static final String LABEL = "label";
        public static final String THUMBNAIL_COLOR = "thmb_color";
        public static final String THUMBNAIL_STYLE = "thmb_style";
        public static final String TAGS = "tags";
        public static final String POSITION = "pos";
    }
}
