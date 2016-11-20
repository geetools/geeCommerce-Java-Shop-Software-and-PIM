package com.geecommerce.guiwidgets.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.guiwidgets.model.WebSlideShow;

public interface WebSlideShows extends Repository {
    public List<WebSlideShow> thatBelongTo(String name);
}
