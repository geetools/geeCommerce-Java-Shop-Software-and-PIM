package com.geecommerce.coupon.promotion.model;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.owlike.genson.annotation.JsonIgnore;

import java.util.List;

public interface CouponPromotion extends Model {

	CouponPromotion setId(Id id);

	ContextObject<String> getLabel();

	CouponPromotion setLabel(ContextObject<String> label);

	ContextObject<String> getDescription();

	CouponPromotion setDescription(ContextObject<String> description);

	ContextObject<String> getDescriptionProduct();

	CouponPromotion setDescriptionProduct(ContextObject<String> descriptionProduct);

	Id getCouponId();

	CouponPromotion setCouponId(Id couponId);

	Id getConditionMediaAssetId();

	CouponPromotion setConditionMediaAssetId(Id conditionMediaAssetId);

	MediaAsset getConditionMediaAsset();

	List<Id> getProductListIds();

	CouponPromotion setProductListIds(List<Id> productListIds);

	@JsonIgnore
	List<ProductList> getProductLists();

	public ContextObject<Boolean> getEnabled();

	public CouponPromotion setEnabled(ContextObject<Boolean> enabled);

	public static class Col
	{
		public static final String ID = "_id";
		public static final String LABEL = "label";
		public static final String DESCRIPTION = "descr";
		public static final String CONDITION_MEDIA_ASSET_ID = "c_ma_id";
		public static final String DESCRIPTION_PRODUCT = "descr_prd";
		public static final String COUPON_ID = "coupon_id";
		public static final String PRODUCT_LIST_IDS = "product_list_ids";
		public static final String ENABLED = "enabled";
	}
}
