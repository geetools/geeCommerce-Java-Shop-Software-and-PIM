package com.geecommerce.catalog.product.widget;

import com.google.inject.Inject;
import com.geecommerce.catalog.product.MediaType;
import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.CatalogMediaType;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.CatalogMedia;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by korsar on 13.08.2015.
 */

@Widget(name = "product_energy_efficiency")
public class EnergyEfficiencyWidget extends AbstractWidgetController implements WidgetController {
    private final ProductService productService;
    private final CatalogMedia catalogMedia;
    public Product p = null;

    public String style = null;
    public String energy_val = null;
    public CatalogMediaAsset ee_image = null;

    @Inject
    public EnergyEfficiencyWidget(ProductService productService, CatalogMedia catalogMedia) {
	this.productService = productService;
	this.catalogMedia = catalogMedia;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {

	style = null;
	energy_val = null;
	ee_image = null;
	p = null;
	Id productId = widgetCtx.getParam("product_id", Id.class);

	if (productId != null) {
	    p = productService.getProduct(productId);

	    Map<String, CatalogMediaType> mediaTypeMap = catalogMedia.mediaTypeMap();

	    List<CatalogMediaAsset> productImages = catalogMedia.allImagesFor(p, mediaTypeMap.get(MediaType.PRODUCT_ENERGY_EFFICIENCY));

	    if (productImages.size() > 0) {
		ee_image = productImages.get(productImages.size() - 1);
	    }

	    // p.getImages().get(0).getMediaTypes();
	    // CatalogMediaType cat ;
	    // cat.get;
	    if (p != null) {
		AttributeValue atrr_root = p.getAttribute("energieeffizienz_skala");
		if (atrr_root != null && atrr_root.getFirstAttributeOption() != null) {
		    String skala = atrr_root.getFirstAttributeOption().getLabel().getVal();

		    if (!StringUtils.isBlank(skala)) {

			String new_code;
			switch (skala) {
			case "A+++ bis D":
			    new_code = "energieeffizienzklasse";
			    break;
			case "A++ bis E":
			    new_code = "energieeffizienzklasse_a_e";
			    break;
			case "A bis G":
			    new_code = "energieeffizienzklasse_a_g";
			    break;
			default:
			    new_code = null;
			    break;
			}

			if (new_code != null) {
			    AttributeValue attrVal = p.getAttribute(new_code);
			    if (attrVal != null) {
				AttributeOption atrr = attrVal.getFirstAttributeOption();

				if (atrr != null) {
				    energy_val = atrr.getLabel().getVal();

				    int position = atrr.getPosition();

				    switch (position) {
				    case 1:
					style = "a";
					break;
				    case 2:
					style = "b";
					break;
				    case 3:
					style = "c";
					break;
				    case 4:
					style = "d";
					break;
				    case 5:
					style = "e";
					break;
				    case 6:
					style = "f";
					break;
				    case 7:
					style = "g";
					break;
				    default:
					style = null;
					break;
				    }

				    // widgetCtx.setParam("style", style);
				    // widgetCtx.setParam("energy_val", energy_val);
				}
			    }
			}
		    }
		}

		widgetCtx.setParam("productId", p.getId().toString());
	    }
	}

	widgetCtx.setParam("style", style);
	widgetCtx.setParam("prod", p);
	widgetCtx.setParam("energy_val", energy_val);
	if (ee_image != null) {
	    widgetCtx.setParam("energy_image", ee_image.getPath());
	}

	boolean is_category = widgetCtx.getParam("is_category_page", Boolean.class);

	if (is_category) {
	    widgetCtx.render("product/product_energy_efficiency_category");
	} else {
	    widgetCtx.render("product/product_energy_efficiency");
	}

    }

    public String getStyle() {
	return style;
    }

    public void setStyle(String style) {
	this.style = style;
    }

    public String getEnergy_val() {
	return energy_val;
    }

    public void setEnergy_val(String energy_val) {
	this.energy_val = energy_val;
    }
}
