package com.geecommerce.guiwidgets.helper;

import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.guiwidgets.model.Content;

public interface ContentUrlHelper extends Helper {
    public void generateUniqueUri(Content content, UrlRewrite urlRewrite, boolean empty);
}
