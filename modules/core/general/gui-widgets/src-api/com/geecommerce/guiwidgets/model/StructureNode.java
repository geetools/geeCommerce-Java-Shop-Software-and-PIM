package com.geecommerce.guiwidgets.model;

import java.util.List;

import com.geecommerce.core.service.api.Model;

public interface StructureNode extends Model {
    public String getCss();

    public StructureNode setCss(String css);

    public List<StructureNode> getNodes();

    public StructureNode setNodes(List<StructureNode> nodes);

    public String getNodeId();

    public StructureNode setNodeId(String nodeId);

    static final class Col {
        public static final String CSS = "css";
        public static final String NODES = "nodes";
        public static final String NODE_ID = "node_id";

    }

}
