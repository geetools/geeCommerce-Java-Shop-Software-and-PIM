package com.geecommerce.coupon.promotion.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.Date;

@Model(collection = "idx_product_list_promo", context = "store")
public class DefaultProductListPromotionIndex extends AbstractModel implements ProductListPromotionIndex {

	@Column(Col.ID)
	private Id id = null;

	@Column(Col.PRODUCT_LIST_ID)
	private Id productListId = null;

	@Column(Col.PROMOTION_ID)
	private Id promotionId = null;

	@Column(Col.DATE_FROM)
	private Date dateFrom = null;

	@Column(Col.DATE_TO)
	private Date dateTo = null;

	@Override
	public Id getId() {
		return id;
	}

	@Override
	public ProductListPromotionIndex setId(Id id) {
		this.id = id;
		return this;
	}

	@Override
	public Id getProductListId() {
		return productListId;
	}

	@Override
	public ProductListPromotionIndex setProductListId(Id productListId) {
		this.productListId = productListId;
		return this;
	}

	@Override
	public Id getPromotionId() {
		return promotionId;
	}

	@Override
	public ProductListPromotionIndex setPromotionId(Id promotionId) {
		this.promotionId = promotionId;
		return this;
	}

	@Override
	public Date getDateFrom() {
		return dateFrom;
	}

	@Override
	public ProductListPromotionIndex setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
		return this;
	}

	@Override
	public Date getDateTo() {
		return dateTo;
	}

	@Override
	public ProductListPromotionIndex setDateTo(Date dateTo) {
		this.dateTo = dateTo;
		return this;
	}
}
