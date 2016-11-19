package com.geecommerce.core.system.model;

import com.geecommerce.core.type.Id;

public class ContextTree {
    ContextNode rootNode = null;

    public ContextTree(ContextNode rootNode) {
        this.rootNode = rootNode;
    }

    public ContextNode getRootNode() {
        return rootNode;
    }

    public ContextNode findContextNode(Id id) {
        if (rootNode == null)
            return null;

        return rootNode.findChild(id);
    }

    public void dumpAll() {
        System.out.println(rootNode.toDebugString());
    }
}
