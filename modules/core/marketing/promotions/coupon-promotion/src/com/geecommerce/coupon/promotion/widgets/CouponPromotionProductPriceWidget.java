package com.geecommerce.coupon.promotion.widgets;

import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.coupon.repository.Coupons;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;
import com.geecommerce.coupon.promotion.repository.CouponPromotions;
import com.geecommerce.coupon.promotion.repository.PromotionPriceIndexes;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Widget(name = "cp_product_price")
public class CouponPromotionProductPriceWidget extends AbstractWidgetController implements WidgetController
{
    private final String PARAM_PRODUCT = "product_id";
    protected final PromotionPriceIndexes promotionPriceIndexes;
    protected final ProductService productService;
    protected final CouponPromotions couponPromotions;
    protected final Coupons coupons;

    @Inject
    public CouponPromotionProductPriceWidget(PromotionPriceIndexes promotionPriceIndexes, ProductService productService, CouponPromotions couponPromotions, Coupons coupons) {
        this.promotionPriceIndexes = promotionPriceIndexes;
        this.productService = productService;
        this.couponPromotions = couponPromotions;
        this.coupons = coupons;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception
    {
        String id = widgetCtx.getParam(PARAM_PRODUCT);

        widgetCtx.setParam("promotionPrice", null);
        if(!StringUtils.isBlank(id)){
            Id productId = Id.parseId(id);
            ProductPromotionPriceIndex index = promotionPriceIndexes.byProduct(productId);

            if(index != null){
                widgetCtx.setParam("promotionPrice", index.getPrice());
            }
        }
        widgetCtx.render();
    }


}