package com.geecommerce.retail.model;

import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.type.Id;

public interface RetailStoreDistance extends MultiContextModel {
    public Id getId();

    public RetailStoreDistance setId(Id id);

    public String getFromZip();

    public RetailStoreDistance setFromZip(String fromZip);

    public String getFromCity();

    public RetailStoreDistance setFromCity(String fromCity);

    public Id getToRetailStore();

    public RetailStoreDistance setToRetailStore(Id toRetailStore);

    public Integer getDistance();

    public RetailStoreDistance setDistance(Integer distance);

    static final class Column {
        public static final String ID = "_id";
        public static final String FROM_ZIP = "from_zip";
        public static final String FROM_CITY = "from_city";
        public static final String TO_RETAIL_STORE = "to_retail_store";
        public static final String DISTANCE = "distance";
    }

}
