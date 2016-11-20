package com.geecommerce.core.system.attribute.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Cacheable
@Model("attribute_options")
@XmlRootElement(name = "attribute-option")
@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultAttributeOption extends AbstractModel implements AttributeOption {
    private static final long serialVersionUID = -474967354073929013L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.ID2)
    private Id id2 = null;

    @Column(Col.ATTRIBUTE_ID)
    private Id attributeId = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.TAGS)
    private List<String> tags = null;

    @Column(Col.THUMBNAIL_COLOR)
    private String thumbnailColor = null;

    @Column(Col.THUMBNAIL_STYLE)
    private String thumbnailStyle = null;

    @Column(Col.POSITION)
    private int position = 0;

    // Loaded lazily if it does not exist
    private transient Attribute belongsToAttribute = null;

    // Attribute repository
    private final transient Attributes attributes;

    public DefaultAttributeOption() {
        this(i(Attributes.class));
    }

    @Inject
    public DefaultAttributeOption(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public AttributeOption setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getId2() {
        return id2;
    }

    @Override
    public AttributeOption setId2(Id id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public Id getAttributeId() {
        if (attributeId == null && belongsToAttribute != null) {
            attributeId = belongsToAttribute.getId();
        }

        return attributeId;
    }

    @Override
    public AttributeOption setAttributeId(Id attributeId) {
        this.attributeId = attributeId;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public AttributeOption setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public List<String> getTags() {
        return tags;
    }

    @Override
    public AttributeOption setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    @Override
    public AttributeOption addTag(String tag) {
        if (tags == null)
            tags = new ArrayList<>();

        tags.add(tag);

        return this;
    }

    @Override
    public String getThumbnailColor() {
        return thumbnailColor;
    }

    @Override
    public void setThumbnailColor(String thumbnailColor) {
        this.thumbnailColor = thumbnailColor;
    }

    @Override
    public String getThumbnailStyle() {
        return thumbnailStyle;
    }

    @Override
    public void setThumbnailStyle(String thumbnailStyle) {
        this.thumbnailStyle = thumbnailStyle;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public AttributeOption setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public AttributeOption belongsTo(Attribute attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute cannot be null in AttributeOption.");

        this.belongsToAttribute = attribute;

        return this;
    }

    @JsonIgnore
    @Override
    public Attribute getAttribute() {
        if (belongsToAttribute == null) {
            belongsToAttribute = attributes.findById(Attribute.class, attributeId);
        }

        return belongsToAttribute;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        this.id = id_(map.get(Col.ID));
        this.id2 = id_(map.get(Col.ID2));
        this.attributeId = id_(map.get(Col.ATTRIBUTE_ID));
        this.label = ctxObj_(map.get(Col.LABEL));
        this.thumbnailColor = str_(map.get(Col.THUMBNAIL_COLOR));
        this.thumbnailStyle = str_(map.get(Col.THUMBNAIL_STYLE));
        this.tags = list_(map.get(Col.TAGS));
        this.position = int_(map.get(Col.POSITION)) == null ? 0 : int_(map.get(Col.POSITION));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        if (getId() != null) {
            map.put(Col.ID, getId());
        }

        if (getId2() != null) {
            map.put(Col.ID2, getId2());
        }

        if (getAttributeId() == null) {
            throw new IllegalArgumentException("AttributeId cannot be null when saving attribute-option.");
        }

        map.put(Col.ATTRIBUTE_ID, getAttributeId());
        map.put(Col.LABEL, getLabel());
        map.put(Col.THUMBNAIL_COLOR, getThumbnailColor());
        map.put(Col.THUMBNAIL_STYLE, getThumbnailStyle());
        map.put(Col.POSITION, getPosition());

        if (getTags() != null) {
            map.put(Col.TAGS, getTags());
        }

        return map;
    }

    @Override
    public String toString() {
        return "DefaultAttributeOption [id=" + id + ", id2=" + id2 + ", attributeId=" + attributeId + ", label=" + label
            + ", tags=" + tags + ", position=" + position + "]";
    }
}
