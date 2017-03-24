package com.geecommerce.coupon.promotion.widgets;

import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.repository.Coupons;
import com.geecommerce.coupon.promotion.model.CouponPromotion;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;
import com.geecommerce.coupon.promotion.repository.CouponPromotions;
import com.geecommerce.coupon.promotion.repository.PromotionPriceIndexes;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@Widget(name = "cp_product")
public class CouponPromotionProductWidget extends AbstractWidgetController implements WidgetController
{
    private final String PARAM_PRODUCT = "product_id";
    protected final PromotionPriceIndexes promotionPriceIndexes;
    protected final ProductService productService;
    protected final CouponPromotions couponPromotions;
    protected final Coupons coupons;

    @Inject
    public CouponPromotionProductWidget(PromotionPriceIndexes promotionPriceIndexes, ProductService productService, CouponPromotions couponPromotions, Coupons coupons) {
        this.promotionPriceIndexes = promotionPriceIndexes;
        this.productService = productService;
        this.couponPromotions = couponPromotions;
        this.coupons = coupons;
    }


    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception
    {
        String id = widgetCtx.getParam(PARAM_PRODUCT);

        widgetCtx.setParam("couponPromotionMessage", "");
        if(!StringUtils.isBlank(id)){
            Id productId = Id.parseId(id);
            ProductPromotionPriceIndex index = promotionPriceIndexes.byProduct(productId);
            if(index != null){
                CouponPromotion couponPromotion = couponPromotions.findById(CouponPromotion.class, index.getPromotionId());
                Coupon coupon = coupons.findById(Coupon.class, couponPromotion.getCouponId());

                if(coupon != null && couponPromotion != null){
                    if(couponPromotion.getDescriptionProduct()!= null) {
                        String message = couponPromotion.getDescriptionProduct().str();
                        if (!StringUtils.isBlank(message)) {
                            message = message.replace("%end%", new SimpleDateFormat("dd.MM.yyyy").format(coupon.getToDate()));
                            widgetCtx.setParam("couponPromotionMessage", message);
                        }
                    }
                }
            }
        }


/*        widgetCtx.setParam("promoConditionUrl", "");
        if(!StringUtils.isBlank(id)){
            Id productId = Id.parseId(id);
            ProductPromotionPriceIndex index = promotionPriceIndexes.byProduct(productId);
            if(index != null && index.getPromotionId() != null){
                CouponPromotion couponPromotion = couponPromotions.findById(CouponPromotion.class, index.getPromotionId());
                Coupon coupon = coupons.findById(Coupon.class, couponPromotion.getCouponId());

                if(coupon != null && couponPromotion != null){
                    String discount = new DecimalFormat("#").format(coupon.getCouponAction().getDiscountAmount()) + "%";
                    widgetCtx.setParam("promoDiscount", discount);
                    widgetCtx.setParam("promoEndTime",  new SimpleDateFormat("dd.MM.yyyy").format(coupon.getToDate()));
                    widgetCtx.setParam("promoEndTimeForCounter", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(coupon.getToDate()));

                    if(couponPromotion.getConditionMediaAsset() != null){
                        widgetCtx.setParam("promoConditionUrl", couponPromotion.getConditionMediaAsset().getUrl());
                    }
                }
            }
        }*/
        widgetCtx.render();
    }


}