package com.geecommerce.guiwidgets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;

@Widget(name = "cms_image_product", cms = true)
public class CmsImageProductWidget extends AbstractWidgetController implements WidgetController {
    private final MediaAssetService mediaAssetService;
    private final String PARAM_IMAGE = "image";
    private final String PARAM_PRODUCT = "product_id";
    private final ProductService productService;
    private final Products products;

    @Inject
    public CmsImageProductWidget(MediaAssetService mediaAssetService, ProductService productService,
        Products products) {
        this.mediaAssetService = mediaAssetService;
        this.productService = productService;
        this.products = products;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        String imageId = widgetCtx.getParam(PARAM_IMAGE);
        String productId = widgetCtx.getParam(PARAM_PRODUCT);
        if (!StringUtils.isBlank(productId)) {
            Id id = Id.parseId(productId);
            Product product = products.findById(Product.class, id);
            widgetCtx.setParam("ipw_product", product);
        }
        if (imageId != null && !imageId.isEmpty()) {
            MediaAsset image = mediaAssetService.get(Id.parseId(imageId));
            widgetCtx.setParam("ipw_image", image.getUrl());
        }
        widgetCtx.render();
    }

}