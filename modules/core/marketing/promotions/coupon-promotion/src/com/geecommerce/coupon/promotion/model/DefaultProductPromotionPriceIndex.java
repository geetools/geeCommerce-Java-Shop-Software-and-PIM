package com.geecommerce.coupon.promotion.model;

import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.Id;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Model(collection = "idx_product_promo_price", context = "store")
public class DefaultProductPromotionPriceIndex extends AbstractModel implements ProductPromotionPriceIndex {

	@Column(Col.ID)
	private Id id = null;

	@Column(Col.PRODUCT_ID)
	private Id productId = null;

	@Column(Col.PROMOTION_ID)
	private Id promotionId = null;

	@Column(Col.PRICE)
	private Double price = null;

	@Column(Col.DATE_FROM)
	private Date dateFrom = null;

	@Column(Col.DATE_TO)
	private Date dateTo = null;

	@Override
	public Id getId() {
		return id;
	}

	@Override
	public ProductPromotionPriceIndex setId(Id id) {
		this.id = id;
		return this;
	}

	@Override
	public Id getProductId() {
		return productId;
	}

	@Override
	public ProductPromotionPriceIndex setProductId(Id productId) {
		this.productId = productId;
		return this;
	}

	@Override
	public Id getPromotionId() {
		return promotionId;
	}

	@Override
	public ProductPromotionPriceIndex setPromotionId(Id promotionId) {
		this.promotionId = promotionId;
		return this;
	}

	@Override
	public Double getPrice() {
		return price;
	}

	@Override
	public ProductPromotionPriceIndex setPrice(Double price) {
		this.price = price;
		return this;
	}

	@Override
	public Date getDateFrom() {
		return dateFrom;
	}

	@Override
	public ProductPromotionPriceIndex setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
		return this;
	}

	@Override
	public Date getDateTo() {
		return dateTo;
	}

	@Override
	public ProductPromotionPriceIndex setDateTo(Date dateTo) {
		this.dateTo = dateTo;
		return this;
	}

	@Override
	public void fromMap(Map<String, Object> map)
	{
		this.id = id_(map.get(Col.ID));
		this.productId = id_(map.get(Col.PRODUCT_ID));
		this.promotionId = id_(map.get(Col.PROMOTION_ID));
		this.price = double_(map.get(Col.PRICE));
		this.dateFrom = date_(map.get(Col.DATE_FROM));
		this.dateTo = date_(map.get(Col.DATE_TO));
	}

	@Override
	public Map<String, Object> toMap()
	{
		Map<String, Object> m = new LinkedHashMap<>(super.toMap());

		m.put(Col.ID, getId());
		m.put(Col.PRODUCT_ID, getProductId());
		m.put(Col.PROMOTION_ID, getPromotionId());
		m.put(Col.PRICE, getPrice());
		m.put(Col.DATE_FROM, getDateFrom());
		m.put(Col.DATE_TO, getDateTo());

		return m;
	}
}
