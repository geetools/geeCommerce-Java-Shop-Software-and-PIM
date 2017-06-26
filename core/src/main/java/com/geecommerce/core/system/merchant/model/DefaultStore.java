package com.geecommerce.core.system.merchant.model;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.core.Char;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.utils.Filenames;
import com.owlike.genson.annotation.JsonIgnore;

@Model(collection = "stores", fieldAccess = true)
public class DefaultStore extends AbstractModel implements Store {
    private static final long serialVersionUID = 2350896018677985822L;
    @Column(Col.ID)
    private Id id = null;
    @Column(Col.CODE)
    private String code = null;
    @Column(Col.NAME)
    private String name = null;
    @Column(Col.DEFAULT_LANGUAGE)
    private String defaultLanguage = null;
    @Column(Col.ICON_PATH_XS)
    private String iconPathXS = null;
    @Column(Col.ICON_PATH_S)
    private String iconPathS = null;
    @Column(Col.PARENT_STORE_ID)
    private Id parentStoreId = null;

    private Merchant merchant = null;

    public DefaultStore() {
        super();
    }

    public DefaultStore(final Id id, final String code, final String name, final String defaultLanguage,
        final String iconPathXS, final String iconPathS, Id parentStoreId) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.defaultLanguage = defaultLanguage;
        this.iconPathXS = iconPathXS;
        this.iconPathS = iconPathS;
        this.parentStoreId = parentStoreId;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Store setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Store setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Store setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public Store setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return this;
    }

    @Override
    public String getIconPathXS() {
        return iconPathXS;
    }

    @Override
    public Store setIconPathXS(String iconPathXS) {
        this.iconPathXS = iconPathXS;
        return this;
    }

    @Override
    public String getIconPathS() {
        return iconPathS;
    }

    @Override
    public Store setIconPathS(String iconPathS) {
        this.iconPathS = iconPathS;
        return this;
    }

    @Override
    public Id getParentStoreId() {
        return parentStoreId;
    }

    @Override
    public Store setParentStoreId(Id parentStoreId) {
        this.parentStoreId = parentStoreId;
        return this;
    }

    @Override
    public final Store belongsTo(Merchant merchant) {
        this.merchant = merchant;
        return this;
    }

    @JsonIgnore
    @Override
    public final String getTemplatesPath() {
        if (merchant == null)
            return null;

        return new StringBuilder(merchant.getTemplatesPath()).append(File.separatorChar).append(Char.UNDERSCORE)
            .append(Filenames.ensureSafeName(getCode() != null ? getCode() : getName(), true)).toString();
    }

    @JsonIgnore
    @Override
    public final String getResourcesPath() {
        if (merchant == null)
            return null;

        return new StringBuilder(merchant.getResourcesPath()).append(File.separatorChar).append(Char.UNDERSCORE)
            .append(Filenames.ensureSafeName(getCode() != null ? getCode() : getName(), true)).toString();
    }

    @JsonIgnore
    @Override
    public final String getCertsPath() {
        String rp = getResourcesPath();

        return rp == null ? null
            : new StringBuilder(getResourcesPath()).append(File.separatorChar).append("certs").toString();
    }

    @Override
    public final void fromMap(Map<String, Object> map) {
        this.id = id_(map.get(Col.ID));
        this.code = str_(map.get(Col.CODE));
        this.name = str_(map.get(Col.NAME));
        this.defaultLanguage = str_(map.get(Col.DEFAULT_LANGUAGE));
        this.iconPathXS = str_(map.get(Col.ICON_PATH_XS));
        this.iconPathS = str_(map.get(Col.ICON_PATH_S));
        this.parentStoreId = id_(map.get(Col.PARENT_STORE_ID));
    }

    @Override
    public final Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        map.put(Col.ID, getId());
        map.put(Col.CODE, getCode());
        map.put(Col.NAME, getName());
        map.put(Col.DEFAULT_LANGUAGE, getDefaultLanguage());
        map.put(Col.ICON_PATH_XS, getIconPathXS());
        map.put(Col.ICON_PATH_S, getIconPathS());
        map.put(Col.PARENT_STORE_ID, getParentStoreId());

        return map;
    }
}
