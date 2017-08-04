package com.geecommerce.catalog.product.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.enums.AttributeGroupMappingType;
import com.geecommerce.core.system.attribute.model.AttributeGroup;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.attribute.repository.AttributeGroups;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Widget(name = "product_details")
public class ProductDetailsWidget extends AbstractWidgetController implements WidgetController {
    private final ProductService productService;
    private final AttributeGroups attributeGroups;

    @Inject
    public ProductDetailsWidget(ProductService productService, AttributeGroups attributeGroups) {
        this.productService = productService;
        this.attributeGroups = attributeGroups;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        Id productId = widgetCtx.getParam("product_id", Id.class);

        if (productId != null) {
            Product p = productService.getProduct(productId);
            List<ProductDetail> productDetails = new ArrayList<>();
            List<AttributeGroup> attributeGroupList = attributeGroups.findAll(AttributeGroup.class);

            for (AttributeGroup attributeGroup : attributeGroupList) {
                
                if(attributeGroup.getItems() == null || attributeGroup.getItems().isEmpty())
                    continue;
                
                List<AttributeValue> attributeValues = attributeGroup.getItems().stream()
                    .filter(item -> item.getType().equals(AttributeGroupMappingType.ATTRIBUTE))
                    .map(item -> p.getAttribute(item.getId(), true)).filter(x -> x != null)
                    .collect(Collectors.toList());
                
                if (attributeValues != null && attributeValues.size() > 0) {
                    ProductDetail productDetail = new ProductDetail();
                    productDetail.setAttributeValues(attributeValues);
                    productDetail.setLabel(attributeGroup.getLabel().getStr());
                    productDetail.setCode(attributeGroup.getCode());
                    productDetail.setGroup(true);
                    productDetail.setPosition(attributeGroup.getPosition());
                    productDetails.add(productDetail);
                }
            }

            Collections.sort(productDetails, (p1, p2) -> (int) (p1.getPosition() - p2.getPosition()));
            widgetCtx.setParam("productDetails", productDetails);
            widgetCtx.setParam("wProduct", p);
        }

        widgetCtx.render("product/details");
    }

    public class ProductDetail {
        private int position;
        private String label;
        private String code;
        private boolean group = false;

        private List<AttributeValue> attributeValues;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<AttributeValue> getAttributeValues() {
            return attributeValues;
        }

        public void setAttributeValues(List<AttributeValue> attributeValues) {
            this.attributeValues = attributeValues;
        }

        public void addAttributeValue(AttributeValue attributeValue) {
            if (attributeValues == null) {
                attributeValues = new ArrayList<>();
            }
            attributeValues.add(attributeValue);
        }

        public boolean isGroup() {
            return group;
        }

        public void setGroup(boolean group) {
            this.group = group;
        }
    }
}
