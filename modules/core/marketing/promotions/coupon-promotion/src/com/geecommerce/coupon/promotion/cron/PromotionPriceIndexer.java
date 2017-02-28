package com.geecommerce.coupon.promotion.cron;

import com.geecommerce.cart.helper.CartHelper;
import com.geecommerce.cart.model.Cart;
import com.geecommerce.cart.service.CartService;
import com.geecommerce.catalog.product.helper.ProductListHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.catalog.product.service.ProductService;
import com.geecommerce.catalog.search.ProductSearchParams;
import com.geecommerce.core.App;
import com.geecommerce.core.cron.Environment;
import com.geecommerce.core.cron.Taskable;
import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.service.annotation.Task;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.promotion.helper.CouponPromotionHelper;
import com.geecommerce.coupon.repository.CouponCodes;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.price.model.Price;
import com.geecommerce.coupon.promotion.model.CouponPromotion;
import com.geecommerce.coupon.promotion.model.ProductListPromotionIndex;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;
import com.geecommerce.coupon.promotion.repository.PromotionPriceIndexes;
import com.geecommerce.coupon.promotion.repository.PromotionProductListIndexes;
import com.geecommerce.coupon.promotion.service.CouponPromotionService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.FilterBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

@DisallowConcurrentExecution
@Task(group = "Coupon/Promotion", name = "Promotion Price Indexer", schedule = "0 0/1 * * * ?", enabled = false)
public class PromotionPriceIndexer implements Taskable, Job {

	private final CouponPromotionHelper couponPromotionHelper;

	private Logger log = null;

	@Inject
	protected App app;

	@Inject
	public PromotionPriceIndexer(CouponPromotionHelper couponPromotionHelper) {
		this.couponPromotionHelper = couponPromotionHelper;
	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		try {
			Environment.disableMessageBus();
			Environment.disableObservers();

			log = LogManager.getLogger(PromotionPriceIndexer.class);

			System.out.println("PromotionPriceIndexer:: Promotion Price Indexer started...");

			couponPromotionHelper.createPromotionIndex();

		} catch (Throwable t) {
			// According to quartz documentation, exceptions should be caught in a try-catch-block
			// wrapping the whole task. Only exceptions of type JobExecutionException may be thrown.
			// http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/TutorialLesson03#TutorialLesson3-JobExecutionException

			JobExecutionException e = new JobExecutionException(t.getMessage(), t, false);

			t.printStackTrace();

			throw e;
		} finally {
			Environment.enableMessageBus();
			Environment.enableObservers();
		}

		System.out.println("PromotionPriceIndexer:: Promotion Price Indexer ENDED...");

	}

}
