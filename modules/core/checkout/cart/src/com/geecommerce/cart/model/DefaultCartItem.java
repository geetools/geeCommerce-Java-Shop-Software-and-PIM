package com.geecommerce.cart.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.geecommerce.calculation.model.CalculationItem;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.AttributeCodes;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.Price;
import com.geecommerce.price.model.PriceType;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.shipping.converter.ShippingItemConverter;
import com.geecommerce.shipping.model.ShippingItem;
import com.geecommerce.tax.TaxClassType;
import com.geecommerce.tax.model.TaxClass;
import com.geecommerce.tax.model.TaxRate;
import com.geecommerce.tax.service.TaxService;
import com.google.inject.Inject;

@Model
public class DefaultCartItem extends AbstractModel implements CartItem, CalculationItem, ShippingItemConverter {
    private static final long serialVersionUID = -4548951491176002083L;
    private Id productId = null;
    private int quantity = 0;

    private Product product;
    private String productName = null;
    private Double productTaxRate = null;

    private Double packageWeight = null;
    private Double packageWidth = null;
    private Double packageHeight = null;
    private Double packageDepth = null;

    private Id bundleId = null;
    private Product bundle;

    private Boolean last = null;

    // Product repository
    private transient final Products products;
    // Tax service
    private transient final TaxService taxService;

    @Inject
    public DefaultCartItem(Products products, TaxService taxService) {
        this.products = products;
        this.taxService = taxService;
    }

