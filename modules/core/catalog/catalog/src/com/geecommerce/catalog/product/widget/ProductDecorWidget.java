package com.geecommerce.catalog.product.widget;

import com.google.inject.Inject;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Widget(name = "product_decor")
public class ProductDecorWidget extends AbstractWidgetController implements WidgetController {

    private final ProductService productService;

    @Inject
    public ProductDecorWidget(ProductService productService) {
	this.productService = productService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
	Id productId = widgetCtx.getParam("product_id", Id.class);

	if (productId != null) {
	    Product product = productService.getProduct(productId);

	    if (product.isVariantMaster()) {

		List<String> decorAttributes = Arrays.asList(new String[] { "dekor_kombination", "jalousien_farbe", "rollo_farbe", "teppichfarbe", "wohndecken_farbe", "suchfarbe" });

		List<AttributeValue> attributeValueList = getVariantAttributes(product);
		List<String> notDecorAttributes = notDecorAttributes(decorAttributes, attributeValueList);
		decorAttributes = decorAttributes(decorAttributes, attributeValueList);

		List<Product> variants = product.getVariants();

		List<Product> validVariants = variants.stream().filter(v -> v.isValidForSelling()).collect(Collectors.toList());

		boolean showColorIcon = false;
		boolean showCogIcon = false;
		for (String code : decorAttributes) {
		    if (isDifferentValues(validVariants, code)) {
			showColorIcon = true;
			break;
		    }
		}
		for (String code : notDecorAttributes) {
		    if (isDifferentValues(validVariants, code)) {
			showCogIcon = true;
			break;
		    }
		}

		widgetCtx.setParam("showColorIcon", showColorIcon);
		widgetCtx.setParam("showCogIcon", showCogIcon);

	    }
	}

	widgetCtx.render("product/decor_icon");
    }

    private boolean isDifferentValues(List<Product> products, String attributeCode) {

	Set<Id> valueSet = new HashSet<>();

	for (Product product : products) {
	    AttributeValue value = product.getAttribute(attributeCode);
	    if (value != null && value.getOptionId() != null) {
		valueSet.add(value.getOptionId());
	    }
	}

	if (valueSet.size() > 1)
	    return true;

	return false;
    }

    private List<AttributeValue> getVariantAttributes(Product product) {
	/*
	 * if(product.getVariantAttributes() != null && product.getVariantAttributes().size() > 0) return product.getVariantAttributes();
	 */
	if (product.getVariants() != null && product.getVariants().size() > 0)
	    return product.getVariants().get(0).getVariantAttributes();
	return new ArrayList<>();
    }

    private List<String> notDecorAttributes(List<String> decorCodes, List<AttributeValue> attributeValueList) {
	List<String> notDecorCodes = new ArrayList<>();

	for (AttributeValue attributeValue : attributeValueList) {
	    if (!decorCodes.contains(attributeValue.getCode()))
		notDecorCodes.add(attributeValue.getCode());
	}

	return notDecorCodes;
    }

    private List<String> decorAttributes(List<String> decorCodes, List<AttributeValue> attributeValueList) {
	List<String> actualDecorCodes = new ArrayList<>();

	for (AttributeValue attributeValue : attributeValueList) {
	    if (decorCodes.contains(attributeValue.getCode()))
		actualDecorCodes.add(attributeValue.getCode());
	}

	return actualDecorCodes;
    }
}
