package com.geecommerce.navigation.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.controller.ProductController;
import com.geecommerce.catalog.product.controller.ProductListController;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.ProductNavigationIndexes;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Requests;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.navigation.model.NavigationItem;
import com.geecommerce.navigation.service.NavigationService;
import com.geemvc.view.GeemvcKey;
import com.google.inject.Inject;

@Widget(name = "breadcrumbs_navigation")
public class BreadcrumbsNavigation extends AbstractWidgetController implements WidgetController {
    private static final String PRODUCT_LIST_BASE_URI = "/catalog/product-list/view/";

    private static final String PRODUCT_PARAM = "product";
    private static final String CHILD_PRODUCT_PARAM = "childProduct";
    private static final String PRODUCT_LIST1_PARAM = "productList1";
    private static final String PRODUCT_LIST2_PARAM = "productList2";
    private static final String NAV_ITEM1_PARAM = "navItem1";
    private static final String NAV_ITEM2_PARAM = "navItem2";

    private static final String NAV_ITEMS_PARAM = "navItems";

    private static final String IS_PRODUCT_PARAM = "isProduct";
    private static final String IS_PRODUCT_CONTACT_FORM_PARAM = "isProductContactForm";
    private static final String IS_PRODUCT_LIST_PARAM = "isProductList";
    private static final String IS_NAV_SITEMAP_PARAM = "isNavSitemap";

    private static final String VIEW = "navigation/breadcrumbs_navigation";

    private final NavigationService navigationService;
    private final ProductLists productLists;
    private final ProductNavigationIndexes productNavigationIndexes;

    @Inject
    public BreadcrumbsNavigation(NavigationService navigationService, ProductLists productLists, ProductNavigationIndexes productNavigationIndexes) {
        this.navigationService = navigationService;
        this.productLists = productLists;
        this.productNavigationIndexes = productNavigationIndexes;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        Class<?> controllerClass = (Class<?>) request.getAttribute(GeemvcKey.CONTROLLER_CLASS);

        boolean isProductList = false;
        boolean isProduct = false;
        boolean isProductContactForm = false;
        boolean isNavSitemap = false;

        Product product = null;
        Product childProduct = null;
        ProductList productList = null;

        Object customBreadcrumbs = widgetCtx.getParam("breadcrumbs");

        if (customBreadcrumbs != null) {
            widgetCtx.setParam(IS_PRODUCT_PARAM, isProduct);
            widgetCtx.setParam(IS_PRODUCT_CONTACT_FORM_PARAM, isProductContactForm);
            widgetCtx.setParam(IS_PRODUCT_LIST_PARAM, isProductList);
            widgetCtx.setParam(IS_NAV_SITEMAP_PARAM, isNavSitemap);

            widgetCtx.setParam("breadcrumbs", customBreadcrumbs);
        } else {
            // -------------------------------------------------------------
            // Locate objects needed for product breadcrumbs.
            // -------------------------------------------------------------
            if (ProductController.class.isAssignableFrom(controllerClass)) {
                productList = findProductList(product, request);

                isProduct = true;
                product = (Product) request.getAttribute("product");

                if (product.isProgrammeChild()) {
                    childProduct = product;
                    product = product.getProgrammeParent();

                    productList = findProductList(product, request);
                }

                if (productList == null && product != null)
                    productList = findProductList(product, request);
            }

            // -------------------------------------------------------------
            // Locate objects needed for product-list breadcrumbs.
            // -------------------------------------------------------------
            if (ProductListController.class.isAssignableFrom(controllerClass)) {
                productList = (ProductList) request.getAttribute("productList");
                isProductList = true;
            }

            // Attempt to find a navigation item match using the located product
            // and product-list objects.
            NavigationItem navItem = findNavigationItem(productList, product);

            widgetCtx.setParam(IS_PRODUCT_PARAM, isProduct);
            widgetCtx.setParam(IS_PRODUCT_CONTACT_FORM_PARAM, isProductContactForm);
            widgetCtx.setParam(IS_PRODUCT_LIST_PARAM, isProductList);

            widgetCtx.setParam(PRODUCT_PARAM, product);
            widgetCtx.setParam(NAV_ITEM1_PARAM, navItem);
            widgetCtx.setParam(PRODUCT_LIST1_PARAM, productList);

            if (childProduct != null)
                widgetCtx.setParam(CHILD_PRODUCT_PARAM, childProduct);

            List<NavigationItem> productListNavItems = new ArrayList<>();

            if (navItem != null) {
                productListNavItems.add(navItem);
            }

            // Try and get additional information if possible.
            if (navItem != null) {
                if (navItem.getLevel() > 1) {
                    NavigationItem parentNavItem = navItem.getParent();

                    while (parentNavItem != null) {
                        productListNavItems.add(parentNavItem);

                        if (parentNavItem.getLevel() > 1) {
                            parentNavItem = parentNavItem.getParent();
                        } else {
                            break;
                        }
                    }
                }

                Collections.reverse(productListNavItems);

                widgetCtx.setParam(NAV_ITEMS_PARAM, productListNavItems);
            }
        }

        widgetCtx.render(VIEW);
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

    protected ProductList findProductList(Product product, HttpServletRequest request) {
        ProductList productList = null;

        // --------------------------------------------------------------------------------
        // First we attempt to find the previous product-list by analyzing the
        // referrer
        // and trying to find a matching entry in the database.
        // --------------------------------------------------------------------------------

        String referrerURI = Requests.getReferrerURI(request);

        if (!Str.isEmpty(referrerURI)) {
            UrlRewrite urlRewrite = app.getUrlRewrite(referrerURI);

            if (urlRewrite != null) {
                ObjectType targetObjType = urlRewrite.getTargetObjectType();
                Id targetObjId = urlRewrite.getTargetObjectId();

                if (targetObjType != null && targetObjType == ObjectType.PRODUCT_LIST)
                    productList = productLists.findById(ProductList.class, targetObjId);
            } else if (referrerURI.startsWith(PRODUCT_LIST_BASE_URI)) {
                // Remove the slash so that the last part is just the id.
                if (referrerURI.endsWith(Str.SLASH))
                    referrerURI = referrerURI.substring(0, referrerURI.length() - 1);

                String idPart = referrerURI.substring(PRODUCT_LIST_BASE_URI.length());

                if (!Str.isEmpty(idPart))
                    productList = productLists.findById(ProductList.class, Id.valueOf(idPart));
            }
        }

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
}
