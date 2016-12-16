package com.geecommerce.catalog.product.batch.dataimport.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.App;
import com.geecommerce.core.Bool;
import com.geecommerce.core.Str;
import com.geecommerce.core.batch.dataimport.helper.ImportHelper;
import com.geecommerce.core.batch.exception.ImportException;
import com.geecommerce.core.enums.ProductType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObject;
import com.google.inject.Inject;

@Helper
public class DefaultProductBeanHelper implements ProductBeanHelper {

    @Inject
    protected App app;

    protected final ImportHelper importHelper;

    protected static final String FORCE_UPDATE_PREFIX = "!:";
    protected Pattern isNumberRegex = Pattern.compile("^[0-1]+$");

    @Inject
    public DefaultProductBeanHelper(ImportHelper importHelper) {
        this.importHelper = importHelper;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setSaleable(Product product, Map<String, String> data) {
        String _saleable = data.get("_saleable");

        Boolean saleable = Bool.toBoolean(_saleable);
        if (saleable == null)
            saleable = false;

        ContextObject prdSaleable = product.getSaleable();

        if (prdSaleable == null) {
            product.setSaleable(importHelper.toContextObject(saleable, data));
        } else {
            importHelper.updateContextObject(prdSaleable, saleable, data);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setVisibility(Product product, Map<String, String> data) {
        String _visible = data.get("_visible");
        String _visibleFrom = data.get("_visible_from");
        String _visibleTo = data.get("_visible_to");
        String _visibleInProductList = data.get("_visible_in_product_list");

        Boolean visible = Bool.toBoolean(_visible);
        if (visible == null)
            visible = false;

        ContextObject prdVisible = product.getVisible();

        if (prdVisible == null) {
            product.setVisible(importHelper.toContextObject(visible, data));
        } else {
            importHelper.updateContextObject(prdVisible, visible, data);
        }

        if (!Str.isEmpty(_visibleFrom) || !Str.isEmpty(_visible)) {
            ContextObject prdVisibleFrom = product.getVisibleFrom();

            Instant visibleFrom = Instant.parse(_visibleFrom);
            LocalDateTime ldt = LocalDateTime.ofInstant(visibleFrom, ZoneId.systemDefault());
            Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

            if (prdVisibleFrom == null && !Str.isEmpty(_visibleFrom)) {
                product.setVisibleFrom(importHelper.toContextObject(date, data));
            } else if (prdVisibleFrom != null && !Str.isEmpty(_visible)) {
                if (!Str.isEmpty(_visibleFrom)) {
                    importHelper.updateContextObject(prdVisibleFrom, date, data);
                } else {
                    importHelper.removeFromContextObject(prdVisibleFrom, data);
                }
            }
        }

        if (!Str.isEmpty(_visibleTo)) {
            ContextObject prdVisibleTo = product.getVisibleTo();

            Instant visibleTo = Instant.parse(_visibleTo);
            LocalDateTime ldt = LocalDateTime.ofInstant(visibleTo, ZoneId.systemDefault());
            Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

            if (prdVisibleTo == null) {
                product.setVisibleTo(importHelper.toContextObject(date, data));
            } else if (!Str.isEmpty(_visible)) {
                importHelper.updateContextObject(prdVisibleTo, date, data);
            }
        }

        ContextObject prdVisibleInProductList = product.getVisible();

        Boolean visibleInProductList = Bool.toBoolean(_visibleInProductList);
        if (visibleInProductList == null)
            visibleInProductList = false;

        if (prdVisibleInProductList == null) {
            product.setVisibleInProductList(importHelper.toContextObject(visibleInProductList, data));
        } else if (!Str.isEmpty(_visibleInProductList)) {
            importHelper.updateContextObject(prdVisibleInProductList, visibleInProductList, data);
        }
    }

    @Override
    public void setTypeAndGroup(Product product, Map<String, String> data) {
        ProductType prdType = product.getType();
        String _type = data.get("_type");
        String _productGroup = data.get("_product_group");
        String _programmeGroup = data.get("_programme");
        String _bundleGroup = data.get("_bundle_group");

        if (prdType == null && Str.isEmpty(_type))
            throw new ImportException("missingProductType", data.get("_type"));

        if (!Str.isEmpty(_type)) {
            _type = _type.trim().toUpperCase();

            ProductType[] productTypes = ProductType.values();

            boolean isNumber = false;
            Matcher m = isNumberRegex.matcher(_type);

            if (m.matches()) {
                isNumber = true;
            }

            ProductType type = null;

            if (isNumber) {
                type = ProductType.fromId(Integer.parseInt(_type));
            } else {
                type = ProductType.valueOf(_type);
            }

            if (type == null)
                throw new ImportException("invalidProductType", data.get("_type"));

            if (prdType != null && prdType != type)
                throw new ImportException("productTypeChangeNotSupported", data.get("_type"));

            if (prdType == null) {
                prdType = type;
                product.setType(type);
            }
        }

        if (prdType != null) {
            switch (prdType) {
            case PRODUCT:
                AttributeValue prdProductGroup = product.getAttribute("product_group");

                break;
            case VARIANT_MASTER:

                break;
            case PROGRAMME:

                break;
            case BUNDLE:

                break;
            }
        }

    }

    @Override
    public void setProductKeys(Product product, Map<String, String> data) {
        String _id2 = data.get("_id2");
        String _articleNumber = data.get("_article_number");
        String _ean = data.get("_ean");

        if (!Str.isEmpty(_id2)) {
            _id2 = _id2.trim();

            String prdId2 = product.getId2();

            if (prdId2 == null || _id2.startsWith(FORCE_UPDATE_PREFIX)) {
                product.setId2(_id2.replace(FORCE_UPDATE_PREFIX, Str.EMPTY));
            }
        }

        if (!Str.isEmpty(_ean)) {
            _ean = _ean.trim();

            Long prdEan = product.getEan();

            if (prdEan == null || _ean.startsWith(FORCE_UPDATE_PREFIX)) {
                _ean = _id2.replace(FORCE_UPDATE_PREFIX, Str.EMPTY);

                Matcher m = isNumberRegex.matcher(_ean);

                if (m.matches()) {
                    product.setEan(Long.valueOf(_ean));
                } else {
                    throw new ImportException("invalidEan", _ean);
                }
            }
        }

        if (!Str.isEmpty(_articleNumber)) {
            _articleNumber = _articleNumber.trim();

            String prdArticleNumber = product.getArticleNumber();

            if (prdArticleNumber == null || _articleNumber.startsWith(FORCE_UPDATE_PREFIX)) {
                product.setAttribute("article_number", _articleNumber.replace(FORCE_UPDATE_PREFIX, Str.EMPTY));
            }
        }
    }

}
