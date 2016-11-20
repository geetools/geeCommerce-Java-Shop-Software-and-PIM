package com.geecommerce.guiwidgets.service;

import java.io.IOException;
import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.guiwidgets.model.Content;
import com.geecommerce.guiwidgets.model.ContentNode;
import com.geecommerce.guiwidgets.model.StructureNode;

public interface ContentService extends Service {
    public String generateTemplate(Content content) throws IOException;

    public String generateNode(ContentNode contentNode) throws IOException;

    public String generateNode(StructureNode structureNode) throws IOException;

    public List<Content> getContentsByKey(String key);
}
