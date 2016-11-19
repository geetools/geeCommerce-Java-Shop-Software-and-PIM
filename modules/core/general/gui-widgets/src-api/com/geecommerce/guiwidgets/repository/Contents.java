package com.geecommerce.guiwidgets.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.guiwidgets.model.Content;

import java.util.List;

public interface Contents extends Repository {
    public List<Content> withKey(String key);
}
