package com.geecommerce.checkout.model;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.price.model.PriceType;

public interface OrderItem extends Model {
    public Id getId();

    public OrderItem setId(Id id);

    public Id getOrderId();

    public OrderItem setOrderId(Id orderId);

    public Id getProductId();

    public Product getProduct();

    public OrderItem setProductId(Id productId);

    public String getName();

    public OrderItem setName(String name);

    public String getArticleNumber();

    public OrderItem setArticleNumber(String articleNumber);

    public Double getPrice();

    public OrderItem setPrice(Double price);

    public Id getPriceTypeId();

    public OrderItem setPriceTypeId(Id priceType);

    public PriceType getPriceType();

    public Double getDiscountAmount();

    public OrderItem setDiscountAmount(Double discountAmount);

    public Double getTaxRate();

    public OrderItem setTaxRate(Double taxRate);

    public int getQuantity();

    public OrderItem setQuantity(int quantity);

    public Double getTotalRowPrice();

    public OrderItem setTotalRowPrice(Double totalRowPrice);

    public OrderItem belongsTo(Order order);

    static final class Column {
        public static final String ID = "_id";
        public static final String ORDER_ID = "order_fk";
        public static final String PRODUCT_ID = "product_id";
        public static final String NAME = "name";
        public static final String ARTICLE_NUMBER = "art_no";
        public static final String PRICE = "price";
        public static final String PRICE_TYPE_ID = "price_type_id";
        public static final String TAX_RATE = "tax_rate";
        public static final String QUANTITY = "qty";
        public static final String TOTAL_ROW_PRICE = "total_row_price";
    }
}
