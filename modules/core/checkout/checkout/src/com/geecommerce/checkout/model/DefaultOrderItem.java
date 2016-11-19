package com.geecommerce.checkout.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.geecommerce.calculation.model.CalculationItem;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.price.repository.PriceTypes;
import com.geecommerce.shipping.converter.ShippingItemConverter;
import com.geecommerce.shipping.model.ShippingItem;
import com.google.inject.Inject;

@Model("sale_order_item")
@XmlRootElement(name = "item")
public class DefaultOrderItem extends AbstractModel implements OrderItem, CalculationItem, ShippingItemConverter {
    private static final long serialVersionUID = -7056105659732618839L;
    private Id id = null;
    private Id orderId = null;
    private Product product;
    private Id productId = null;
    private String name = null;
    private String articleNumber = null;
    private Double price = null;
    private Id priceTypeId = null;
    private Double discountAmount = null;
    private Double taxRate = null;
    private int quantity = 0;
    private Double totalRowPrice = null;

    // Repositories
    private final Products products;
    private final PriceTypes priceTypes;

    // Lazy-loaded price type.
    private PriceType priceType = null;

    @Inject
    public DefaultOrderItem(Products products, PriceTypes priceTypes) {
        this.products = products;
        this.priceTypes = priceTypes;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public OrderItem setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getOrderId() {
        return orderId;
    }

    @Override
    public OrderItem setOrderId(Id orderId) {
        this.orderId = orderId;
        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public OrderItem setProductId(Id productId) {
        this.productId = productId;
        return this;
    }

    @Override
    public Product getProduct() {
        if (this.product == null) {
            this.product = products.findById(Product.class, getProductId());
        }

        return this.product;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OrderItem setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getArticleNumber() {
        return articleNumber;
    }

    @Override
    public OrderItem setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
        return this;
    }

    @Override
    public Double getPrice() {
        return price;
    }

    @Override
    public OrderItem setPrice(Double price) {
        this.price = price;
        return this;
    }

    @Override
    public Id getPriceTypeId() {
        return priceTypeId;
    }

    @Override
    public OrderItem setPriceTypeId(Id priceTypeId) {
        this.priceTypeId = priceTypeId;
        return this;
    }

    @Override
    public PriceType getPriceType() {
        if (priceTypeId != null && priceType == null) {
            priceType = priceTypes.findById(PriceType.class, priceTypeId);
        }

        return priceType;
    }

    @Override
    public Double getDiscountAmount() {
        return discountAmount;
    }

    @Override
    public OrderItem setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    @Override
    public OrderItem setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
        return this;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public OrderItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public Double getTotalRowPrice() {
        return totalRowPrice;
    }

    @Override
    public OrderItem setTotalRowPrice(Double totalRowPrice) {
        this.totalRowPrice = totalRowPrice;
        return this;
    }

    @Override
    public OrderItem belongsTo(Order order) {
        if (order != null) {
            this.orderId = order.getId();
        }

        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);

        this.id = id_(map.get(Column.ID));
        this.orderId = id_(map.get(Column.ORDER_ID));
        this.productId = id_(map.get(Column.PRODUCT_ID));
        this.name = str_(map.get(Column.NAME));
        this.articleNumber = str_(map.get(Column.ARTICLE_NUMBER));
        this.price = double_(map.get(Column.PRICE));
        this.priceTypeId = id_(map.get(Column.PRICE_TYPE_ID));
        this.quantity = int_(map.get(Column.QUANTITY));
        this.totalRowPrice = double_(map.get(Column.TOTAL_ROW_PRICE));
        this.taxRate = double_(map.get(Column.TAX_RATE));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>(super.toMap());

        m.put(Column.ID, getId());
        m.put(Column.ORDER_ID, getOrderId());
        m.put(Column.PRODUCT_ID, getProductId());
        m.put(Column.NAME, getName());
        m.put(Column.ARTICLE_NUMBER, getArticleNumber());
        m.put(Column.PRICE, getPrice());
        m.put(Column.PRICE_TYPE_ID, getPriceTypeId());
        m.put(Column.QUANTITY, getQuantity());
        m.put(Column.TOTAL_ROW_PRICE, getTotalRowPrice());
        m.put(Column.TAX_RATE, getTaxRate());

        return m;
    }

    @Override
    public Map<String, Object> toCalculationItem() {
        Map<String, Object> m = new HashMap<>();
        m.put(CalculationItem.FIELD.ITEM_ARTICLE_ID, getProductId());
        m.put(CalculationItem.FIELD.ITEM_QUANTITY, getQuantity());
        m.put(CalculationItem.FIELD.ITEM_BASE_CALCULATION_PRICE, getPrice());
        m.put(CalculationItem.FIELD.ITEM_TAX_RATE, getTaxRate());

        PriceResult pr = getProduct().getPrice();
        if (pr != null) {
            Price price = pr.getFinalPriceFor(quantity);
            m.put(CalculationItem.FIELD.ITEM_BASE_CALCULATION_PRICE, price.getFinalPrice());
            m.put(CalculationItem.FIELD.ITEM_BASE_CALCULATION_PRICE_TYPE, price.getPriceType().getCode());
        }

        return m;
    }

    @Override
    public ShippingItem toShippingItem() {
        ShippingItem shippingItem = app.getModel(ShippingItem.class);

        shippingItem.setQuantity(getQuantity());
        shippingItem.setDepth(null/* TODO */);
        shippingItem.setHeight(null/* TODO */);
        shippingItem.setWidth(null/* TODO */);
        shippingItem.setPrice(getPrice());
        shippingItem.setWeight(null/* TODO */);

        return shippingItem;
    }

    @Override
    public String toString() {
        return "DefaultOrderItem [id=" + id + ", orderId=" + orderId + ", product=" + product + ", productId=" + productId + ", name=" + name + ", articleNumber=" + articleNumber + ", price=" + price
            + ", priceTypeId=" + priceTypeId
            + ", discountAmount=" + discountAmount + ", taxRate=" + taxRate + ", quantity=" + quantity + ", totalRowPrice=" + totalRowPrice + ", products=" + products + ", priceTypes=" + priceTypes
            + ", priceType=" + priceType + "]";
    }
}
