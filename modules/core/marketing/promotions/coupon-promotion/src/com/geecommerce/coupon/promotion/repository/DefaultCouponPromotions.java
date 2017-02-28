package com.geecommerce.coupon.promotion.repository;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.google.inject.Inject;

@Repository
public class DefaultCouponPromotions extends AbstractRepository implements CouponPromotions {
	private final MongoDao mongoDao;

	@Inject
	public DefaultCouponPromotions(MongoDao mongoDao) {
		this.mongoDao = mongoDao;
	}

	@Override
	public Dao dao() {
		return this.mongoDao;
	}
}
