package com.geecommerce.guiwidgets.model;

import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.enums.ContentNodeType;
import com.google.common.collect.Maps;

@Model
public class DefaultContentNode extends AbstractModel implements ContentNode {

    @Column(Col.TYPE)
    private ContentNodeType type = null;
    /*
     * @Column(Col.TYPE2) private String type2 = null;
     */

    @Column(Col.CONTENT)
    private String content = null;

    @Column(Col.KEY)
    private String key = null;

    @Column(Col.WIDGET)
    private String widget = null;

    @Column(Col.NODE_ID)
    private String nodeId = null;

    @Column(Col.PARAMETER_VALUES)
    private Map<String, Object> parameterValues = null;

    /*
     * @Column(Col.CSS) private String css = null;
     */

    @Column(Col.PREVIEW)
    private String preview = null;

    /* private List<ContentNode> nodes = null; */

    @Override
    public ContentNodeType getType() {
        return type;
    }

    @Override
    public ContentNode setType(ContentNodeType type) {
        this.type = type;
        return this;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ContentNode setKey(String key) {
        this.key = key;
        return this;
    }

    /*
     * @Override public String getType2() { return type2; }
     * 
     * @Override public ContentNode setType2(String type2) { this.type2 = type2;
     * return this; }
     */

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public ContentNode setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public Map<String, Object> getParameterValues() {
        return parameterValues;
    }

    @Override
    public ContentNode setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
        return this;
    }

    /*
     * @Override public String getCss() { return css; }
     * 
     * @Override public ContentNode setCss(String css) { this.css = css; return
     * this; }
     * 
     * @Override public List<ContentNode> getNodes() { return nodes; }
     * 
     * @Override public ContentNode setNodes(List<ContentNode> nodes) {
     * this.nodes = nodes; return this; }
     */

    @Override
    public String getPreview() {
        return preview;
    }

    @Override
    public ContentNode setPreview(String preview) {
        this.preview = preview;
        return this;
    }

    @Override
    public String getWidget() {
        return widget;
    }

    @Override
    public ContentNode setWidget(String widget) {
        this.widget = widget;
        return this;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public ContentNode setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    @Override
    public Id getId() {
        return null;
    }

    /*
     * private void fixParams(Map<String, Object> map) { if
     * (map.containsKey("widget")) { if (this.parameters == null)
     * this.parameters = new HashMap<>();
     * 
     * Map<String, Object> widget = map_(map.get("widget")); List<Map<String,
     * Object>> parameters = list_(widget.get("parameters")); for (Map<String,
     * Object> parameter : parameters) {
     * this.parameters.put(str_(parameter.get("code")),
     * str_(parameter.get("value"))); } } }
     */

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        type = enum_(ContentNodeType.class, map.get(Col.TYPE));
        content = str_(map.get(Col.CONTENT));
        key = str_(map.get(Col.KEY));
        widget = str_(map.get(Col.WIDGET));
        preview = str_(map.get(Col.PREVIEW));
        parameterValues = map_(map.get(Col.PARAMETER_VALUES));
        nodeId = str_(map.get(Col.NODE_ID));
        /*
         * css = str_(map.get(Col.CSS));
         * 
         * 
         * type2 = str_(map.get(Col.TYPE2));
         */
        /*        */

        /*
         * fixParams(map);
         * 
         * this.nodes = new ArrayList<>(); List<Map<String, Object>> nodesList =
         * list_(map.get(Col.NODES)); if (nodesList != null) { this.nodes = new
         * ArrayList<>(); for (Map<String, Object> node : nodesList) {
         * ContentNode n = app.getModel(ContentNode.class); n.fromMap(node);
         * this.nodes.add(n); }
         * 
         * }
         */

    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        if (getType() != null)
            map.put(Col.TYPE, getType().toId());
        map.put(Col.CONTENT, getContent());
        map.put(Col.KEY, getKey());
        map.put(Col.WIDGET, getWidget());
        /* map.put(Col.CSS, getCss()); */
        map.put(Col.PREVIEW, getPreview());
        map.put(Col.PARAMETER_VALUES, getParameterValues());
        map.put(Col.NODE_ID, getNodeId());

        /*
         * map.put(Col.TYPE2, getType2()); List<Map<String, Object>> nodesList =
         * new ArrayList<>(); if (getNodes() != null) { for (ContentNode node :
         * getNodes()) { nodesList.add(node.toMap()); } map.put(Col.NODES,
         * nodesList); }
         */

        return map;
    }
}
