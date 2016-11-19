package com.geecommerce.guiwidgets;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;

@Widget(name = "cms_image_link", cms = true, css = true)
public class CmsImageLinkWidget extends AbstractWidgetController implements WidgetController {
    private final MediaAssetService mediaAssetService;
    private final String PARAM_IMAGE = "image";
    private final String PARAM_LINK = "link";

    @Inject
    public CmsImageLinkWidget(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String imageId = widgetCtx.getParam(PARAM_IMAGE);
        String link = widgetCtx.getParam(PARAM_LINK);
        if (link == null)
            link = "#";

        if (imageId != null && !imageId.isEmpty() && link != null && !link.isEmpty()) {
            MediaAsset image = mediaAssetService.get(Id.parseId(imageId));

            if (image != null) {
                widgetCtx.setParam("ilw_image", image.getUrl());
                widgetCtx.setParam("ilw_link", link);
            }
        }

        widgetCtx.render();
    }

}