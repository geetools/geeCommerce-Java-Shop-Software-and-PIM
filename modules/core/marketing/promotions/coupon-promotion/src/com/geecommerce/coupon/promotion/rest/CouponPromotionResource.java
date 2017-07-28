package com.geecommerce.coupon.promotion.rest;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.promotion.helper.CouponPromotionHelper;
import com.geecommerce.coupon.promotion.model.CouponPromotion;
import com.geecommerce.coupon.promotion.service.CouponPromotionService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;

@Api
@Singleton
@Path("/v1/coupon-promotions")
public class CouponPromotionResource extends AbstractResource {

    private final RestService service;
    private final CouponPromotionService couponPromotionService;
    private final CouponPromotionHelper couponPromotionHelper;

    @Inject
    public CouponPromotionResource(RestService service, CouponPromotionService couponPromotionService, CouponPromotionHelper couponPromotionHelper) {
        this.service = service;
        this.couponPromotionService = couponPromotionService;
        this.couponPromotionHelper = couponPromotionHelper;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCouponPromotions(@FilterParam Filter filter)
    {
        return ok(service.get(CouponPromotion.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public CouponPromotion getCouponPromotion(@PathParam("id") Id id)
    {
        return checked(service.get(CouponPromotion.class, id));
    }

    @GET
    @Path("/clone/{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public CouponPromotion getCloneCouponPromotion(@PathParam("id") Id id)
    {
        CouponPromotion couponPromotion = service.get(CouponPromotion.class, id);
        couponPromotion.setId(null);
        couponPromotion.setEnabled(new ContextObject<>(false));
        couponPromotion = service.create(couponPromotion);
        return checked(couponPromotion);
    }


    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createCouponPromotion(@ModelParam CouponPromotion couponPromotion)
    {
        couponPromotion = service.create(couponPromotion);
        couponPromotionHelper.createPromotionIndex();
        return created(couponPromotion);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateCouponPromotion(@PathParam("id") Id id, Update update)
    {
        if (id != null && update != null)
        {
            CouponPromotion c = checked(service.get(CouponPromotion.class, id));
            c.set(update.getFields());
            service.update(c);

            couponPromotionHelper.createPromotionIndex();

            return ok(c);
        }
        return notFound();
    }

    @DELETE
    @Path("{id}")
    public void removeCouponPromotion(@PathParam("id") Id id)
    {
        CouponPromotion couponPromotion = checked(service.get(CouponPromotion.class, id));
        //couponPromotion.setDeleted(true);
        service.remove(couponPromotion);
    }

    @GET
    @Path("/coupons")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCoupons()
    {
        return ok(couponPromotionService.getCoupons());
    }




    @GET
    @Path("{id}/notPromotionProductLists")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getNotTabAttributes(@PathParam("id") Id id, @FilterParam Filter filter)
    {
        List<Id> productListIds = new LinkedList<>();

        if (id != null)
        {
            CouponPromotion couponPromotion = service.get(CouponPromotion.class, id);
            if(couponPromotion != null) {
                productListIds = couponPromotion.getProductListIds();
            }
        }

        Map<String, Object> productListsFilter = new HashMap<>();
        if(productListIds != null && !productListIds.isEmpty()) {
            Map<String, Object> productListsNotInFilter = new HashMap<>();
            productListsNotInFilter.put("$nin", productListIds);
            productListsFilter.put(ProductList.Col.ID, productListsNotInFilter);
            productListsFilter.put(ProductList.Col.SALE, false);
            productListsFilter.put(ProductList.Col.SPECIAL, false);
        }

        return ok(service.get(ProductList.class, productListsFilter, queryOptions(filter)));
    }

    @PUT
    @Path("{id}/product-list/{productListId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void addProductList(@PathParam("id") Id id, @PathParam("productListId") Id productListId)
    {
        if (id != null && productListId != null)
        {
            CouponPromotion couponPromotion = service.get(CouponPromotion.class, id);
            if(couponPromotion != null) {
                couponPromotion.getProductListIds().add(productListId);
                service.update(couponPromotion);
            }
        }
    }

    @DELETE
    @Path("{id}/product-list/{productListId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeProductList(@PathParam("id") Id id, @PathParam("productListId") Id productListId)
    {
        if (id != null && productListId != null)
        {
            CouponPromotion couponPromotion = service.get(CouponPromotion.class, id);
            if(couponPromotion != null) {
                couponPromotion.getProductListIds().remove(productListId);
                service.update(couponPromotion);
            }
        }
    }

}
