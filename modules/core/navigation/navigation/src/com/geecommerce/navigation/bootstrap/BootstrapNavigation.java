package com.geecommerce.navigation.bootstrap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.catalog.product.repository.ProductNavigationIndexes;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.bootstrap.AbstractBootstrap;
import com.geecommerce.core.bootstrap.annotation.Bootstrap;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Requests;
import com.geecommerce.navigation.NavigationConstant;
import com.geecommerce.navigation.model.NavigationItem;
import com.geecommerce.navigation.service.NavigationService;
import com.google.inject.Inject;

@Bootstrap
public class BootstrapNavigation extends AbstractBootstrap {
    protected static final String PRODUCT_LIST_BASE_URI = "/catalog/product-list/view/";
    protected static final String PRODUCT_BASE_URI = "/catalog/product/view/";
    protected static final Set<String> BASE_URI_BLACKLIST = new HashSet<String>();
    static {
        BASE_URI_BLACKLIST.add("/customer/");
        BASE_URI_BLACKLIST.add("/checkout/");
        BASE_URI_BLACKLIST.add("/cart/");
    }

    protected final NavigationService navigationService;
    protected final ProductNavigationIndexes productNavigationIndexes;
    protected final Products products;

    @Inject
    protected BootstrapNavigation(NavigationService navigationService,
        ProductNavigationIndexes productNavigationIndexes, Products products) {
        this.navigationService = navigationService;
        this.productNavigationIndexes = productNavigationIndexes;
        this.products = products;
    }

