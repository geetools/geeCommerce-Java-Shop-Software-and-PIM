package com.geecommerce.guiwidgets.generator;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.guiwidgets.model.ContentNode;

public class ImageProductWidgetNodeGenerator implements NodeGenerator {

    private final Products products = App.get().getRepository(Products.class);

    /*
     * @Inject public ImageProductWidgetNodeGenerator(Products products) {
     * this.products = products; }
     */

    @Override
    public boolean couldGenerateNode(ContentNode node) {
        /*
         * if (ContentNodeType.WIDGET.equals(node.getType()) &&
         * "cp_image_product".equals(node.getContent()))
         * return true;
         */
        return false;
    }

    @Override
    public void generateNode(ContentNode contentNode, HtmlCanvas html) throws IOException {
        /*
         * String widget = "<@" + contentNode.getContent() + " ";
         * 
         * if (contentNode.getParameters() != null) {
         * for (String key : contentNode.getParameters().keySet()) {
         * String value = contentNode.getParameters().get(key);
         * if (key.equals("product")) {
         * Product product = products.havingArticleNumber(value);
         * if (product != null) {
         * value = product.getId().str();
         * key = "product_id";
         * }
         * }
         * if (value != null && !value.isEmpty())
         * widget += key + "=\"" + value + "\" ";
         * }
         * }
         * widget += "/>";
         * 
         * html.div(class_(contentNode.getCss())).write(widget, false)._div();
         */
    }
}
