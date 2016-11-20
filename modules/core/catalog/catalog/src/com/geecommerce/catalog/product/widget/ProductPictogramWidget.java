package com.geecommerce.catalog.product.widget;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;

/**
 * Created by korsar on 14.08.2015.
 */

@Widget(name = "product_pictogram")
public class ProductPictogramWidget extends AbstractWidgetController implements WidgetController {

    private final ProductService productService;
    private final MediaAssetService mediaAssetService;

    @Inject
    public ProductPictogramWidget(ProductService productService, MediaAssetService mediaAssetService) {
        this.productService = productService;
        this.mediaAssetService = mediaAssetService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {

        Id productId = widgetCtx.getParam("product_id", Id.class);

        if (productId != null) {
            Product p = productService.getProduct(productId);

            if (p.getAttribute("pictogram_image") != null) {
                Id pictogram_image_id = Id.valueOf(p.getAttribute("pictogram_image").getVal());

                MediaAsset pictogram_asset = mediaAssetService.get(pictogram_image_id);

                if (pictogram_asset != null) {
                    widgetCtx.setParam("pictogram_url", pictogram_asset.getUrl());
                }
            }
        }

        widgetCtx.render("product/product_pictogram");
    }
}