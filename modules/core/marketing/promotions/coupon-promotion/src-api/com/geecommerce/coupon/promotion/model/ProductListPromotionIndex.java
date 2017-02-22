package com.geecommerce.coupon.promotion.model;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

import java.util.Date;

public interface ProductListPromotionIndex extends Model {

	ProductListPromotionIndex setId(Id id);

	Id getProductListId();

	ProductListPromotionIndex setProductListId(Id productListId);

	Id getPromotionId();

	ProductListPromotionIndex setPromotionId(Id promotionId);

	Date getDateFrom();

	ProductListPromotionIndex setDateFrom(Date dateFrom);

	Date getDateTo();

	ProductListPromotionIndex setDateTo(Date dateTo);

	public static class Col
	{
		public static final String ID = "_id";
		public static final String PRODUCT_LIST_ID = "product_list_id";
		public static final String PROMOTION_ID = "promotion_id";
		public static final String DATE_FROM = "date_from";
		public static final String DATE_TO = "date_to";
	}

}
