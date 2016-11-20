package com.geecommerce.guiwidgets.generator;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.geecommerce.guiwidgets.enums.ContentNodeType;
import com.geecommerce.guiwidgets.model.ContentNode;

public class DefaultWidgetNodeGenerator implements NodeGenerator {
    @Override
    public boolean couldGenerateNode(ContentNode node) {
        if (ContentNodeType.WIDGET.equals(node.getType()))
            return true;
        return false;
    }

    @Override
    public void generateNode(ContentNode contentNode, HtmlCanvas html) throws IOException {
        String widget = "<@" + contentNode.getWidget() + " ";

        if (contentNode.getParameterValues() != null) {
            for (String key : contentNode.getParameterValues().keySet()) {
                Object value = contentNode.getParameterValues().get(key);
                if (value != null && !value.toString().isEmpty())
                    widget += key + "=\"" + value.toString() + "\" ";
            }
        }
        widget += "/>";

        html/* .div(class_(contentNode.getCss())) */.write(widget,
            false)/* ._div() */;
    }
}
