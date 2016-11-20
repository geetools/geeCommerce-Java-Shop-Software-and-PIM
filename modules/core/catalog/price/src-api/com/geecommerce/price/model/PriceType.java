package com.geecommerce.price.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;

public interface PriceType extends MultiContextModel {
    public Id getId();

    public PriceType setId(Id id);

    public String getId2();

    public PriceType setId2(String id2);

    public String getCode();

    public PriceType setCode(String code);

    public String getErpCode();

    public PriceType setErpCode(String erpCode);

    public ContextObject<String> getLabel();

    public PriceType setLabel(ContextObject<String> label);

    public int getPriority();

    public PriceType setPriority(int priority);

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    static final class Col {
        public static final String ID = "_id";
        public static final String ID2 = "id2";
        public static final String CODE = "code";
        public static final String ERP_CODE = "erp_code";
        public static final String LABEL = "label";
        public static final String PRIORITY = "pri";
        public static final String ENABLED = "enabled";
    }
}