    @Override
    public Id getId() {
        return getProductId();
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public CartItem setProductId(Id productId) {
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
    public CartItem setProduct(Product product) {
        this.product = product;
        this.productId = product.getId();

        return this;
    }

    @Override
    public Id getBundleId() {
        return this.bundleId;
    }

    @Override
    public CartItem setBundleId(Id bundleId) {
        this.bundleId = bundleId;
        this.bundle = null;
        return this;
    }

    @Override
    public Product getBundle() {
        if (this.bundle == null && getBundleId() != null) {
            this.bundle = products.findById(Product.class, getBundleId());
        }

        return this.bundle;
    }

    @Override
    public CartItem setBundle(Product bundle) {
        this.bundle = bundle;
        this.bundleId = bundle.getId();
        return this;
    }

    @Override
    public String getProductName() {
        if (this.productName == null) {
            Product p = getProduct();

            AttributeValue nameAttr = p.getAttribute(AttributeCodes.NAME);
            AttributeValue name2Attr = p.getAttribute(AttributeCodes.NAME2);

            Product parent = p.isVariant() ? p.getParent() : null;

            String name = null;
            String name2 = null;

            if (nameAttr == null || Str.isEmpty(nameAttr.getStr())) {
                if (parent != null)
                    nameAttr = parent.getAttribute(AttributeCodes.NAME);

                if (nameAttr == null || Str.isEmpty(nameAttr.getStr())) {
                    nameAttr = p.getAttribute(AttributeCodes.ARTICLE_NUMBER);

                    if (nameAttr == null || Str.isEmpty(nameAttr.getStr()) && parent != null) {
                        nameAttr = parent.getAttribute(AttributeCodes.ARTICLE_NUMBER);
                    }
                }
            }

            if (nameAttr == null || Str.isEmpty(nameAttr.getStr())) {
                name = p.getId().str();
            } else {
                name = nameAttr.getStr().trim();
            }

            if (name2Attr == null || Str.isEmpty(name2Attr.getStr())) {
                if (parent != null)
                    name2Attr = parent.getAttribute(AttributeCodes.NAME2);
            }

            if (name2Attr != null && !Str.isEmpty(name2Attr.getStr())) {
                name2 = name2Attr.getStr().trim();
            }

            StringBuilder nameBuilder = new StringBuilder(name);

            if (name2 != null)
                nameBuilder.insert(0, name2 + Str.SPACE);

            this.productName = nameBuilder.toString();
        }

        return this.productName;
    }

    @Override
    public String getProductURI() {
        return app.helper(TargetSupportHelper.class).findURI(getProduct());
    }

    @Override
    public Price productPrice() {
        PriceResult pr = getProduct().getPrice();
        return pr == null ? null : pr.getFinalPriceFor(quantity);
    }

    @Override
    public Double getProductPrice() {
        PriceResult pr = getProduct().getPrice();
        return pr == null ? null : pr.getFinalPrice(quantity);
    }

    @Override
    public PriceType getProductPriceType() {
        return productPrice().getPriceType();
    }

    @Override
    public Double getProductTaxRate() {
        String taxClassCode = null;

        AttributeValue attr = getProduct().getAttribute("tax_class");

        if (attr != null) {
            taxClassCode = attr.getString();
        }

        // if tax class has not been set in the product, see if there is a
        // default value
        if (taxClassCode == null) {
            taxClassCode = app.cpStr_("tax/default/product_tax_class");
        }

        if (taxClassCode == null) {
            throw new RuntimeException("No tax-class configured for product: " + getProduct().getId());
        }

        TaxClass productTaxClass = taxService.findTaxClassFor(taxClassCode, TaxClassType.PRODUCT);
        TaxRate taxRate = taxService.findDefaultTaxRateFor(productTaxClass);

        return taxRate == null ? null : taxRate.getRate();
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public CartItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public CartItem incrementQty() {
        this.quantity++;
        return this;
    }

    private Double getDoubleAttribute(Double value, String name) {
        if (value == null) {
            Product p = getProduct();
            AttributeValue v = p.getAttribute(name, true);

            if (v != null)
                value = v.getDouble();
        }
        return value;
    }

    @Override
    public Double getPackageWeight() {
        return getDoubleAttribute(packageWeight, "pkg_weight");
    }

    @Override
    public Double getPackageWidth() {
        return getDoubleAttribute(packageWidth, "pkg_width");
    }

    @Override
    public Double getPackageHeight() {
        return getDoubleAttribute(packageHeight, "pkg_height");
    }

    @Override
    public Double getPackageDepth() {
        return getDoubleAttribute(packageDepth, "pkg_depth");
    }

    @Override
    public Boolean isLast() {
        return last;
    }

    @Override
    public CartItem setLast(Boolean last) {
        this.last = last;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.productId = id_(map.get(Column.PRODUCT_ID));
        this.quantity = int_(map.get(Column.QUANTITY));
        this.last = bool_(map.get(Column.LAST), false);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Column.PRODUCT_ID, getProductId());
        m.put(Column.PRODUCT_NAME, getProductName());
        m.put(Column.PRODUCT_PRICE, getProductPrice());
        m.put(Column.PRODUCT_PRICE_TYPE_ID, getProductPriceType().getId());
        m.put(Column.QUANTITY, getQuantity());
        m.put(Column.LAST, isLast());

        return m;
    }

    @Override
    public Map<String, Object> toCalculationItem() {
        Map<String, Object> m = new HashMap<>();
        m.put(CalculationItem.FIELD.ITEM_ARTICLE_ID, getProductId());
        m.put(CalculationItem.FIELD.ITEM_QUANTITY, getQuantity());
        PriceResult pr = getProduct().getPrice();
        if (pr != null) {
            Price price = pr.getFinalPriceFor(quantity);
            m.put(CalculationItem.FIELD.ITEM_BASE_CALCULATION_PRICE, price.getFinalPrice());
            m.put(CalculationItem.FIELD.ITEM_BASE_CALCULATION_PRICE_TYPE, price.getPriceType().getCode());
        }

        m.put(CalculationItem.FIELD.ITEM_BASE_CALCULATION_PRICE, getProductPrice());
        m.put(CalculationItem.FIELD.ITEM_TAX_RATE, getProductTaxRate());

        return m;
    }

    @Override
    public ShippingItem toShippingItem() {
        ShippingItem shippingItem = app.model(ShippingItem.class);

        shippingItem.setProductName(getProductName());
        shippingItem.setProductId(getProductId());
        shippingItem.setQuantity(getQuantity());
        shippingItem.setDepth(getPackageDepth());
        shippingItem.setHeight(getPackageHeight());
        shippingItem.setWidth(getPackageWidth());
        shippingItem.setPrice(getProductPrice());
        shippingItem.setWeight(getPackageWeight());
        shippingItem.setPickupStoreId(getPickupStoreId());

        return shippingItem;
    }

    @Override
    public Boolean isActive() {
        return true;
    }

    @Override
    public Boolean getActive() {
        return true;
    }

    @Override
    public CartItem setActive(Boolean isActive) {
        return this;
    }

    public Boolean getDeliveryAvailable() {
        return true;
    }

    public CartItem setDeliveryAvailable(Boolean deliveryAvailable) {
        return this;
    }

    public Boolean getPickupAvailable() {
        return true;
    }

    public CartItem setPickupAvailable(Boolean pickupAvailable) {
        return this;
    }

    @Override
    public String getDeliveryMethod() {
        return "";
    }

    @Override
    public CartItem setDeliveryMethod(String deliveryMethod) {
        return this;
    }

    @Override
    public Boolean isPickup() {
        return false;
    }

    @Override
    public Boolean isPickupAvailable(String pickupStoreId) {
        return false;
    }

    @Override
    public Boolean isDeliveryAvailable(String zip) {
        return false;
    }

    @Override
    public String getPickupDeliveryTime() {
        return null;
    }

    public Boolean getPickup() {
        return false;
    }

    @Override
    public CartItem setPickup(Boolean isPickup) {
        return this;
    }

    @Override
    public String getPickupStoreId() {
        return null;
    }

    @Override
    public CartItem setPickupStoreId(String storeId) {
        return this;
    }
}
