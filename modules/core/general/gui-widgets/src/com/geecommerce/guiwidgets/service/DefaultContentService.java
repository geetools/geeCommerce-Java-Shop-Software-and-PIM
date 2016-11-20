package com.geecommerce.guiwidgets.service;

import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.rendersnake.HtmlCanvas;
import org.unbescape.html.HtmlEscape;

import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.system.widget.helper.WidgetHelper;
import com.geecommerce.guiwidgets.enums.ContentNodeType;
import com.geecommerce.guiwidgets.enums.ContentType;
import com.geecommerce.guiwidgets.generator.DefaultWidgetNodeGenerator;
import com.geecommerce.guiwidgets.generator.ImageProductWidgetNodeGenerator;
import com.geecommerce.guiwidgets.generator.NodeGenerator;
import com.geecommerce.guiwidgets.model.Content;
import com.geecommerce.guiwidgets.model.ContentNode;
import com.geecommerce.guiwidgets.model.StructureNode;
import com.geecommerce.guiwidgets.repository.Contents;
import com.google.inject.Inject;

@Service
public class DefaultContentService implements ContentService {
    private final Contents contents;
    private final WidgetHelper widgetHelper;

    @Inject
    public DefaultContentService(Contents contents, WidgetHelper widgetHelper) {
        this.contents = contents;
        this.widgetHelper = widgetHelper;
    }

    protected String wrapPageTemplate(Content content, String template) {
        return "<#import \"${t_layout}/" + content.getLayout().getPath() + "\" as layout>\n" + // ${t_layout}
            "\n" + "<@layout.onecolumn><div class=\"home-container\">" + template + "</div></@layout.onecolumn>";
    }

    @Override
    public String generateTemplate(Content content) throws IOException {
        HtmlCanvas html = new HtmlCanvas();

        if (content.getType() == null || content.getType().equals(ContentType.PARTIAL)) {
            for (StructureNode structureNode : content.getStructureNodes()) {
                generateNode(structureNode, html, content);
            }

            return html.toHtml();
        } else {
            // generate page

            for (StructureNode structureNode : content.getStructureNodes()) {
                generateNode(structureNode, html, content);
            }

            String htmlContent = html.toHtml();

            if (content.getLayout() != null) {
                htmlContent = wrapPageTemplate(content, htmlContent);
            }
            return htmlContent;
        }

    }

    @Override
    public String generateNode(ContentNode contentNode) throws IOException {
        HtmlCanvas html = new HtmlCanvas();

        generateNode(contentNode, html);
        return html.toHtml();
    }

    @Override
    public String generateNode(StructureNode structureNode) throws IOException {
        return null;
    }

    @Override
    public List<Content> getContentsByKey(String key) {
        return contents.withKey(key);
    }

    protected void generateNode(StructureNode structureNode, HtmlCanvas html, Content content) throws IOException {
        if (structureNode.getNodes() != null && structureNode.getNodes().size() > 0) {
            html.div(class_(structureNode.getCss()));
            for (StructureNode childNode : structureNode.getNodes()) {
                generateNode(childNode, html, content);
            }
            html._div();
        } else {
            html.div(class_(structureNode.getCss()));
            if (!StringUtils.isBlank(structureNode.getNodeId())) {
                if (content.getContentNodes() != null) {
                    Optional<ContentNode> node = content.getContentNodes().stream()
                        .filter(n -> n.getNodeId().equals(structureNode.getNodeId())).findFirst();
                    if (node.isPresent()) {
                        generateNode(node.get(), html);
                    }
                }
            }
            html._div();
        }
    }

    protected void generateNode(ContentNode contentNode, HtmlCanvas html) throws IOException {

        if (contentNode.getType().equals(ContentNodeType.TEXT)) {
            html/* .div(class_(contentNode.getCss())) */.write(HtmlEscape.unescapeHtml(contentNode.getContent()),
                false)/* ._div() */;
        }

        if (contentNode.getType().equals(ContentNodeType.WIDGET) && contentNode.getWidget() != null
            && !contentNode.getWidget().isEmpty()) {
            generateWidgetNode(contentNode, html);
        }

    }

    private void generateWidgetNode(ContentNode contentNode, HtmlCanvas html) throws IOException {
        for (NodeGenerator nodeGenerator : getWidgetNodeGenerators()) {
            if (nodeGenerator.couldGenerateNode(contentNode)) {
                nodeGenerator.generateNode(contentNode, html);
                return;
            }
        }

    }

    private List<NodeGenerator> getWidgetNodeGenerators() {
        List<NodeGenerator> nodeGenerators = new ArrayList<>();
        nodeGenerators.add(new ImageProductWidgetNodeGenerator());
        nodeGenerators.add(new DefaultWidgetNodeGenerator());

        return nodeGenerators;
    }

}
