package com.geecommerce.guiwidgets.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.guiwidgets.model.Content;

public interface Contents extends Repository {
    public List<Content> withKey(String key);
}
