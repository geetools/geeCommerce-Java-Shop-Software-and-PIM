package com.geecommerce.guiwidgets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Widget(name = "cms_product_attribute", cms = true)
public class CmsProductAttributeWidget extends AbstractWidgetController implements WidgetController {

    private final String PARAM_ATTRIBUTE_CODE = "attr_code";
    private final String PARAM_PRODUCT = "product_id";
    private final ProductService productService;
    private final WidgetParameters widgetParameters;
    private final AttributeService attributeService;
    private final Attributes attributes;

    @Inject
    public CmsProductAttributeWidget(ProductService productService, WidgetParameters widgetParameters,
        AttributeService attributeService, Attributes attributes) {
        this.productService = productService;
        this.widgetParameters = widgetParameters;
        this.attributes = attributes;
        this.attributeService = attributeService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String attrCode = widgetCtx.getParam(PARAM_ATTRIBUTE_CODE);
        String productId = widgetCtx.getParam(PARAM_PRODUCT);
        if (!StringUtils.isBlank(productId)) {
            Id id = Id.parseId(productId);
            Product product = productService.getProduct(id);
            widgetCtx.setParam("wProduct", product);
        }

        if (StringUtils.isNotBlank(attrCode)) {
            widgetCtx.setParam("wAttributeCode", attrCode);
        }

        widgetCtx.render();
    }

    @Override
    public List<WidgetParameterOption> getParameterOptions(Id parameterId) {

        List<WidgetParameterOption> parameterOptions = super.getParameterOptions(parameterId);

        if (parameterOptions == null || parameterOptions.isEmpty()) {
            WidgetParameter parameter = widgetParameters.findById(WidgetParameter.class, parameterId);

            List<Attribute> productAttributes = attributeService.getAttributesFor(Product.class);

            if (productAttributes != null && !productAttributes.isEmpty()) {
                parameterOptions = new ArrayList<>();
                for (Attribute attribute : productAttributes) {
                    WidgetParameterOption option = app.model(WidgetParameterOption.class);
                    option.setValue(attribute.getCode());
                    option.setLabel(new ContextObject<>(attribute.getCode()));
                    option.belongsTo(parameter);
                    parameterOptions.add(option);
                }
            }
        }
        return parameterOptions;
    }
}
