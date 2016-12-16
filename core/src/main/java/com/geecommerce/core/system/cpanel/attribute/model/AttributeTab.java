package com.geecommerce.core.system.cpanel.attribute.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface AttributeTab extends Model {
    public Id getId();

    public AttributeTab setId(Id id);

    public Id getControlPanelId();

    public AttributeTab setControlPanelId(Id controlPanelId);

    public Id getTargetObjectId();

    public AttributeTab setTargetObjectId(Id targetObjectId);

    public AttributeTargetObject getTargetObject();

    public ContextObject<String> getLabel();

    public AttributeTab setLabel(ContextObject<String> label);

    public int getPosition();

    public AttributeTab setPosition(int position);

    public boolean isShowInVariantMaster();

    public AttributeTab setShowInVariantMaster(boolean showInVariantMaster);

    public boolean isShowInProgramme();

    public AttributeTab setShowInProgramme(boolean showInProgramme);

    public boolean isShowInBundle();

    public AttributeTab setShowInBundle(boolean showInBundle);

    public boolean isShowInProduct();

    public AttributeTab setShowInProduct(boolean showInProduct);

    public String getPreRenderCallback();

    public AttributeTab setPreRenderCallback(String preRenderCallback);

    public String getPostRenderCallback();

    public AttributeTab setPostRenderCallback(String postRenderCallback);

    public String getDisplayAttributeCallback();

    public AttributeTab setDisplayAttributeCallback(String displayAttributeCallback);

    public String getDisplayAttributesCallback();

    public AttributeTab setDisplayAttributesCallback(String displayAttributesCallback);

    public boolean isEnabled();

    public AttributeTab setEnabled(boolean enabled);

    static final class Col {
        public static final String ID = "_id";
        public static final String CONTROL_PANEL_ID = "cpanel_id";
        public static final String TARGET_OBJECT_ID = "tar_obj";
        public static final String LABEL = "label";
        public static final String POSITION = "pos";
        public static final String SHOW_IN_VARIANT_MASTER = "showInVM";
        public static final String SHOW_IN_PROGRAMME = "ShowInPG";
        public static final String SHOW_IN_BUNDLE = "ShowInBL";
        public static final String SHOW_IN_PRODUCT = "showInPD";
        public static final String PRE_RENDER_CALLBACK = "preRenderCallback";
        public static final String POST_RENDER_CALLBACK = "postRenderCallback";
        public static final String DISPLAY_ATTRIBUTE_CALLBACK = "displayAttributeCallback";
        public static final String DISPLAY_ATTRIBUTES_CALLBACK = "displayAttributesCallback";
        public static final String ENABLED = "enabled";
    }
}
