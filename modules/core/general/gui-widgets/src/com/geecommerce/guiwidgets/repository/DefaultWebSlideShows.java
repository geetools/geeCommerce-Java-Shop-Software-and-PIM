package com.geecommerce.guiwidgets.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.guiwidgets.model.WebSlideShow;
import com.geecommerce.guiwidgets.model.WebSlideShow.Col;

@Repository
public class DefaultWebSlideShows extends AbstractRepository implements WebSlideShows {
    @Override
    public List<WebSlideShow> thatBelongTo(String name) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Col.NAME, name);
        return find(WebSlideShow.class, filter);
    }

}
