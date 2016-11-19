package com.geecommerce.guiwidgets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Maps;

@Model
public class DefaultStructureNode extends AbstractModel implements StructureNode {

    @Column(Col.NODE_ID)
    private String nodeId = null;

    @Column(Col.CSS)
    private String css = null;

    private List<StructureNode> nodes = null;

    @Override
    public String getCss() {
        return css;
    }

    @Override
    public StructureNode setCss(String css) {
        this.css = css;
        return this;
    }

    @Override
    public List<StructureNode> getNodes() {
        return nodes;
    }

    @Override
    public StructureNode setNodes(List<StructureNode> nodes) {
        this.nodes = nodes;
        return this;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public StructureNode setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    @Override
    public Id getId() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null)
            return;

        super.fromMap(map);

        nodeId = str_(map.get(Col.NODE_ID));
        css = str_(map.get(Col.CSS));

        this.nodes = new ArrayList<>();
        List<Map<String, Object>> nodesList = list_(map.get(Col.NODES));
        if (nodesList != null) {
            this.nodes = new ArrayList<>();
            for (Map<String, Object> node : nodesList) {
                StructureNode n = app.getModel(StructureNode.class);
                n.fromMap(node);
                this.nodes.add(n);
            }

        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newLinkedHashMap(super.toMap());

        map.put(Col.CSS, getCss());
        map.put(Col.NODE_ID, getNodeId());

        List<Map<String, Object>> nodesList = new ArrayList<>();
        if (getNodes() != null) {
            for (StructureNode node : getNodes()) {
                nodesList.add(node.toMap());
            }
            map.put(Col.NODES, nodesList);
        }

        return map;
    }
}
