package com.geecommerce.guiwidgets.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.guiwidgets.model.Magazine;

public interface Magazines extends Repository {
    public List<Magazine> enabledMagazines();
}
