package com.geecommerce.coupon.promotion.model;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.repository.MediaAssets;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Model("coupon_promotions")
public class DefaultCouponPromotion extends AbstractModel implements CouponPromotion {

	private static final long serialVersionUID = -1349883601294831373L;

	@Column(Col.ID)
	private Id id = null;

	@Column(Col.LABEL)
	private ContextObject<String> label = null;

	@Column(Col.DESCRIPTION)
	private ContextObject<String> description = null;

	@Column(Col.DESCRIPTION_PRODUCT)
	private ContextObject<String> descriptionProduct = null;

	@Column(Col.COUPON_ID)
	private Id couponId = null;

	@Column(Col.CONDITION_MEDIA_ASSET_ID)
	private Id conditionMediaAssetId = null;

	@Column(Col.PRODUCT_LIST_IDS)
	private List<Id> productListIds;

	@Column(Col.ENABLED)
	private ContextObject<Boolean> enabled;

	private MediaAsset conditionMediaAsset = null;

	@Override
	public Id getId() {
		return id;
	}

	private final ProductLists productListRepository;
	private final MediaAssets mediaAssets;

	public DefaultCouponPromotion() {
		this(i(ProductLists.class), i(MediaAssets.class));
	}

	@Inject
	public DefaultCouponPromotion(ProductLists productListRepository, MediaAssets mediaAssets) {
		this.productListRepository = productListRepository;
		this.mediaAssets = mediaAssets;
	}

	@Override
	public CouponPromotion setId(Id id) {
		this.id = id;
		return this;
	}

	@Override
	public ContextObject<String> getLabel() {
		return label;
	}

	@Override
	public CouponPromotion setLabel(ContextObject<String> label) {
		this.label = label;
		return this;
	}

	@Override
	public ContextObject<String> getDescription() {
		return description;
	}

	@Override
	public CouponPromotion setDescription(ContextObject<String> description) {
		this.description = description;
		return this;
	}

	@Override
	public ContextObject<String> getDescriptionProduct() {
		return descriptionProduct;
	}

	@Override
	public CouponPromotion setDescriptionProduct(ContextObject<String> descriptionProduct) {
		this.descriptionProduct = descriptionProduct;
		return this;
	}

	@Override
	public Id getCouponId() {
		return couponId;
	}

	@Override
	public CouponPromotion setCouponId(Id couponId) {
		this.couponId = couponId;
		return this;
	}

	@Override
	public Id getConditionMediaAssetId() {
		return conditionMediaAssetId;
	}

	@Override
	public CouponPromotion setConditionMediaAssetId(Id conditionMediaAssetId) {
		this.conditionMediaAssetId = conditionMediaAssetId;
		return this;
	}

	@Override
	public MediaAsset getConditionMediaAsset() {
		if(conditionMediaAsset == null){
			if(conditionMediaAssetId != null){
				conditionMediaAsset = mediaAssets.findById(MediaAsset.class, conditionMediaAssetId);
			}
		}
		return conditionMediaAsset;
	}

	@Override
	public List<Id> getProductListIds() {
		if(productListIds == null)
			productListIds = new ArrayList<>();
		return productListIds;
	}

	@Override
	public CouponPromotion setProductListIds(List<Id> productListIds) {
		this.productListIds = productListIds;
		return this;
	}

	@JsonIgnore
	@Override
	public List<ProductList> getProductLists() {
		if(productListIds != null && !productListIds.isEmpty())
			return productListRepository.findByIds(ProductList.class, productListIds.toArray(new Id[productListIds.size()]));
		return null;
	}

	@Override
	public ContextObject<Boolean> getEnabled() {
		return enabled;
	}

	@Override
	public CouponPromotion setEnabled(ContextObject<Boolean> enabled) {
		this.enabled = enabled;
		return this;
	}


}
