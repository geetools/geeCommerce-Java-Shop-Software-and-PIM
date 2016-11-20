package com.geecommerce.guiwidgets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractAttributeSupport;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.enums.ContentPageType;
import com.geecommerce.guiwidgets.enums.ContentType;
import com.geecommerce.guiwidgets.repository.ContentLayouts;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Model(collection = "contents", history = true)
public class DefaultContent extends AbstractAttributeSupport implements Content {

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.KEY)
    private String key = null;

    @Column(Col.TYPE)
    private ContentType type = null;

    @Column(Col.PAGE_TYPE)
    private ContentPageType pageType = null;

    @Column(Col.TEMPLATE)
    private String template = null;

    @Column(Col.LAYOUT)
    private Id layoutId = null;

    @Column(Col.NAME)
    private ContextObject<String> name = null;

    @Column(Col.DESCRIPTION)
    private ContextObject<String> description = null;

    @Column(Col.PREVIEW_PRODUCT_ID)
    private Id previewProductId = null;

    @JsonIgnore
    private ContextObject<String> uri = null;

    private ContentLayout layout = null;

    @Column(name = Col.CONTENT_NODES, autoPopulate = false)
    private List<ContentNode> contentNodes = null;
    @Column(name = Col.STRUCTURE_NODES, autoPopulate = false)
    private List<StructureNode> structureNodes = null;
    private final ContentLayouts contentLayouts;

    @Inject
    public DefaultContent(ContentLayouts contentLayouts) {
        this.contentLayouts = contentLayouts;
    }

    @Override
    public Content setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ContextObject<String> getName() {
        return name;
    }

    @Override
    public Content setName(ContextObject<String> name) {
        this.name = name;
        return this;
    }

    @Override
    public ContextObject<String> getDescription() {
        return description;
    }

    @Override
    public Content setDescription(ContextObject<String> description) {
        this.description = description;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Content setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public ContentType getType() {
        return type;
    }

    @Override
    public Content setType(ContentType type) {
        this.type = type;
        return this;
    }

    @Override
    public ContentPageType getPageType() {
        return pageType;
    }

    @Override
    public Content setPageType(ContentPageType pageType) {
        this.pageType = pageType;
        return this;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public Content setTemplate(String template) {
        this.template = template;
        return this;
    }

    @Override
    public List<ContentNode> getContentNodes() {
        return contentNodes;
    }

    @Override
    public Content setContentNodes(List<ContentNode> nodes) {
        this.contentNodes = nodes;
        return this;
    }

    @Override
    public List<StructureNode> getStructureNodes() {
        return structureNodes;
    }

    @Override
    public Content setStructureNodes(List<StructureNode> nodes) {
        this.structureNodes = nodes;
        return this;
    }

    @Override
    public Id getLayoutId() {
        return layoutId;
    }

    @Override
    public Content setLayoutId(Id layoutId) {
        this.layoutId = layoutId;
        this.layout = null;
        return this;
    }

    @Override
    public ContentLayout getLayout() {
        if (layout == null && layoutId != null) {
            layout = contentLayouts.findById(ContentLayout.class, layoutId);
        }
        return layout;
    }

    @Override
    public Id getPreviewProductId() {
        return previewProductId;
    }

    @Override
    public Content setPreviewProductId(Id previewProductId) {
        this.previewProductId = previewProductId;
        return this;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        if (map.containsKey(Col.TYPE))
            this.type = enum_(ContentType.class, map.get(Col.TYPE));

        if (map.containsKey(Col.PAGE_TYPE))
            this.pageType = enum_(ContentPageType.class, map.get(Col.PAGE_TYPE));

        if (map.containsKey(Col.CONTENT_NODES)) {
            this.contentNodes = new ArrayList<>();
            List<Map<String, Object>> nodesList = list_(map.get(Col.CONTENT_NODES));
            if (nodesList != null) {
                for (Map<String, Object> node : nodesList) {
                    ContentNode n = app.model(ContentNode.class);
                    n.fromMap(node);
                    this.contentNodes.add(n);
                }
            }
        }

        if (map.containsKey(Col.STRUCTURE_NODES)) {
            this.structureNodes = new ArrayList<>();
            List<Map<String, Object>> nodesList = list_(map.get(Col.STRUCTURE_NODES));
            if (nodesList != null) {
                for (Map<String, Object> node : nodesList) {
                    StructureNode n = app.model(StructureNode.class);
                    n.fromMap(node);
                    this.structureNodes.add(n);
                }
            }
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        if (getType() != null)
            map.put(Col.TYPE, getType().toId());

        if (getPageType() != null)
            map.put(Col.PAGE_TYPE, getPageType().toId());

        List<Map<String, Object>> nodesList = new ArrayList<>();
        if (getContentNodes() != null) {
            for (ContentNode node : getContentNodes()) {
                nodesList.add(node.toMap());
            }
            map.put(Col.CONTENT_NODES, nodesList);
        }

        nodesList = new ArrayList<>();
        if (getStructureNodes() != null) {
            for (StructureNode node : getStructureNodes()) {
                nodesList.add(node.toMap());
            }
            map.put(Col.STRUCTURE_NODES, nodesList);
        }

        return map;
    }

    @Override
    public ContextObject<String> getLabel() {
        return null;
    }

    @Override
    public ContextObject<String> getURI() {
        if (uri == null) {
            // UrlRewrite urlRewrite = urlRewrites.forProduct(getId());

            // if (urlRewrite != null)
            // uri = urlRewrite.getRequestURI();

            if (uri == null)
                uri = new ContextObject<String>();

            if (!uri.hasGlobalEntry())
                uri.addOrUpdateGlobal("/content/page/" + getId());
        }

        return uri;
    }
}
