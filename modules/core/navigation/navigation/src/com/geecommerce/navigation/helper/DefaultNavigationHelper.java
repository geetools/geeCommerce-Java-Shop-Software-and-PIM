package com.geecommerce.navigation.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.ProductNavigationIndexes;
import com.geecommerce.core.App;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;
import com.geecommerce.navigation.model.NavigationItem;
import com.geecommerce.navigation.repository.NavigationItems;
import com.geecommerce.navigation.service.NavigationService;
import com.google.inject.Inject;

@Helper
public class DefaultNavigationHelper implements NavigationHelper {
    @Inject
    protected App app;

    protected final NavigationService navigationService;
    protected final ProductLists productLists;
    protected final ProductNavigationIndexes productNavigationIndexes;
    protected final NavigationItems navigationItems;

    @Inject
    public DefaultNavigationHelper(NavigationService navigationService, ProductLists productLists,
        ProductNavigationIndexes productNavigationIndexes, NavigationItems navigationItems) {
        this.navigationService = navigationService;
        this.productLists = productLists;
        this.productNavigationIndexes = productNavigationIndexes;
        this.navigationItems = navigationItems;
    }

    public NavigationItem populateNavigationTree(Map<String, Object> treeMap) {
        return populateNavigationNode(treeMap);
    }

    private NavigationItem populateNavigationNode(Map<String, Object> nodeMap) {
        NavigationItem navigationItem = app.model(NavigationItem.class);
        navigationItem.fromMap(nodeMap);

        if (nodeMap.containsKey("children")) {
            try {
                ArrayList<Map<String, Object>> children = (ArrayList<Map<String, Object>>) nodeMap.get("children");

                List<NavigationItem> childrenNodes = new ArrayList<>();
                for (Map<String, Object> child : children) {
                    childrenNodes.add(populateNavigationNode(child));
                }

                navigationItem.setChildren(childrenNodes);
            } catch (Exception ex) {
                System.out.println("Can't populate children from node map");
                ex.printStackTrace();
            }
        }
        return navigationItem;
    }

    public NavigationItem createNavigationTree(NavigationItem navigationItem) {
        return createNavigationNode(navigationItem, null, null);
    }

    ;

    private NavigationItem createNavigationNode(NavigationItem navigationItem, Id rootId, Id parentId) {

        if (navigationItem.getId() == null) {
            navigationItem.setId(app.nextId());
        }

        if (rootId == null) {
            // we need to create root
            rootId = navigationItem.getId();
            navigationItem.setParentId(null);
            navigationItem.setRootId(rootId);
        } else {
            navigationItem.setParentId(parentId);
            navigationItem.setRootId(rootId);
        }
        navigationItem = navigationItems.add(navigationItem);

        for (NavigationItem child : navigationItem.getChildren()) {
            createNavigationNode(child, rootId, navigationItem.getId());
        }
        return navigationItem;
    }

    public void removeNavigationList(Id rootId) {

        List<NavigationItem> navigationItemList = navigationItems.havingRoot(rootId);

        for (NavigationItem item : navigationItemList) {
            this.navigationItems.remove(item);
        }
        ;
    }

    ;

    public void removeNavigationTree(Id id) {
        NavigationItem navigationItem = navigationItems.findById(NavigationItem.class, id);
        navigationItem.loadTree();
        removeNavigationNode(navigationItem);
    }

    ;

    private void removeNavigationNode(NavigationItem navigationItem) {
        navigationItems.remove(navigationItem);
        System.out.println("Removed nav child - " + navigationItem.getDisplayLabel());

        for (NavigationItem child : navigationItem.getChildren()) {
            removeNavigationNode(child);
        }
    }

    public void cloneNavigationTree(Id id) {
        NavigationItem navigationItem = navigationItems.findById(NavigationItem.class, id);
        navigationItem.loadTree();
        cloneNavigationNode(navigationItem, null, null);
    }

