package com.geecommerce.coupon.promotion.widgets;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.repository.Coupons;
import com.geecommerce.coupon.promotion.model.CouponPromotion;
import com.geecommerce.coupon.promotion.model.ProductListPromotionIndex;
import com.geecommerce.coupon.promotion.repository.CouponPromotions;
import com.geecommerce.coupon.promotion.repository.PromotionProductListIndexes;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@Widget(name = "cp_product_list")
public class CouponPromotionProductListWidget extends AbstractWidgetController implements WidgetController
{
    private final String PARAM_PRODUCT_LIST = "product_list";
    protected final PromotionProductListIndexes promotionProductListIndexes;
    protected final ProductLists productLists;
    protected final CouponPromotions couponPromotions;
    protected final Coupons coupons;

    @Inject
    public CouponPromotionProductListWidget(PromotionProductListIndexes promotionProductListIndexes, ProductLists productLists, CouponPromotions couponPromotions, Coupons coupons) {
        this.promotionProductListIndexes = promotionProductListIndexes;
        this.productLists = productLists;
        this.couponPromotions = couponPromotions;
        this.coupons = coupons;
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception
    {
        String id = widgetCtx.getParam(PARAM_PRODUCT_LIST);

        widgetCtx.setParam("couponPromotionMessage", "");
        if(!StringUtils.isBlank(id)){
            Id productListId = Id.parseId(id);
            ProductListPromotionIndex index = promotionProductListIndexes.byProductList(productListId);
            ProductList productList = productLists.findById(ProductList.class, productListId);
            if(index != null && productList != null) {
                CouponPromotion couponPromotion = couponPromotions.findById(CouponPromotion.class, index.getPromotionId());
                Coupon coupon = coupons.findById(Coupon.class, couponPromotion.getCouponId());

                if(coupon != null && couponPromotion != null){
                    String message = couponPromotion.getDescription().str();
                    if(!StringUtils.isBlank(message)){
                        message = message.replace("%category%", productList.getLabel().str());
                        message = message.replace("%end%", new SimpleDateFormat("dd.MM.yyyy").format(coupon.getToDate()));

                        widgetCtx.setParam("couponPromotionMessage", message);
                    }
                }
            }
        }

/*        widgetCtx.setParam("promoConditionUrl", "");
        if(!StringUtils.isBlank(id)){
            Id productListId = Id.parseId(id);
            ProductListPromotionIndex index = promotionProductListIndexes.byProductList(productListId);
            ProductList productList = productLists.findById(ProductList.class, productListId);
            if(index != null && productList != null) {
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

                    if(productList.hasAttribute("promo_name")){
                        widgetCtx.setParam("promoName", productList.getAttribute("promo_name").getValue().getStr());
                    } else {
                        widgetCtx.setParam("promoName", productList.getLabel().str());
                    }

                }
            }
        }*/
        widgetCtx.render();
    }


}