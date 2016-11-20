package com.geecommerce.price.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Cacheable;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.price.repository.PriceTypes;
import com.google.inject.Inject;

@Cacheable
@XmlRootElement(name = "price")
@XmlAccessorType(XmlAccessType.FIELD)
@Model("price")
public class DefaultPrice extends AbstractModel implements Price {
    private static final long serialVersionUID = -6530960098221657042L;

    @Column(Col.ID)
    private Id id = null;

    @Column(Col.ID2)
    private String id2 = null;

    @Column(Col.PRODUCT_ID)
    private Id productId = null;

    @Column(Col.COUNTRY)
    private String country = null;

    @Column(Col.CURRENCY)
    private String currency = null;

    @Column(Col.STORE_ID)
    private Id storeId = null;

    @Column(Col.CUSTOMER_ID)
    private Id customerId = null;

    @Column(Col.CUSTOMER_GROUP_ID)
    private Id customerGroupId = null;

    @Column(Col.QTY_FROM)
    private Integer qtyFrom = null;

    @Column(Col.TYPE_ID)
    private Id typeId = null;

    @Column(Col.TYPE_OBJECT_ID)
    private Id typeObjId = null;

    @Column(Col.PRICE)
    private Double price = null;

    @Column(Col.VALID_FROM)
    private Date validFrom = null;

    @Column(Col.VALID_TO)
    private Date validTo = null;

    // Repository.
    private final transient PriceTypes priceTypes;

    // Lazy-loaded price type.
    private PriceType priceType = null;

    public DefaultPrice() {
        this(i(PriceTypes.class));
    }

    @Inject
    public DefaultPrice(PriceTypes priceTypes) {
        this.priceTypes = priceTypes;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public Price setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public String getId2() {
        return id2;
    }

    @Override
    public Price setId2(String id2) {
        this.id2 = id2;
        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public Price setProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public Id getStoreId() {
        return storeId;
    }

    @Override
    public Price setStoreId(Id storeId) {
        this.storeId = storeId;
        return this;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public Price setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public Price setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public Id getCustomerId() {
        return customerId;
    }

    @Override
    public Price setCustomerId(Id customerId) {
        this.customerId = customerId;
        return this;
    }

    @Override
    public Id getCustomerGroupId() {
        return customerGroupId;
    }

    @Override
    public Price setCustomerGroupId(Id customerGroupId) {
        this.customerGroupId = customerGroupId;
        return this;
    }

    @Override
    public Integer getQtyFrom() {
        return qtyFrom;
    }

    @Override
    public Price setQtyFrom(Integer qtyFrom) {
        this.qtyFrom = qtyFrom;
        return this;
    }

    @Override
    public Id getTypeId() {
        return typeId;
    }

    @Override
    public Price setTypeId(Id typeId) {
        this.typeId = typeId;
        return this;
    }

    @Override
    public PriceType getPriceType() {
        if (typeId != null && priceType == null) {
            priceType = priceTypes.findById(PriceType.class, typeId);
        }

        return priceType;
    }

    @Override
    public Id getTypeObjId() {
        return typeObjId;
    }

    @Override
    public Price setTypeObjId(Id typeObjId) {
        this.typeObjId = typeObjId;
        return this;
    }

    @Override
    public Double getPrice() {
        return price;
    }

    @Override
    public Price setPrice(Double price) {
        this.price = price;
        return this;
    }

    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    @Override
    public Price setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    @Override
    public Date getValidTo() {
        return validTo;
    }

    @Override
    public Price setValidTo(Date validTo) {
        this.validTo = validTo;
        return this;
    }

    @Override
    public Double getFinalPrice() {
        return getValidPrice();
    }

    public Double getValidPrice() {
        Double validPrice = null;

        long currentTime = DateTimes.newDate().getTime();

        if (price != null) {
            if (validFrom == null && validTo == null) {
                validPrice = price;
            } else {
                boolean isPriceValid = true;

                if (validFrom != null) {
                    long timeFrom = validFrom.getTime();

                    if (currentTime < timeFrom)
                        isPriceValid = false;
                }

                if (validTo != null) {
                    long timeTo = validTo.getTime();

                    if (currentTime > timeTo)
                        isPriceValid = false;
                }

                if (isPriceValid) {
                    validPrice = price;
                }
            }
        }

        return validPrice;
    }

    @Override
    public String toString() {
        return "DefaultPrice [id=" + id + ", productId=" + productId + ", country=" + country + ", currency=" + currency
            + ", storeId=" + storeId + ", qtyFrom=" + qtyFrom + ", typeId=" + typeId + ", price=" + price
            + ", validFrom=" + validFrom + ", validTo=" + validTo + "]";
    }
}
