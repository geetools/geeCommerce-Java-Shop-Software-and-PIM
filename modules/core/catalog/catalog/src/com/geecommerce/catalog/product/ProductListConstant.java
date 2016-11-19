package com.geecommerce.catalog.product;

public class ProductListConstant {
    // Current product list filter rule used to process the request.
    public static final String FILTER_RULE = "plist.filter.rule";

    // Filter attributes stored in the MongoDB product_lists collection.
    public static final String STATIC_FILTER_PARTS = "plist.filter.parts";

    // Filter attributes passed in the URI.
    public static final String URI_FILTER_PARTS = "uri.filter.parts";

    // Filter attributes sent in the request query string.
    public static final String PARAM_FILTER_PARTS = "param.filter.parts";

    // Filter attributes definitions.
    public static final String FILTER_ATTRIBUTES = "plist.filter.attributes";

    // Filter attribute aliases.
    public static final String FILTER_ATTRIBUTE_ALIAS_INDEX = "plist.filter.attribute.alias.index";
}
