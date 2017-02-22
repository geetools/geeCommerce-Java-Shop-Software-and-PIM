package com.geecommerce.coupon.promotion.controller;


import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.price.pojo.PriceResult;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;
import com.geecommerce.coupon.promotion.repository.PromotionPriceIndexes;
import com.geecommerce.coupon.promotion.service.CouponPromotionService;
import com.google.inject.Inject;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UrlBinding("/promotion/sconto/{$event}/{id}")
public class ScontoPromotionAction  extends BaseActionBean {

	private final CouponPromotionService promotionService;

	private Id[] productIds = null;
	private static final Logger log = LogManager.getLogger(ScontoPromotionAction.class);
	private final ProductService productService;
	protected final PromotionPriceIndexes promotionPriceIndexes;

	@Inject
	public ScontoPromotionAction(CouponPromotionService promotionService, ProductService productService, PromotionPriceIndexes promotionPriceIndexes) {
		this.promotionService = promotionService;
		this.productService = productService;
		this.promotionPriceIndexes = promotionPriceIndexes;
	}

	@HandlesEvent("list-sale-data")
	public Resolution listSaleData()
	{
		Map<String, Object> salesData = new HashMap<>();

		if (productIds != null && productIds.length > 0)
		{
			for (Id productId : productIds)
			{
				Map<String, Object> saleData = getProductSaleData(productId);

				if (saleData == null)
					return jsonError("An internal error occured. Please try again later.");

				salesData.put(productId.str(), saleData);
			}
		}
		return json(salesData);
	}

	@HandlesEvent("sale-data")
	public Resolution saleData()
	{
		if (getId() == null)
			return jsonError("An internal error occured. Please try again later.");

		Map<String, Object> saleData = getProductSaleData(getId());
		if (saleData == null)
			return jsonError("An internal error occured. Please try again later.");

		return json(saleData);
	}

	private Map<String, Object> getProductSaleData(Id productId)
	{
		Product p = productService.getProduct(productId);

		if (p == null)
			return null;

		Map<String, Object> saleData = new HashMap<>();

		PriceResult priceResult = p.getPrice();

		if (priceResult != null)
		{
			saleData.put("final_price", priceResult.getFinalPrice());

			Map<String, Double> prices = priceResult.getValidPrices();
			Set<String> priceTypes = prices.keySet();

			for (String priceType : priceTypes)
			{
				saleData.put(priceType, prices.get(priceType));
			}

			ProductPromotionPriceIndex index = promotionPriceIndexes.byProduct(productId);

			if(index != null){
				saleData.put("special_price", index.getPrice());
			}

			// saleData.put("qty", p.getQty()); We may not want to show the outside world how much we actually have in
			// stock.
			saleData.put("saleable", p.isValidForSelling());
		}
		else
		{
			log.warn("Unable to find price data for product: " + productId);
		}

		return saleData;
	}


	public Id[] getProductIds() {
		return productIds;
	}

	public void setProductIds(Id[] productIds) {
		this.productIds = productIds;
	}

}
