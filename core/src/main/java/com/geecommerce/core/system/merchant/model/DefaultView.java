package com.geecommerce.core.system.merchant.model;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.utils.Filenames;

public class DefaultView extends AbstractModel implements View {
    private static final long serialVersionUID = -3328975975809087586L;
    private Id id = null;
    private String code = null;
    private String name = null;
    private Id parentViewId = null;

    private Merchant merchant = null;

    public DefaultView() {
        super();
    }

    public DefaultView(final Id id, final String code, final String name, final Id parentViewId) {
        super();
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentViewId = parentViewId;
    }

    @Override
    public final Id getId() {
        return id;
    }

    @Override
    public final String getCode() {
        return code;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public Id getParentViewId() {
        return parentViewId;
    }

    @Override
    public final View belongsTo(Merchant merchant) {
        this.merchant = merchant;
        return this;
    }

    @Override
    public final String getTemplatesPath() {
        if (merchant == null)
            return null;

        ApplicationContext appCtx = app.context();

        if (appCtx == null)
            return null;

        Store s = merchant.getStoreFor(appCtx.getRequestContext());

        if (s == null)
            return null;

        return new StringBuilder(s.getTemplatesPath()).append(File.separatorChar).append(Char.UNDERSCORE)
            .append(Filenames.ensureSafeName(getCode() != null ? getCode() : getName(), true)).toString();
    }

    @Override
    public final String getResourcesPath() {
        if (merchant == null)
            return null;

        ApplicationContext appCtx = app.context();

        if (appCtx == null)
            return null;

        Store s = merchant.getStoreFor(appCtx.getRequestContext());

        if (s == null)
            return null;

        return new StringBuilder(s.getResourcesPath()).append(File.separatorChar).append(Char.UNDERSCORE)
            .append(Filenames.ensureSafeName(getCode() != null ? getCode() : getName(), true)).toString();
    }

    @Override
    public final String getCertsPath() {
        String rp = getResourcesPath();

        return rp == null ? null
            : new StringBuilder(getResourcesPath()).append(File.separatorChar).append("certs").toString();
    }

    @Override
    public final void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Column.ID));
        this.code = str_(map.get(Column.CODE));
        this.name = str_(map.get(Column.NAME));
        this.parentViewId = id_(map.get(Column.PARENT_VIEW_ID));
    }

    @Override
    public final Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put(Column.ID, getId());
        map.put(Column.CODE, getCode());
        map.put(Column.NAME, getName());
        map.put(Column.PARENT_VIEW_ID, getParentViewId());

        return map;
    }

    @Override
    public String toString() {
        return "DefaultView [id=" + id + ", code=" + code + ", name=" + name + ", parentViewId=" + parentViewId + "]";
    }
}
