package com.geecommerce.guiwidgets.model;

import java.util.List;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.enums.ContentPageType;
import com.geecommerce.guiwidgets.enums.ContentType;

public interface Content extends AttributeSupport, TargetSupport {

    public Id getId();

    public Content setId(Id id);

    public ContextObject<String> getName();

    public Content setName(ContextObject<String> name);

    public ContextObject<String> getDescription();

    public Content setDescription(ContextObject<String> description);

    public String getKey();

    public Content setKey(String key);

    public ContentType getType();

    public Content setType(ContentType type);

    public ContentPageType getPageType();

    public Content setPageType(ContentPageType pageType);

    public String getTemplate();

    public Content setTemplate(String template);

    public List<ContentNode> getContentNodes();

    public Content setContentNodes(List<ContentNode> nodes);

    public List<StructureNode> getStructureNodes();

    public Content setStructureNodes(List<StructureNode> nodes);

    public Id getLayoutId();

    public Content setLayoutId(Id layoutId);

    public ContentLayout getLayout();

    public Id getPreviewProductId();

    public Content setPreviewProductId(Id previewProductId);

    static final class Col {
        public static final String ID = "_id";
        public static final String KEY = "key";
        public static final String TYPE = "type";
        public static final String PAGE_TYPE = "page_type";
        public static final String TEMPLATE = "template";
        public static final String LAYOUT = "layout";
        public static final String CONTENT_NODES = "content_nodes";
        public static final String STRUCTURE_NODES = "structure_nodes";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "descr";
        public static final String PREVIEW_PRODUCT_ID = "pr_prd_id";
    }

}
