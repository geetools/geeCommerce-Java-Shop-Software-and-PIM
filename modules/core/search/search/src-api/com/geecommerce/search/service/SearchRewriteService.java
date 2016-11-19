package com.geecommerce.search.service;

import com.geecommerce.core.service.api.Service;

/**
 * Created by Andrey on 05.10.2015.
 */
public interface SearchRewriteService extends Service {
    String findUrl(String keyword);
}
