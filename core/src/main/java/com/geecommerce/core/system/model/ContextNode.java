package com.geecommerce.core.system.model;

import java.util.ArrayList;
import java.util.List;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.enums.Scope;
import com.geecommerce.core.type.Id;

public class ContextNode {
    private Id id = null;
    private Scope scope = null;
    private ContextNode parent = null;
    private List<ContextNode> children = null;
    private String name = null; // for debugging only

    public ContextNode() {
        this.scope = Scope.GLOBAL;
    }

    public ContextNode(Id id, Scope scope, ContextNode parent, String name) {
        this.id = id;
        this.scope = scope;
        this.parent = parent;
        this.name = name;
    }

    public ContextNode(Id id, Scope scope, ContextNode parent) {
        this.id = id;
        this.scope = scope;
        this.parent = parent;
    }

    public ContextNode addChild(Id id, Scope scope) {
        return addChild(id, scope, null);
    }

    public ContextNode addChild(Id id, Scope scope, String name) {
        if (children == null) {
            children = new ArrayList<>();
        }

        if (children.size() > 0) {
            for (ContextNode child : children) {
                // child already exists
                if (child.getId().equals(id))
                    return child;
            }
        }

        ContextNode child = new ContextNode(id, scope, this, name);

        children.add(child);

        return child;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public boolean isMerchantScope() {
        return this.scope == Scope.MERCHANT;
    }

    public boolean isStoreScope() {
        return this.scope == Scope.STORE;
    }

    public boolean isRequestContextScope() {
        return this.scope == Scope.REQUEST_CONTEXT;
    }

    public ContextNode getParent() {
        return parent;
    }

    public void setParent(ContextNode parent) {
        this.parent = parent;
    }

    public List<ContextNode> getChildren() {
        return children;
    }

    public void setChildren(List<ContextNode> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContextNode findChild(Id id) {
        if (id == null)
            return null;

        if (this.id != null && this.id.equals(id)) {
            return this;
        }

        ContextNode foundChild = null;

        if (children != null && children.size() > 0) {
            for (ContextNode child : children) {
                foundChild = child.findChild(id);

                if (foundChild != null)
                    break;
            }
        }

        return foundChild;
    }

    /**
     * Traverses up the tree trying to find the parent.
     * 
     * @param id
     * @return parent
     */
    public ContextNode findParent(Id id) {
        if (id == null || this.id == null)
            return null;

        if (this.id.equals(id))
            return this;

        if (parent == null)
            return null;

        return parent.findParent(id);
    }

    /**
     * Traverses up the tree trying to find the parent of a particular scope.
     * 
     * @param id
     * @return parent
     */
    public ContextNode findParent(Scope scope) {
        if (scope == null || this.scope == null)
            return null;

        if (this.scope == scope)
            return this;

        if (parent == null)
            return null;

        return parent.findParent(scope);
    }

    @SuppressWarnings("incomplete-switch")
    public String toDebugString() {
        StringBuilder sb = new StringBuilder();

        switch (scope) {
        case GLOBAL:
            sb.append("   +-- ");
            break;
        case MERCHANT:
            sb.append("   |   +-- ");
            break;
        case STORE:
            sb.append("   |   |   +-- ");
            break;
        case REQUEST_CONTEXT:
            sb.append("   |   |   |   +-- ");
            break;
        }

        sb.append(scope);

        if (id != null) {
            sb.append(" ").append(id.getS());
        }

        if (name != null) {
            sb.append(" (").append(name).append(")");
        }

        ApplicationContext appCtx = App.get().getApplicationContext();

        if (appCtx != null) {
            RequestContext reqCtx = appCtx.getRequestContext();

            if (reqCtx != null) {
                switch (scope) {
                case MERCHANT:
                    if (reqCtx.getMerchantId().equals(id))
                        sb.append(" [*]");
                    break;
                case STORE:
                    if (reqCtx.getStoreId().equals(id))
                        sb.append(" [*]");
                    break;
                case REQUEST_CONTEXT:
                    if (reqCtx.getId().equals(id))
                        sb.append(" [*]");
                    break;
                }
            }
        }

        if (children != null && children.size() > 0) {
            for (ContextNode child : children) {
                sb.append("\n");

                sb.append(child.toDebugString());
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "ContextNode [id=" + id + ", scope=" + scope + ", name=" + name + "]";
    }
}
