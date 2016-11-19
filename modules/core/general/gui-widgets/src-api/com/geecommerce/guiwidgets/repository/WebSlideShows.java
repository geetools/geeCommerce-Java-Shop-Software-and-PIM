package com.geecommerce.guiwidgets.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.guiwidgets.model.WebSlideShow;

import java.util.List;

public interface WebSlideShows extends Repository {
    public List<WebSlideShow> thatBelongTo(String name);
}