    @Override
    public void init() {
        App app = App.get();

        if (app.isAjaxRequest() || app.isAPIRequest() || app.isMediaRequest())
            return;

        HttpServletRequest request = getRequest();
        String path = request.getRequestURI();

        ApplicationContext appCtx = app.context();
        UrlRewrite urlRewrite = appCtx.getUrlRewrite();

        if (isInBlackList(path) || (urlRewrite != null && isInBlackList(urlRewrite.getTargetURL())))
            return;

        Id productListId = null;

        // -------------------------------------------------------------------------
        // None-Product-List Request.
        // -------------------------------------------------------------------------

        // If we are in a product page, we attempt to get the product-list URI
        // via the referrer.
        // This is obviously client dependent, but should work for most users.
        if (!path.startsWith(PRODUCT_LIST_BASE_URI) && (urlRewrite == null || !urlRewrite.isForProductList())) {
            String referrer = Requests.getReferrerURI(request);

            if (!Str.isEmpty(referrer)) {
                // Optimal case - referrer exists and it is of type
                // product-list.
                if (referrer.startsWith(PRODUCT_LIST_BASE_URI)) {
                    // Remove the slash so that the last part is just the id.
                    if (referrer.endsWith(Str.SLASH))
                        referrer = referrer.substring(0, referrer.length() - 1);

                    String idPart = referrer.substring(PRODUCT_LIST_BASE_URI.length());

                    if (!Str.isEmpty(idPart))
                        productListId = Id.valueOf(idPart);
                }
                // The referrer may be a rewritten URI, so lets try translating
                // it.
                else if (!app.helper(UrlRewriteHelper.class).isExcludedFromURLRewriting(referrer)) {
                    UrlRewrite referrerURLRewrite = app.getUrlRewrite(referrer);

                    if (referrerURLRewrite != null && referrerURLRewrite.isForProductList())
                        productListId = referrerURLRewrite.getTargetObjectId();
                }
            }

            // Unfortunately none of the standard cases above worked, so we now
            // try and locate a productList via the
            // productId.
            if (productListId == null
                && (path.startsWith(PRODUCT_BASE_URI) || (urlRewrite != null && urlRewrite.isForProduct()))) {
                Id productId = null;

                // Simple case - URL-rewrite already exists and it is of type
                // product.
                if (urlRewrite != null && urlRewrite.isForProduct()) {
                    productId = urlRewrite.getTargetObjectId();
                }
                // Otherwise it must at least be a product-view-URI which also
                // contains the productId.
                else {
                    // Remove the slash so that the last part is just the id.
                    if (path.endsWith(Str.SLASH))
                        path = path.substring(0, path.length() - 1);

                    String idPart = path.substring(PRODUCT_BASE_URI.length());

                    if (!Str.isEmpty(idPart))
                        productId = Id.valueOf(idPart);
                }

                // Attempt to find the productListId via the productId.
                productListId = findProductListIdByProductId(productId);

                // If we have still not found anything, then it may be because
                // the product is part of a
                // programme and not listed in any product-lists.
                if (productListId == null && productId != null) {
                    Product product = products.findById(Product.class, productId);

                    // If we cannot find a productListId for the current
                    // product, we may have more luck with its
                    // parent-programme-product.
                    if (product != null && !product.isProgramme() && !product.isVariant()) {
                        Product programmeParent = product.getProgrammeParent();
                        if (programmeParent != null) {
                            productListId = findProductListIdByProductId(programmeParent.getId());
                        }
                    }
                }
            }
        }
        // -------------------------------------------------------------------------
        // Product-List Request.
        // -------------------------------------------------------------------------
        else {
            // The most simple case is obviously if we are in a
            // product-list-page itself.

            // If URLRewrite exists, grab the targetObjectId.
            if (urlRewrite != null && urlRewrite.isForProductList())
                productListId = urlRewrite.getTargetObjectId();

            // Attempt to parse it out of URL if productListId could not be
            // found.
            if (productListId == null && path.startsWith(PRODUCT_LIST_BASE_URI)) {
                // Remove the slash so that the last part is just the id.
                if (path.endsWith(Str.SLASH))
                    path = path.substring(0, path.length() - 1);

                String idPart = path.substring(PRODUCT_LIST_BASE_URI.length());

                if (!Str.isEmpty(idPart))
                    productListId = Id.valueOf(idPart);
            }
        }

        // Finally we should have a productListId at this point. Now we attempt
        // to find a navigation-item for it.
        if (productListId != null) {

            NavigationItem rootNavItem = navigationService.findRootNavigationItem();

            List<NavigationItem> navItems = null;

            // Now we try to find out the matching navItemId.
            if (rootNavItem != null) {
                navItems = navigationService.getNavigationItemsByTargetObject(ObjectType.PRODUCT_LIST, productListId,
                    rootNavItem.getId());
            } else {
                navItems = navigationService.getNavigationItemsByTargetObject(ObjectType.PRODUCT_LIST, productListId);
            }

            if (navItems != null && navItems.size() > 0) {
                NavigationItem navItem = navItems.get(0);

                if (navItem != null) {
                    List<Id> navItemIds = new ArrayList<>();
                    List<NavigationItem> flatNavItems = new ArrayList<>();

                    navItem.collectIds(navItemIds);
                    navItem.flatten(flatNavItems);

                    // If everything has gone well up to this point, we should
                    // now have the selected navigation
                    // hierarchy, which we insert into our
                    // thread-local-registry.
                    app.registryPut(NavigationConstant.NAV_ITEM_IDS, navItemIds);
                    app.registryPut(NavigationConstant.NAV_ITEMS, flatNavItems);
                }
            }
        }
    }

    private Id findProductListIdByProductId(Id productId) {
        if (productId == null)
            return null;

        Id productListId = null;

        NavigationItem rootNavItem = navigationService.findRootNavigationItem();

        if (rootNavItem != null) {
            List<ProductNavigationIndex> pniList = productNavigationIndexes.forProduct(productId, rootNavItem.getId());
            if (pniList != null && pniList.size() > 0) {
                ProductNavigationIndex pni = pniList.get(0);
                productListId = pni.getProductListId();
            }
        }

        if (productListId == null) {
            List<ProductNavigationIndex> pniList = productNavigationIndexes.forProduct(productId);
            if (pniList != null && pniList.size() > 0) {
                ProductNavigationIndex pni = pniList.get(0);
                productListId = pni.getProductListId();
            }
        }

        return productListId;
    }

    private boolean isInBlackList(String path) {
        for (String baseUri : BASE_URI_BLACKLIST) {
            if (path.startsWith(baseUri))
                return true;
        }

        return false;
    }
}
