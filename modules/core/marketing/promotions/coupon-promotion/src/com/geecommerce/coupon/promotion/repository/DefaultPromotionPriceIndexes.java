package com.geecommerce.coupon.promotion.repository;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DefaultPromotionPriceIndexes extends AbstractRepository implements PromotionPriceIndexes {

	private final MongoDao mongoDao;

	@Inject
	public DefaultPromotionPriceIndexes(MongoDao mongoDao) {
		this.mongoDao = mongoDao;
	}

	@Override
	public Dao dao() {
		return this.mongoDao;
	}

	@Override
	public ProductPromotionPriceIndex byProduct(Id productId) {
		Map<String, Object> filter = new HashMap<>();
		filter.put(ProductPromotionPriceIndex.Col.PRODUCT_ID, productId);
		return findOne(ProductPromotionPriceIndex.class, filter);
	}
}
