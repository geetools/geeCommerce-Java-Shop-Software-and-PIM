package com.geecommerce.wishlist.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.attribute.AttributeCodes;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.tax.TaxClassType;
import com.geecommerce.tax.model.TaxClass;
import com.geecommerce.tax.model.TaxRate;
import com.geecommerce.tax.service.TaxService;
import com.google.inject.Inject;

@Model
public class DefaultWishListItem extends AbstractModel implements WishListItem {
    private static final long serialVersionUID = -6579993034909931065L;
    private Id id = null;
    private Id productId = null;
    private Date createdOn = null;
    private Date modifiedOn = null;

    private Product product;
    private String productName = null;
    private Double productTaxRate = null;

    private String idea = null;
    private WishListItemType type = null;

    // ProductWidget repository
    private final Products products;
    // Tax service
    private final TaxService taxService;

    @Inject
    public DefaultWishListItem(Products products, TaxService taxService) {
        this.products = products;
        this.taxService = taxService;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public WishListItem setId(Id id) {
        this.id = id;
        return this;
    }

    @Override
    public Id getProductId() {
        return productId;
    }

    @Override
    public Product getProduct() {
        if (getProductId() == null)
            return null;
        if (this.product == null) {
            this.product = products.findById(Product.class, getProductId());
        }

        return this.product;
    }

    @Override
    public WishListItem setProduct(Product product) {
        this.product = product;
        this.productId = product.getId();

        return this;
    }

    @Override
    public String getProductName() {
        if (this.productName == null) {
            Product p = getProduct();

            AttributeValue nameAttr = p.getAttribute(AttributeCodes.NAME);

            Product parent = p.isVariant() ? p.getParent() : null;

            String name = null;

            if (nameAttr == null || "".equals(nameAttr.getStr().trim())) {
                if (parent != null)
                    nameAttr = parent.getAttribute(AttributeCodes.NAME);

                if (nameAttr == null || "".equals(nameAttr.getStr().trim())) {
                    nameAttr = p.getAttribute(AttributeCodes.ARTICLE_NUMBER);

                    if (nameAttr == null || "".equals(nameAttr.getStr().trim()) && parent != null) {
                        nameAttr = parent.getAttribute(AttributeCodes.ARTICLE_NUMBER);
                    }
                }
            }

            if (nameAttr == null || "".equals(nameAttr.getStr().trim())) {
                name = p.getId().str();
            } else {
                name = nameAttr.getStr().trim();
            }

            StringBuilder nameBuilder = new StringBuilder(name);

            if (p.isVariant()) {
                List<AttributeValue> variantAttributes = p.getVariantAttributes();

                StringBuilder variantsText = new StringBuilder();

                for (AttributeValue variantAttribute : variantAttributes) {
                    String attributeLabel = variantAttribute.getAttribute().getFrontendLabel().getString();

                    Map<Id, AttributeOption> optionsMap = variantAttribute.getAttributeOptions();

                    if (optionsMap != null && optionsMap.size() > 0) {
                        AttributeOption option = optionsMap.get(variantAttribute.getOptionId());

                        if (option != null) {
                            variantsText.append(", ").append(attributeLabel).append(": ").append(option.getLabel().getString());
                        }
                    }
                }

                // Change product name to include the variant information
                nameBuilder.append(variantsText);
            }

            this.productName = nameBuilder.toString();
        }

        return this.productName;
    }

    @Override
    public String getProductURI() {
        if (getProduct() == null)
            return null;

        return app.getHelper(TargetSupportHelper.class).findURI(getProduct());
    }

    @Override
    public Integer getProductQuantity() {
        if (getProduct() == null)
            return null;

        return getProduct().getQty();
    }

    @Override
    public Boolean getAvailable() {
        if (getProduct() == null)
            return false;

        Double price = getProductPrice();
        Integer quantity = getProductQuantity();

        if (price == null || quantity == null)
            return false;

        if (price > 0 && quantity > 0)
            return true;

        return false;
    }

    @Override
    public Double getProductPrice() {
        if (getProduct() == null)
            return null;

        PriceResult pr = getProduct().getPrice();

        if (pr != null)
            return pr.getFinalPrice();

        return null;
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
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public WishListItem setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @Override
    public WishListItem setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
        return this;
    }

    @Override
    public String getIdea() {
        return idea;
    }

    @Override
    public WishListItem setIdea(String idea) {
        this.idea = idea;
        return this;
    }

    @Override
    public WishListItemType getType() {
        return type;
    }

    @Override
    public WishListItem setType(WishListItemType type) {
        this.type = type;
        return this;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

        this.id = id_(map.get(Column.ID));
        this.productId = id_(map.get(Column.PRODUCT_ID));
        this.idea = str_(map.get(Column.IDEA));
        this.type = WishListItemType.valueOf(str_(map.get(Column.WISH_ITEM_TYPE)));
        this.createdOn = date_(map.get(GlobalColumn.CREATED_ON));
        this.modifiedOn = date_(map.get(GlobalColumn.MODIFIED_ON));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();

        m.put(Column.ID, getId());
        m.put(Column.PRODUCT_ID, getProductId());
        m.put(Column.PRODUCT_NAME, getProductName());
        m.put(Column.PRODUCT_PRICE, getProductPrice());

        m.put(Column.IDEA, getIdea());
        m.put(Column.WISH_ITEM_TYPE, getType().toString());

        m.put(Column.CREATED_ON, getCreatedOn());
        m.put(Column.MODIFIED_ON, getModifiedOn());
        return m;
    }

}
