package com.geecommerce.core.system.cpanel.attribute.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.repository.AttributeTargetObjects;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

//@Cacheable
@Model("cpanel_attribute_tabs")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attributeTab")
public class DefaultAttributeTab extends AbstractModel implements AttributeTab {
    private static final long serialVersionUID = 5996844565715870568L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.CONTROL_PANEL_ID)
    private Id controlPanelId = null;

    @Column(Col.TARGET_OBJECT_ID)
    private Id targetObjectId = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.POSITION)
    private int position = 0;

    @Column(Col.SHOW_IN_VARIANT_MASTER)
    private boolean showInVariantMaster = true;

    @Column(Col.SHOW_IN_PROGRAMME)
    private boolean showInProgramme = true;

    @Column(Col.SHOW_IN_BUNDLE)
    private boolean showInBundle = true;

    @Column(Col.SHOW_IN_PRODUCT)
    private boolean showInProduct = true;

    @Column(Col.PRE_RENDER_CALLBACK)
    private String preRenderCallback = null;

    @Column(Col.POST_RENDER_CALLBACK)
    private String postRenderCallback = null;

    @Column(Col.DISPLAY_ATTRIBUTE_CALLBACK)
    private String displayAttributeCallback = null;

    @Column(Col.DISPLAY_ATTRIBUTES_CALLBACK)
    private String displayAttributesCallback = null;

    @Column(Col.ENABLED)
    private boolean enabled = false;

    // Lazy loaded attribute target object
    private transient AttributeTargetObject targetObject = null;

    // Attribute target objects repository
    private final transient AttributeTargetObjects attributeTargetObjects;

    public DefaultAttributeTab() {
        this(i(AttributeTargetObjects.class));
    }

    @Inject
    public DefaultAttributeTab(AttributeTargetObjects attributeTargetObjects) {
        this.attributeTargetObjects = attributeTargetObjects;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public AttributeTab setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getControlPanelId() {
        return controlPanelId;
    }

    @Override
    public AttributeTab setControlPanelId(Id controlPanelId) {
        this.controlPanelId = controlPanelId;
        return this;
    }

    @Override
    public Id getTargetObjectId() {
        return targetObjectId;
    }

    @Override
    public AttributeTab setTargetObjectId(Id targetObjectId) {
        this.targetObjectId = targetObjectId;
        return this;
    }

    @Override
    public AttributeTargetObject getTargetObject() {
        if (targetObjectId == null)
            return null;

        if (targetObject == null) {
            targetObject = attributeTargetObjects.findById(AttributeTargetObject.class, targetObjectId);
        }

        return targetObject;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public AttributeTab setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public AttributeTab setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public boolean isShowInVariantMaster() {
        return showInVariantMaster;
    }

    @Override
    public AttributeTab setShowInVariantMaster(boolean showInVariantMaster) {
        this.showInVariantMaster = showInVariantMaster;
        return this;
    }

    @Override
    public boolean isShowInProgramme() {
        return showInProgramme;
    }

    @Override
    public AttributeTab setShowInProgramme(boolean showInProgramme) {
        this.showInProgramme = showInProgramme;
        return this;
    }

    @Override
    public boolean isShowInBundle() {
        return showInBundle;
    }

    @Override
    public AttributeTab setShowInBundle(boolean showInBundle) {
        this.showInBundle = showInBundle;
        return this;
    }

    @Override
    public boolean isShowInProduct() {
        return showInProduct;
    }

    @Override
    public AttributeTab setShowInProduct(boolean showInProduct) {
        this.showInProduct = showInProduct;
        return this;
    }

    @Override
    public String getPreRenderCallback() {
        return preRenderCallback;
    }

    @Override
    public AttributeTab setPreRenderCallback(String preRenderCallback) {
        this.preRenderCallback = preRenderCallback;
        return this;
    }

    @Override
    public String getPostRenderCallback() {
        return postRenderCallback;
    }

    @Override
    public AttributeTab setPostRenderCallback(String postRenderCallback) {
        this.postRenderCallback = postRenderCallback;
        return this;
    }

    @Override
    public String getDisplayAttributeCallback() {
        return displayAttributeCallback;
    }

    @Override
    public AttributeTab setDisplayAttributeCallback(String displayAttributeCallback) {
        this.displayAttributeCallback = displayAttributeCallback;
        return this;
    }

    @Override
    public String getDisplayAttributesCallback() {
        return displayAttributesCallback;
    }

    @Override
    public AttributeTab setDisplayAttributesCallback(String displayAttributesCallback) {
        this.displayAttributesCallback = displayAttributesCallback;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public AttributeTab setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultAttributeTab [id=" + id + ", controlPanelId=" + controlPanelId + ", targetObjectId="
            + targetObjectId + ", label=" + label + ", position=" + position + ", showInVariantMaster="
            + showInVariantMaster + ", showInProgramme=" + showInProgramme + ", showInProduct=" + showInProduct
            + ", enabled=" + enabled + "]";
    }
}
