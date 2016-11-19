package com.geecommerce.guiwidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.system.widget.model.WidgetParameter;
import com.geecommerce.core.system.widget.model.WidgetParameterOption;
import com.geecommerce.core.system.widget.repository.WidgetParameters;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.guiwidgets.model.Slide;
import com.geecommerce.guiwidgets.model.WebSlideShow;
import com.geecommerce.guiwidgets.repository.WebSlideShows;
import com.geecommerce.guiwidgets.service.WebSlideShowService;
import com.google.inject.Inject;

@Widget(name = "web_slideshow", js = true, css = true, cms = true)
public class WebSlideShowWidget extends AbstractWidgetController implements WidgetController {
    private final WebSlideShowService webSlideShowService;
    private final WebSlideShows webSlideShows;
    private final WidgetParameters widgetParameters;
    private final String PARAM_NAME = "name";

    @Inject
    public WebSlideShowWidget(WebSlideShowService webSlideShowService, WidgetParameters widgetParameters, WebSlideShows webSlideShows) {
        this.webSlideShowService = webSlideShowService;
        this.widgetParameters = widgetParameters;
        this.webSlideShows = webSlideShows;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        String webSlideShowName = widgetCtx.getParam(PARAM_NAME);
        widgetCtx.setParam("slideshowId", null);
        if (webSlideShowName != null && !webSlideShowName.isEmpty()) {
            List<WebSlideShow> slideShows = webSlideShowService.getWebSlideShowByName(webSlideShowName);

            if (slideShows != null && !slideShows.isEmpty()) {
                Optional<WebSlideShow> optional = slideShows.stream().filter(x -> (x.getDateFrom() == null || x.getDateFrom().before(new Date())) && (x.getDateTo() == null || x.getDateTo().after(new Date()))).findFirst();

                WebSlideShow slideShow = null;

                if (optional.isPresent())
                    slideShow = optional.get();

                if (slideShow != null) {
                    // if (webSlideShowID != null){
                    // widgetCtx.setParam("slideshowId", slideShow.getName()+"-" +webSlideShowID);
                    // } else {
                    // widgetCtx.setParam("slideshowId", slideShow.getName());
                    // }
                    widgetCtx.setParam("slideshowId", slideShow.getName());
                    List<Slide> slides = slideShow.getSlides();
                    if (slides != null && !slides.isEmpty()) {
                        slides = slides.stream().filter(slide -> (slide.getShowFrom() == null || slide.getShowFrom().before(new Date())) && (slide.getShowTo() == null || slide.getShowTo().after(new Date()))).collect(Collectors.toList());
                        ;
                        if (slides != null && !slides.isEmpty()) {
                            Collections.sort(slides, new SlideViewSortByPosition());
                            widgetCtx.setParam("slides", slides);
                        }
                    }
                }
            }
        }

/*
    if (promotionTemplate.equals("web_slideshow")) {
	    int slideshowInterval = app.cpInt_("home/web_slideshow/interval", 8000);

	    if (slideshowInterval < 1000) {
		slideshowInterval = 8000;
	    }

	    widgetCtx.setParam("slideshowInterval", slideshowInterval);
	}
*/

        widgetCtx.render();
    }

    private class SlideViewSortByPosition implements Comparator<Slide> {
        @Override
        public int compare(Slide s1, Slide s2) {
            return s1.getPosition() - s2.getPosition();
        }
    }

    @Override
    public List<WidgetParameterOption> getParameterOptions(Id parameterId) {

        List<WidgetParameterOption> parameterOptions = super.getParameterOptions(parameterId);

        if (parameterOptions == null || parameterOptions.isEmpty()) {
            WidgetParameter parameter = widgetParameters.findById(WidgetParameter.class, parameterId);
            if (parameter.getCode().equals("name")) {
                List<?> webSlideShowList = webSlideShows.distinct(WebSlideShow.class, WebSlideShow.Col.NAME);
                parameterOptions = new ArrayList<>();
                for (Object name : webSlideShowList) {
                    WidgetParameterOption option = app.getModel(WidgetParameterOption.class);
                    option.setValue(name.toString());
                    option.setLabel(new ContextObject<String>(name.toString()));
                    option.belongsTo(parameter);
                    parameterOptions.add(option);
                }
            }
        }
        return parameterOptions;
    }
}