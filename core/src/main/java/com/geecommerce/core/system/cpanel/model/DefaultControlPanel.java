package com.geecommerce.core.system.cpanel.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractMultiContextModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

@Cacheable
@Model("control_panels")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "controlPanel")
public class DefaultControlPanel extends AbstractMultiContextModel implements ControlPanel {
    private static final long serialVersionUID = 5996844565715870568L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.LABEL)
    private ContextObject<String> label = null;

    @Column(Col.ENABLED)
    private boolean enabled = false;

    public DefaultControlPanel() {
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ControlPanel setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public ContextObject<String> getLabel() {
        return label;
    }

    @Override
    public ControlPanel setLabel(ContextObject<String> label) {
        this.label = label;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public ControlPanel setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultControlPanel [id=" + id + ", label=" + label + ", enabled=" + enabled + "]";
    }
}
