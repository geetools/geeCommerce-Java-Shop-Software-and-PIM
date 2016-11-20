package com.geecommerce.guiwidgets.generator;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.geecommerce.guiwidgets.model.ContentNode;

public interface NodeGenerator {

    public boolean couldGenerateNode(ContentNode node);

    public void generateNode(ContentNode contentNode, HtmlCanvas html) throws IOException;
}
