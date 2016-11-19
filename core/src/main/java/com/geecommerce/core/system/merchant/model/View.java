package com.geecommerce.core.system.merchant.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public interface View extends Model {
    public Id getId();

    public String getName();

    public String getCode();

    public Id getParentViewId();

    public View belongsTo(Merchant merchant);

    public String getTemplatesPath();

    public String getResourcesPath();

    public String getCertsPath();

    static final class Column {
	public static final String ID = "_id";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String PARENT_VIEW_ID = "p_view_id";
    }
}
