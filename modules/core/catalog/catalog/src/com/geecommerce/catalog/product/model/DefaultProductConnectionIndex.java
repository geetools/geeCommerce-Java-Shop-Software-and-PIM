package com.geecommerce.catalog.product.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdSupport;

@Cacheable
@Model(collection = "idx_product_connections", context = "store")
public class DefaultProductConnectionIndex extends AbstractModel implements ProductConnectionIndex {
    private static final long serialVersionUID = -2020976818762903454L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.PRODUCT_ID)
    private Id productId = null;

    @Column(Col.CONNECTIONS)
    private Set<Id> connections = null;

    @Column(Col.CHILD_CONNECTIONS)
    private Set<Id> childConnections = null;

    @Column(Col.SELLABLE_CHILD_CONNECTIONS)
    private Set<Id> sellableChildConnections = null;

    @Column(Col.UPDATE_FLAG)
    private int updateFlag = 0;

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public ProductConnectionIndex setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public ProductConnectionIndex setProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public Set<Id> getConnections() {
        return connections;
    }

    @Override
    public ProductConnectionIndex setConnections(Collection<Id> productIds) {
        this.connections = new HashSet<Id>(productIds);
        return this;
    }

    @Override
    public ProductConnectionIndex appendConnections(Id... productIds) {
        if (productIds == null || productIds.length == 0)
            return this;

        if (this.connections == null)
            this.connections = new HashSet<Id>();

        this.connections.addAll(Arrays.asList(productIds));
        return this;
    }

    @Override
    public ProductConnectionIndex appendConnection(Id productId) {
        if (productId == null)
            return this;

        if (this.connections == null)
            this.connections = new HashSet<Id>();

        this.connections.add(productId);
        return this;
    }

    @Override
    public ProductConnectionIndex appendConnections(Collection<IdSupport> products) {
        if (products == null || products.isEmpty())
            return this;

        if (this.connections == null)
            this.connections = new HashSet<Id>();

        for (IdSupport product : products) {
            if (product == null || product.getId() == null)
                continue;

            if (!(product instanceof Product))
                throw new IllegalArgumentException("IdSupport object must be of type: " + Product.class.getName());

            this.connections.add(product.getId());
        }

        return this;
    }

    @Override
    public ProductConnectionIndex appendConnection(IdSupport product) {
        if (product == null || product.getId() == null)
            return this;

        if (!(product instanceof Product))
            throw new IllegalArgumentException("IdSupport object must be of type: " + Product.class.getName());

        if (this.connections == null)
            this.connections = new HashSet<Id>();

        this.connections.add(product.getId());

        return this;
    }

    @Override
    public Set<Id> getChildConnections() {
        return childConnections;
    }

    @Override
    public ProductConnectionIndex setChildConnections(Collection<Id> productIds) {
        this.childConnections = new HashSet<Id>(productIds);
        return this;
    }

    @Override
    public ProductConnectionIndex appendChildConnections(Id... productIds) {
        if (productIds == null || productIds.length == 0)
            return this;

        if (this.childConnections == null)
            this.childConnections = new HashSet<Id>();

        this.childConnections.addAll(Arrays.asList(productIds));
        return this;
    }

    @Override
    public ProductConnectionIndex appendChildConnection(Id productId) {
        if (productId == null)
            return this;

        if (this.childConnections == null)
            this.childConnections = new HashSet<Id>();

        this.childConnections.add(productId);
        return this;
    }

    @Override
    public ProductConnectionIndex appendChildConnections(Collection<IdSupport> products) {
        if (products == null || products.isEmpty())
            return this;

        if (this.childConnections == null)
            this.childConnections = new HashSet<Id>();

        for (IdSupport product : products) {
            if (product == null || product.getId() == null)
                continue;

            if (!(product instanceof Product))
                throw new IllegalArgumentException("IdSupport object must be of type: " + Product.class.getName());

            this.childConnections.add(product.getId());
        }

        return this;
    }

    @Override
    public ProductConnectionIndex appendChildConnection(IdSupport product) {
        if (product == null || product.getId() == null)
            return this;

        if (!(product instanceof Product))
            throw new IllegalArgumentException("IdSupport object must be of type: " + Product.class.getName());

        if (this.childConnections == null)
            this.childConnections = new HashSet<Id>();

        this.childConnections.add(product.getId());

        return this;
    }

    @Override
    public Set<Id> getSellableChildConnections() {
        return sellableChildConnections;
    }

    @Override
    public ProductConnectionIndex setSellableChildConnections(Collection<Id> productIds) {
        this.sellableChildConnections = new HashSet<Id>(productIds);
        return this;
    }

    @Override
    public ProductConnectionIndex appendSellableChildConnections(Id... productIds) {
        if (productIds == null || productIds.length == 0)
            return this;

        if (this.sellableChildConnections == null)
            this.sellableChildConnections = new HashSet<Id>();

        this.sellableChildConnections.addAll(Arrays.asList(productIds));
        return this;
    }

    @Override
    public ProductConnectionIndex appendSellableChildConnection(Id productId) {
        if (productId == null)
            return this;

        if (this.sellableChildConnections == null)
            this.sellableChildConnections = new HashSet<Id>();

        this.sellableChildConnections.add(productId);
        return this;
    }

    @Override
    public ProductConnectionIndex appendSellableChildConnections(Collection<IdSupport> products) {
        if (products == null || products.isEmpty())
            return this;

        if (this.sellableChildConnections == null)
            this.sellableChildConnections = new HashSet<Id>();

        for (IdSupport product : products) {
            if (product == null || product.getId() == null)
                continue;

            if (!(product instanceof Product))
                throw new IllegalArgumentException("IdSupport object must be of type: " + Product.class.getName());

            this.sellableChildConnections.add(product.getId());
        }

        return this;
    }

    @Override
    public ProductConnectionIndex appendSellableChildConnection(IdSupport product) {
        if (product == null || product.getId() == null)
            return this;

        if (!(product instanceof Product))
            throw new IllegalArgumentException("IdSupport object must be of type: " + Product.class.getName());

        if (this.sellableChildConnections == null)
            this.sellableChildConnections = new HashSet<Id>();

        this.sellableChildConnections.add(product.getId());

        return this;
    }

    @Override
    public int getUpdateFlag() {
        return updateFlag;
    }

    @Override
    public ProductConnectionIndex setUpdateFlag(int updateFlag) {
        this.updateFlag = updateFlag;
        return this;
    }
}