    private void cloneNavigationNode(NavigationItem navigationNode, Id rootId, Id parentId) {
        List<NavigationItem> navigationItemList = navigationNode.getChildren();

        if (rootId == null) {
            rootId = app.nextId();
            navigationNode.setId(rootId);
            navigationNode.setKey(navigationNode.getKey() + "-copy");
        } else {
            navigationNode.setId(app.nextId());
        }
        if (rootId != null) {
            navigationNode.setRootId(rootId);
        }
        if (parentId != null) {
            navigationNode.setParentId(parentId);
        }
        navigationItems.add(navigationNode);
        for (NavigationItem navigationListItem : navigationItemList) {
            cloneNavigationNode(navigationListItem, rootId, navigationNode.getId());
        }
    }

    @Override
    public Id[] toIds(NavigationItem navigationItem) {
        if (navigationItem == null)
            return null;

        List<NavigationItem> flattenedNavItems = new ArrayList<>();
        flattenNavigationTree(navigationItem, flattenedNavItems);

        return toIds(flattenedNavItems);
    }

    @Override
    public Id[] toIds(List<NavigationItem> navigationItems) {
        if (navigationItems == null)
            return null;

        List<Id> navigationItemIds = new ArrayList<>();

        for (NavigationItem n : navigationItems) {
            navigationItemIds.add(n.getId());
        }

        return navigationItemIds.toArray(new Id[navigationItemIds.size()]);
    }

    @Override
    public void completeNavigationItems(List<NavigationItem> navigationItems, List<UrlRewrite> urlRewrites) {
        if (navigationItems == null || navigationItems.size() == 0 || urlRewrites == null || urlRewrites.size() == 0)
            return;

        Map<Id, String> navigationItemURIs = toURIMap(urlRewrites);

        for (NavigationItem n : navigationItems) {
            String _uri = navigationItemURIs.get(n.getId());
            String uri = _uri == null || "".equals(_uri.trim()) ? "/catalog/product/list/" + n.getId() : _uri;

            // n.setUri(uri);
        }
    }

    @Override
    public void completeNavigationItems(NavigationItem navigationItem, List<UrlRewrite> urlRewrites) {
        List<NavigationItem> flattenedNavItems = new ArrayList<>();
        flattenNavigationTree(navigationItem, flattenedNavItems);

        completeNavigationItems(flattenedNavItems, urlRewrites);
    }

    @Override
    public NavigationItem findClosestNavigationItem(Product product) {
        if (product == null)
            return null;

        ProductList productList = findProductList(product);

        // Attempt to find a navigation item match using the located product and
        // product-list objects.
        NavigationItem navItem = findNavigationItem(productList, product);

        if (navItem == null && product.isVariant() && product.getParentId() != null && product.getParent() != null) {
            Product parent = product.getParent();
            navItem = findNavigationItem(productList, parent);

            if (navItem == null && parent.getProgrammeParent() != null)
                navItem = findNavigationItem(productList, parent.getProgrammeParent());
        }

        if (navItem == null && product.getProgrammeParent() != null) {
            navItem = findNavigationItem(productList, product.getProgrammeParent());
        }

        return navItem;
    }

