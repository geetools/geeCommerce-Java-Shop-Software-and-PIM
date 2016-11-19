package com.geecommerce.price.pojo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Pojo;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;
import com.google.inject.Inject;

@Pojo
public class DefaultPricingContext implements PricingContext {
    private static final long serialVersionUID = -6929234392477986532L;

    @Inject
    protected App app;

    protected Id customerId = null;
    protected List<Id> customerGroupIds = null;
    protected String currency = null;
    protected Set<String> includePriceTypes = new HashSet<String>();
    protected Set<String> excludePriceTypes = new HashSet<String>();

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public PricingContext setCustomerId(Id customerId) {
        this.customerId = customerId;
        return this;
    }

    @Override
    public List<Id> getCustomerGroupIds() {
        return customerGroupIds;
    }

    @Override
    public PricingContext setCustomerGroupIds(List<Id> customerGroupIds) {
        this.customerGroupIds = customerGroupIds;
        return this;
    }

    @Override
    public String getCurrency() {
        // User cannot select currency yet, so we just return the default.
        return currency == null ? app.getBaseCurrency() : currency;
    }

    @Override
    public PricingContext setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public Set<String> getPriceTypesToInclude() {
        return includePriceTypes;
    }

    @Override
    public PricingContext setPriceTypesToInclude(List<PriceType> priceTypesToInclude) {
        if (priceTypesToInclude == null || priceTypesToInclude.size() == 0)
            return this;

        for (PriceType priceType : priceTypesToInclude) {
            includePriceTypes.add(priceType.getCode());
        }

        return this;
    }

    @Override
    public PricingContext addPriceTypeToInclude(PriceType priceType) {
        this.includePriceTypes.add(priceType.getCode());
        return this;
    }

    @Override
    public PricingContext removePriceTypeToInclude(PriceType priceType) {
        this.includePriceTypes.remove(priceType.getCode());
        return this;
    }

    @Override
    public Set<String> getPriceTypesToExclude() {
        return excludePriceTypes;
    }

    @Override
    public PricingContext setPriceTypesToExclude(List<PriceType> priceTypesToExclude) {
        if (priceTypesToExclude == null || priceTypesToExclude.size() == 0)
            return this;

        for (PriceType priceType : priceTypesToExclude) {
            excludePriceTypes.add(priceType.getCode());
        }

        return this;
    }

    @Override
    public PricingContext addPriceTypeToExclude(PriceType priceType) {
        this.excludePriceTypes.add(priceType.getCode());
        return this;
    }

    @Override
    public PricingContext removePriceTypeToExclude(PriceType priceType) {
        this.excludePriceTypes.remove(priceType.getCode());
        return this;
    }

    @Override
    public boolean isPriceAvailable(Price price) {
        if (price == null)
            return false;

        if (getCurrency() == null || price.getCurrency() == null || !getCurrency().equals(price.getCurrency()))
            return false;

        if (includePriceTypes.size() > 0) {
            for (String priceTypeCode : includePriceTypes) {
                if (price.getPriceType().getCode().equals(priceTypeCode)) {
                    return true;
                }
            }

            return false;
        }

        if (excludePriceTypes.size() > 0) {
            for (String priceTypeCode : excludePriceTypes) {
                if (price.getPriceType().getCode().equals(priceTypeCode)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customerGroupIds == null) ? 0 : customerGroupIds.hashCode());
        result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
        result = prime * result + ((getCurrency() == null) ? 0 : getCurrency().hashCode());
        result = prime * result + ((excludePriceTypes == null) ? 0 : excludePriceTypes.hashCode());
        result = prime * result + ((includePriceTypes == null) ? 0 : includePriceTypes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        DefaultPricingContext other = (DefaultPricingContext) obj;

        if (customerGroupIds == null) {
            if (other.customerGroupIds != null)
                return false;
        } else if (!customerGroupIds.equals(other.customerGroupIds))
            return false;

        if (customerId == null) {
            if (other.customerId != null)
                return false;
        } else if (!customerId.equals(other.customerId))
            return false;

        if (getCurrency() == null) {
            if (other.getCurrency() != null)
                return false;
        } else if (!getCurrency().equals(other.getCurrency()))
            return false;

        if (excludePriceTypes == null) {
            if (other.excludePriceTypes != null)
                return false;
        } else if (!excludePriceTypes.equals(other.excludePriceTypes))
            return false;

        if (includePriceTypes == null) {
            if (other.includePriceTypes != null)
                return false;
        } else if (!includePriceTypes.equals(other.includePriceTypes))
            return false;

        return true;
    }

    // @Override
    // public String toString()
    // {
    // return "DefaultPricingContext [customerId=" + customerId + ",
    // customerGroupIds=" + customerGroupIds + ", currency=" + getCurrency() +
    // ", includePriceTypes=" + includePriceTypes + ", excludePriceTypes=" +
    // excludePriceTypes + "]";
    // }
}
