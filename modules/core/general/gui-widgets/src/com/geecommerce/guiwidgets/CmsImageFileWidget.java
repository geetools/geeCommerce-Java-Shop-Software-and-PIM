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

@Widget(name = "cms_image_file", cms = true)
public class CmsImageFileWidget extends AbstractWidgetController implements WidgetController {
    private final MediaAssetService mediaAssetService;
    private final String PARAM_IMAGE = "image";
    private final String PARAM_FILE = "file";

    @Inject
    public CmsImageFileWidget(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String imageId = widgetCtx.getParam(PARAM_IMAGE);
        String fileId = widgetCtx.getParam(PARAM_FILE);
        if (imageId != null && !imageId.isEmpty() && fileId != null && !fileId.isEmpty()) {
            MediaAsset image = mediaAssetService.get(Id.parseId(imageId));
            MediaAsset file = mediaAssetService.get(Id.parseId(fileId));

            if (image != null && file != null) {
                widgetCtx.setParam("ifw_image", image.getUrl());
                widgetCtx.setParam("ifw_file", file.getUrl());
            }
        }

        widgetCtx.render();
    }

}