    protected NavigationItem findNavigationItem(ProductList productList, Product product) {
        NavigationItem navigationItem = null;
        List<NavigationItem> navigationItems = null;

        NavigationItem rootNavItem = navigationService.findRootNavigationItem();

        // --------------------------------------------------------------------------------
        // Attempt to find a unique navigation item by the target object
        // product-list.
        // --------------------------------------------------------------------------------

        if (productList != null) {
            if (rootNavItem != null) {
                navigationItems = navigationService.getNavigationItemsByTargetObject(ObjectType.PRODUCT_LIST,
                    productList.getId(), rootNavItem.getId());

                // No need to search further, we have found a unique entry.
                if (navigationItems != null && navigationItems.size() == 1)
                    navigationItem = navigationItems.get(0);
            }
            // Attempt the same as above, but without root node.
            else {
                navigationItems = navigationService.getNavigationItemsByTargetObject(ObjectType.PRODUCT_LIST,
                    productList.getId());

                // No need to search further, we have found a unique entry.
                if (navigationItems != null && navigationItems.size() == 1)
                    navigationItem = navigationItems.get(0);
            }
        }

        // As there is a unique index on these 3 in the product navigation index
        // collection, there can only be one.
        if (navigationItem == null && productList != null && product != null) {
            if (rootNavItem != null) {
                List<ProductNavigationIndex> pniList = productNavigationIndexes.forValues(product, productList,
                    rootNavItem.getId());
                if (pniList != null && pniList.size() > 0) {
                    ProductNavigationIndex pni = pniList.get(0);
                    Id navItemId = pni.getNavigationItemId();

                    if (navItemId != null)
                        navigationItem = navigationService.getNavigationItem(navItemId);
                }
            }
            // Attempt the same as above, but without root node.
            else {
                List<ProductNavigationIndex> pniList = productNavigationIndexes.forValues(product, productList);
                if (pniList != null && pniList.size() > 0) {
                    ProductNavigationIndex pni = pniList.get(0);
                    Id navItemId = pni.getNavigationItemId();

                    if (navItemId != null)
                        navigationItem = navigationService.getNavigationItem(navItemId);
                }
            }
        }

        // If we could not find an exact match above and we have a result with
        // more than 1 match,
        // then we just take the first one.
        if (navigationItem == null && navigationItems != null && !navigationItems.isEmpty())
            navigationItem = navigationItems.get(0);

        return navigationItem;
    }

    protected ProductList findProductList(Product product) {
        ProductList productList = null;

        // --------------------------------------------------------------------------------
        // If the referrer method does not yield a result, we try finding an
        // entry in the
        // product navigation index collection.
        // --------------------------------------------------------------------------------

        if (productList == null) {
            NavigationItem rootNavItem = navigationService.findRootNavigationItem();

            if (rootNavItem != null) {
                List<ProductNavigationIndex> pniList = productNavigationIndexes.forProduct(product,
                    rootNavItem.getId());
                if (pniList != null && pniList.size() > 0) {
                    ProductNavigationIndex pni = pniList.get(0);
                    Id productListId = pni.getProductListId();

                    if (productListId != null)
                        productList = productLists.findById(ProductList.class, productListId);
                }
            }
        }

        // --------------------------------------------------------------------------------
        // If product list is still null, we try the same as above, but without
        // the
        // root navigation item.
        // --------------------------------------------------------------------------------

        if (productList == null) {
            List<ProductNavigationIndex> pniList = productNavigationIndexes.forProduct(product);
            if (pniList != null && pniList.size() > 0) {
                ProductNavigationIndex pni = pniList.get(0);
                Id productListId = pni.getProductListId();

                if (productListId != null)
                    productList = productLists.findById(ProductList.class, productListId);
            }
        }

        return productList;
    }

    protected void flattenNavigationTree(NavigationItem navigationItem,
        List<NavigationItem> flattenNavigationItemsToList) {
        if (navigationItem == null)
            return;

        flattenNavigationItemsToList.add(navigationItem);

        if (navigationItem.hasChildren()) {
            for (NavigationItem child : navigationItem.getChildren()) {
                flattenNavigationTree(child, flattenNavigationItemsToList);
            }
        }
    }

    protected Map<Id, String> toURIMap(List<UrlRewrite> urlRewrites) {
        Map<Id, String> navigationItemURIs = new HashMap<>();

        if (urlRewrites == null || urlRewrites.size() == 0)
            return navigationItemURIs;

        for (UrlRewrite urlRewrite : urlRewrites) {
            if (urlRewrite.isForProductList() && urlRewrite.getRequestURI() != null)
                navigationItemURIs.put(urlRewrite.getTargetObjectId(), urlRewrite.getRequestURI().getVal());
        }

        return navigationItemURIs;
    }
}
