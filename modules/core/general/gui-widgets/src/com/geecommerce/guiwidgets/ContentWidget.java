package com.geecommerce.guiwidgets;

import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.guiwidgets.model.Content;
import com.geecommerce.guiwidgets.repository.Contents;
import com.geecommerce.guiwidgets.service.ContentService;
import com.google.inject.Inject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Widget(name = "content")
public class ContentWidget extends AbstractWidgetController implements WidgetController {
    private final Contents contents;
    private final ContentService contentService;
    private final String PARAM_KEY = "key";
    private final String PARAM_ID = "id";

    @Inject
    public ContentWidget(Contents contents, ContentService contentService) {
        this.contents = contents;
        this.contentService = contentService;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {

        String contentId = widgetCtx.getParam(PARAM_ID);
        if (contentId != null && !contentId.isEmpty()) {
            Id id = Id.parseId(contentId);
            Content content = contents.findById(Content.class, id);
            if (content != null) {
                widgetCtx.renderContent(content.getTemplate());
            }
            return;
        }

        String contentKey = widgetCtx.getParam(PARAM_KEY);
        if (contentKey != null && !contentKey.isEmpty()) {
            List<Content> contentList = contentService.getContentsByKey(contentKey);

            if (contentList != null && !contentList.isEmpty()) {
                // Optional<Content> optional = contentList.stream().
                // filter(x -> (x.getDateFrom() == null || x.getDateFrom().before(new Date())) && (x.getDateTo() == null || x.getDateTo().after(new
                // Date()))).findFirst();

                Content content = null;

                // if (optional.isPresent())
                // slideShow = optional.get();

                content = contentList.get(0);

                if (content != null) {
                    widgetCtx.renderContent(content.getTemplate());
                }
            }
        }

    }

}