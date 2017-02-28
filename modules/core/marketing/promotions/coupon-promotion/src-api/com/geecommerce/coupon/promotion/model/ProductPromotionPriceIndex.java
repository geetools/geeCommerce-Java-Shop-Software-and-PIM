package com.geecommerce.coupon.promotion.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

import java.util.Date;

public interface ProductPromotionPriceIndex extends Model {

	ProductPromotionPriceIndex setId(Id id);

	Id getProductId();

	ProductPromotionPriceIndex setProductId(Id productId);

	Id getPromotionId();

	ProductPromotionPriceIndex setPromotionId(Id promotionId);

	Double getPrice();

	ProductPromotionPriceIndex setPrice(Double price);

	Date getDateFrom();

	ProductPromotionPriceIndex setDateFrom(Date dateFrom);

	Date getDateTo();

	ProductPromotionPriceIndex setDateTo(Date dateTo);


	public static class Col
	{
		public static final String ID = "_id";
		public static final String PRODUCT_ID = "product_id";
		public static final String PROMOTION_ID = "promotion_id";
		public static final String PRICE = "price";
		public static final String DATE_FROM = "date_from";
		public static final String DATE_TO = "date_to";
	}

}
