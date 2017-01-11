package com.geecommerce.mediaassets.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.App;
import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.repository.MediaAssetDirectories;
import com.google.inject.Inject;

@XmlRootElement(name = "mediaAssetDirectory")
@XmlAccessorType(XmlAccessType.FIELD)
@Model("media_asset_directories")
public class DefaultMediaAssetDirectory extends AbstractAttributeSupport implements MediaAssetDirectory {
    private static final long serialVersionUID = -6499739421456471446L;

    @Inject
    protected App app;

    protected final MediaAssetDirectories mediaAssetDirectories;

    @Column(Col.ID)
    protected Id id = null;

    @Column(Col.NAME)
    protected ContextObject<String> name = null;

    @Column(Col.KEY)
    protected String key = null;

    @Column(Col.PARENT_ID)
    protected Id parentId = null;

    public DefaultMediaAssetDirectory() {
        this.mediaAssetDirectories = i(MediaAssetDirectories.class);
    }

    @Inject
    public DefaultMediaAssetDirectory(MediaAssetDirectories mediaAssetDirectories) {
        this.mediaAssetDirectories = mediaAssetDirectories;
    }

    @Override
    public MediaAssetDirectory setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public MediaAssetDirectory setName(ContextObject<String> name) {
        this.name = name;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public MediaAssetDirectory setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public Id getParentId() {
        return parentId;
    }

    @Override
    public MediaAssetDirectory setParentId(Id parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public List<MediaAssetDirectory> getChildren() {
        List<MediaAssetDirectory> children = mediaAssetDirectories.havingParent(getId());
        return children;
    }

    @Override
    public Id getId() {
        return id;
    }
}
