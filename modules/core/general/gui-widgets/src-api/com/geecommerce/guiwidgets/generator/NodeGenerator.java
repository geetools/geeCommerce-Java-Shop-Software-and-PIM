package com.geecommerce.guiwidgets.generator;

import com.geecommerce.guiwidgets.model.ContentNode;
import org.rendersnake.HtmlCanvas;

import java.io.IOException;

public interface NodeGenerator {

    public boolean couldGenerateNode(ContentNode node);

    public void generateNode(ContentNode contentNode, HtmlCanvas html) throws IOException;
}
