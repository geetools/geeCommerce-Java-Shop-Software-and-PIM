package com.geecommerce.catalog.product.model;

import java.util.Collection;
import java.util.Set;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdSupport;

public interface ProductConnectionIndex extends Model {
    public ProductConnectionIndex setId(Id id);

    public Id getProductId();

    public ProductConnectionIndex setProductId(Id productId);

    public Set<Id> getConnections();

    public ProductConnectionIndex setConnections(Collection<Id> productIds);

    public ProductConnectionIndex appendConnections(Id... productIds);

    public ProductConnectionIndex appendConnection(Id productId);

    public ProductConnectionIndex appendConnections(Collection<IdSupport> products);

    public ProductConnectionIndex appendConnection(IdSupport product);

    public Set<Id> getChildConnections();

    public ProductConnectionIndex setChildConnections(Collection<Id> productIds);

    public ProductConnectionIndex appendChildConnections(Id... productIds);

    public ProductConnectionIndex appendChildConnection(Id productId);

    public ProductConnectionIndex appendChildConnections(Collection<IdSupport> products);

    public ProductConnectionIndex appendChildConnection(IdSupport product);

    public Set<Id> getSellableChildConnections();

    public ProductConnectionIndex setSellableChildConnections(Collection<Id> productIds);

    public ProductConnectionIndex appendSellableChildConnections(Id... productIds);

    public ProductConnectionIndex appendSellableChildConnection(Id productId);

    public ProductConnectionIndex appendSellableChildConnections(Collection<IdSupport> products);

    public ProductConnectionIndex appendSellableChildConnection(IdSupport product);

    public int getUpdateFlag();

    public ProductConnectionIndex setUpdateFlag(int updateFlag);

    public static class Col {
        public static final String ID = "_id";
        public static final String PRODUCT_ID = "prd_id";
        public static final String CONNECTIONS = "conns";
        public static final String CHILD_CONNECTIONS = "c_conns";
        public static final String SELLABLE_CHILD_CONNECTIONS = "sc_conns";
        public static final String UPDATE_FLAG = "upd";
    }
}
