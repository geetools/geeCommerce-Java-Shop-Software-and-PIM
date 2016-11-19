package com.geecommerce.navigation.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;
import com.geecommerce.navigation.model.NavigationItem;

public interface NavigationHelper extends Helper {
    public void completeNavigationItems(List<NavigationItem> navigationItems, List<UrlRewrite> urlRewrites);

    public void completeNavigationItems(NavigationItem navigationItem, List<UrlRewrite> urlRewrites);

    public NavigationItem findClosestNavigationItem(Product product);

    public Id[] toIds(NavigationItem navigationItem);

    public Id[] toIds(List<NavigationItem> navigationItems);

    public void cloneNavigationTree(Id id);

    public NavigationItem populateNavigationTree(Map<String, Object> treeMap);

    public NavigationItem createNavigationTree(NavigationItem navigationItem);

    public void removeNavigationTree(Id id);

    public void removeNavigationList(Id rootId);
}
