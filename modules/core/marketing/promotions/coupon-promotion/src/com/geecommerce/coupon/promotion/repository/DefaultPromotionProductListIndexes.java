package com.geecommerce.coupon.promotion.repository;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.promotion.model.ProductListPromotionIndex;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DefaultPromotionProductListIndexes extends AbstractRepository implements PromotionProductListIndexes {

	private final MongoDao mongoDao;

	@Inject
	public DefaultPromotionProductListIndexes(MongoDao mongoDao) {
		this.mongoDao = mongoDao;
	}

	@Override
	public Dao dao() {
		return this.mongoDao;
	}

	@Override
	public ProductListPromotionIndex byProductList(Id productListId) {
		Map<String, Object> filter = new HashMap<>();
		filter.put(ProductListPromotionIndex.Col.PRODUCT_LIST_ID, productListId);
		return findOne(ProductListPromotionIndex.class, filter);
	}
}
