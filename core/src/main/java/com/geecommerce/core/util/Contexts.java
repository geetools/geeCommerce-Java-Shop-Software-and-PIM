package com.geecommerce.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.system.model.ContextNode;
import com.geecommerce.core.system.model.ContextTree;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.service.SystemService;
import com.geecommerce.core.type.Id;

public class Contexts {
    public static final List<Id> getAllowedMerchants(List<Id> allowedScopes) {
        if (allowedScopes == null || allowedScopes.size() == 0)
            return new ArrayList<>();

        SystemService systemService = App.get().getSystemService(SystemService.class);
        List<RequestContext> requestContexts = systemService.getRequestContextsForScopes(allowedScopes);
        ContextTree ctxTree = systemService.getContextTree(requestContexts);

        // ctxTree.dumpAll();

        Set<Id> merchantIds = new HashSet<>();

        for (Id scopeId : allowedScopes) {
            ContextNode ctxNode = ctxTree.findContextNode(scopeId);

            if (ctxNode.isMerchantScope()) {
                merchantIds.add(scopeId);
            }
        }

        return new ArrayList<>(merchantIds);
    }

    public static final List<Id> getAllowedStores(List<Id> allowedScopes) {
        if (allowedScopes == null || allowedScopes.size() == 0)
            return new ArrayList<>();

        SystemService systemService = App.get().getSystemService(SystemService.class);
        List<RequestContext> requestContexts = systemService.getRequestContextsForScopes(allowedScopes);
        ContextTree ctxTree = systemService.getContextTree(requestContexts);

        // ctxTree.dumpAll();

        Set<Id> storeIds = new HashSet<>();

        for (Id scopeId : allowedScopes) {
            ContextNode ctxNode = ctxTree.findContextNode(scopeId);

            if (ctxNode.isMerchantScope()) {
                // Add all children of merchant
                List<ContextNode> storeNodes = ctxNode.getChildren();

                for (ContextNode storeNode : storeNodes) {
                    storeIds.add(storeNode.getId());
                }
            } else if (ctxNode.isStoreScope()) {
                storeIds.add(ctxNode.getId());
            }
        }

        return new ArrayList<>(storeIds);
    }

    public static final List<Id> getAllowedRequestContexts(List<Id> allowedScopes) {
        if (allowedScopes == null || allowedScopes.size() == 0)
            return new ArrayList<>();

        SystemService systemService = App.get().getSystemService(SystemService.class);
        List<RequestContext> requestContexts = systemService.getRequestContextsForScopes(allowedScopes);
        ContextTree ctxTree = systemService.getContextTree(requestContexts);

        // ctxTree.dumpAll();

        Set<Id> requestContextIds = new HashSet<>();

        for (Id scopeId : allowedScopes) {
            ContextNode ctxNode = ctxTree.findContextNode(scopeId);

            if (ctxNode.isMerchantScope()) {
                List<ContextNode> storeNodes = ctxNode.getChildren();

                // Add all children from stores
                for (ContextNode storeNode : storeNodes) {
                    for (ContextNode reqCtxNode : storeNode.getChildren()) {
                        requestContextIds.add(reqCtxNode.getId());
                    }
                }
            } else if (ctxNode.isStoreScope()) {
                // Add all children from store
                List<ContextNode> requestContextNodes = ctxNode.getChildren();

                for (ContextNode reqCtxNode : requestContextNodes) {
                    requestContextIds.add(reqCtxNode.getId());
                }
            } else if (ctxNode.isRequestContextScope()) {
                requestContextIds.add(ctxNode.getId());
            }
        }

        return new ArrayList<>(requestContextIds);
    }
}
