package com.geecommerce.coupon.promotion.service;


import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.repository.Coupons;
import com.geecommerce.coupon.promotion.model.CouponPromotion;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;
import com.geecommerce.coupon.promotion.repository.CouponPromotions;
import com.geecommerce.coupon.promotion.repository.PromotionPriceIndexes;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.*;

@Service
public class DefaultCouponPromotionService implements CouponPromotionService {

	private final CouponPromotions promotions;
	private final PromotionPriceIndexes priceIndexes;
	private final Coupons coupons;

	@Inject
	public DefaultCouponPromotionService(CouponPromotions promotions, PromotionPriceIndexes priceIndexes, Coupons coupons) {
		this.promotions = promotions;
		this.priceIndexes = priceIndexes;
		this.coupons = coupons;
	}


	@Override
	public List<CouponPromotion> getPromotions() {
		return promotions.findAll(CouponPromotion.class);
	}

	@Override
	public List<CouponPromotion> getPromotions(List<Id> ids) {
		if(ids != null && !ids.isEmpty()) {
			return promotions.findByIds(CouponPromotion.class, ids.toArray(new Id[0]));
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public CouponPromotion getPromotion(Id id) {
		return promotions.findById(CouponPromotion.class, id);
	}

	@Override
	public ProductPromotionPriceIndex createPromotionPriceIndex(ProductPromotionPriceIndex priceIndex) {
		return priceIndexes.add(priceIndex);
	}

	@Override
	public CouponPromotion addPromotion(CouponPromotion promotion) {
		return promotions.add(promotion);
	}

	@Override
	public void updatePromotion(CouponPromotion promotion) {
		promotions.update(promotion);
	}


	@Override
	public List<Coupon> getCoupons() {
			Map<String, Object> filter = new HashMap<>();
			filter.put(Coupon.Col.AUTO, true);

			Date now = new Date();
			DBObject gteClause = new BasicDBObject();
			gteClause.put("$gte", now);
			filter.put(Coupon.Col.TO_DATE, gteClause);

			List<Coupon> findedCoupons = coupons.find(Coupon.class, filter);

			return findedCoupons;
	}

	@Override
	public void IndexPromotionPrices() {

	}
}
