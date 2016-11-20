package com.geecommerce.guiwidgets.controller;

import java.util.List;
import java.util.Random;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.guiwidgets.model.ProductPromotion;
import com.geecommerce.guiwidgets.service.ProductPromotionService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

/**
 * Created by alexey on 17.12.14.
 */

@UrlBinding("/product-promotion/{$event}")
public class ProductPromotionAction extends BaseActionBean {

    private final ProductPromotionService productPromotionService;
    List<Product> products;
    ProductPromotion productPromotion;
    Product product;
    String promotionKey = null;

    @Inject
    public ProductPromotionAction(ProductPromotionService productPromotionService) {
        this.productPromotionService = productPromotionService;
    }

    @HandlesEvent("view")
    public Resolution view() {

        List<ProductPromotion> productPromotions = productPromotionService.getProductPromotionByKey(promotionKey);
        if (productPromotions != null && productPromotions.size() != 0) {
            productPromotion = productPromotions.get(0);
            products = productPromotionService.getProducts(productPromotion);

            if (products != null && products.size() > 0) {
                Random randomizer = new Random();
                product = products.get(randomizer.nextInt(products.size()));
            }
        }

        return view("product_promotion/view");
    }

    public List<Product> getProducts() {
        return products;
    }

    public Product getProduct() {
        return product;
    }

    public ProductPromotion getProductPromotion() {
        return productPromotion;
    }

    public String getPromotionKey() {
        return promotionKey;
    }

    public void setPromotionKey(String promotionKey) {
        this.promotionKey = promotionKey;
    }
}
