package com.geecommerce.guiwidgets.service;

import com.google.inject.Inject;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.guiwidgets.model.WebSlideShow;
import com.geecommerce.guiwidgets.repository.WebSlideShows;


import java.util.List;

@Service
public class DefaultWebSlideShowService implements WebSlideShowService {
    private final WebSlideShows webSlideShows;

    @Inject
    public DefaultWebSlideShowService(WebSlideShows webSlideShows) {
	this.webSlideShows = webSlideShows;
    }

    
    @Override
    public WebSlideShow createWebSlideShow(WebSlideShow webSlideShow) {
	return webSlideShows.add(webSlideShow);
    }

    
    @Override
    public List<WebSlideShow> getWebSlideShowByName(String name) {
	return webSlideShows.thatBelongTo(name);
    }

    
    @Override
    public void update(WebSlideShow webSlideShow) {
	webSlideShows.update(webSlideShow);
    }
}
