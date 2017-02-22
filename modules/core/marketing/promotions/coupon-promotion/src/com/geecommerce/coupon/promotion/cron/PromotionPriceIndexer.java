package com.geecommerce.coupon.promotion.cron;

import com.geecommerce.calculation.model.CalculationResult;
import com.geecommerce.calculation.model.ResultKey;
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
import com.geecommerce.coupon.model.CouponData;
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
@Task(group = "Sconto/Promotion", name = "Promotion Price Indexer", schedule = "0 0/1 * * * ?", enabled = false)
public class PromotionPriceIndexer implements Taskable, Job {

	private final CouponPromotionService promotionService;
	private final ProductListService productListService;
	private final ProductService productService;
	private final ProductListHelper productListHelper;
	private final ElasticsearchHelper elasticsearchHelper;
	private final ElasticsearchService elasticsearchService;
	private final CartService cartService;
	private final CartHelper cartHelper;
	private final CouponService couponService;
	private final CouponCodes couponCodes;
	private final PromotionProductListIndexes productListIndexes;
	private final PromotionPriceIndexes promotionPriceIndexes;
	private final Products products;

	private Logger log = null;

	@Inject
	protected App app;

	@Inject
	public PromotionPriceIndexer(CouponPromotionService promotionService,
								 ProductService productService,
								 ProductListService productListService,
								 ProductListHelper productListHelper,
								 ElasticsearchHelper elasticsearchHelper, ElasticsearchService elasticsearchService, CartService cartService,
								 CartHelper cartHelper,
								 CouponService couponService,
								 CouponCodes couponCodes,
								 PromotionProductListIndexes productListIndexes,
								 PromotionPriceIndexes promotionPriceIndexes, Products products) {
		this.promotionService = promotionService;
		this.productService = productService;
		this.productListService = productListService;
		this.productListHelper = productListHelper;
		this.elasticsearchHelper = elasticsearchHelper;
		this.elasticsearchService = elasticsearchService;
		this.cartService = cartService;
		this.cartHelper = cartHelper;
		this.couponService = couponService;
		this.couponCodes = couponCodes;
		this.productListIndexes = productListIndexes;
		this.promotionPriceIndexes = promotionPriceIndexes;
		this.products = products;
	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		try {
			Environment.disableMessageBus();
			Environment.disableObservers();

			log = LogManager.getLogger(PromotionPriceIndexer.class);

			System.out.println("PromotionPriceIndexer:: Promotion Price Indexer started...");

			// create fake of customer and cart for the price calculation
			final Customer customer = app.model(Customer.class);
			customer.setId(app.nextId())
					.setCustomerNumber(app.nextIncrementId("customer_number"))
					.setForename("Price")
					.setSurname("Indexer")
					.setEmail("promo_price_index@sconto.cz");

			final Cart cart = app.model(Cart.class);
			cart.belongsTo(customer);
			cart.setEnabled(true);
			//cartService.createCart(cart);

			productListIndexes.remove(ProductListPromotionIndex.class, null);
			promotionPriceIndexes.remove(ProductPromotionPriceIndex.class, null);

			List<CouponPromotion> promotions = promotionService.getPromotions();
			for (CouponPromotion promotion : promotions) {

				if (promotion.getEnabled() == null || promotion.getEnabled().getVal() == null || !promotion.getEnabled().getVal()) {
					continue;
				}

				// check if coupon code exists for the promotion
				Coupon coupon = couponService.getCoupon(promotion.getCouponId());
				if (coupon == null || coupon.getEnabled() == null || coupon.getEnabled().getVal() == null || !coupon.getEnabled().getVal())
					continue;

				Date date = new Date();
				Date fromDate = coupon.getFromDate();
				Date toDate = coupon.getToDate();

				if (fromDate != null && fromDate.after(date) || toDate != null && toDate.before(date))
					continue;

				List<CouponCode> couponCodeList = couponCodes.thatBelongTo(coupon);
				if (couponCodeList == null && couponCodeList.isEmpty()) {
					System.out.println("PromotionPriceIndexer:: Coupon code list is empty for the promotion!");
					continue;
				} else {
					CouponCode couponCode = couponCodeList.get(0);
					cart.setCouponCode(couponCode);
				}

				List<ProductList> productLists = promotion.getProductLists();
				if (productLists != null && !productLists.isEmpty()) {
					for (ProductList productList : productLists) {


						//create product list index
						ProductListPromotionIndex plpi = app.model(ProductListPromotionIndex.class);
						plpi.setId(app.nextId());
						plpi.setProductListId(productList.getId());
						plpi.setPromotionId(promotion.getId());
						plpi.setDateFrom(coupon.getFromDate());  // TODO
						plpi.setDateTo(coupon.getToDate());
						productListIndexes.add(plpi);

						//


						Map<String, Object> queryMap = null;
						if (productList.getQuery() != null && !productList.getQuery().isEmpty()) {
							queryMap = Json.fromJson(productList.getQuery(), HashMap.class);
						}

						List<FilterBuilder> builders = productListHelper.getVisibilityFilters();
						builders.add(productListHelper.buildQuery(productList.getQueryNode()));

						ProductSearchParams searchParams = new ProductSearchParams();
						searchParams.setLimit(10000);
						searchParams.setOffset(0);

						SearchResult productListResult = elasticsearchService.findItems(Product.class, builders, null, null, null, searchParams);

						if (productListResult != null && productListResult.getDocumentIds() != null && productListResult.getDocumentIds().size() > 0) {
							Id[] productIds = elasticsearchHelper.toIds(productListResult.getDocumentIds().toArray());
							List<Product> products = productService.getProducts(productIds);
							System.out.println(productList.getLabel().str());
							// add every product to the cart so that to calculate product promotion price

							for (Product product : products) {

								System.out.println(product.getArticleNumber());


								if (!product.isProgramme() && !product.isVariantMaster() && product != null) {
									createPromotionPriceIndex(cart, product, promotion, coupon, product.getId());
								} else if (product.isVariantMaster()) {
									Price lowestPrice = product.getPrice().getLowestFinalPriceFor();
									Product lovestPriceProduct = productService.getProduct(lowestPrice.getProductId());
									createPromotionPriceIndex(cart, lovestPriceProduct, promotion, coupon, product.getId());

									for (Product childProduct : product.getVariants()) {
										if (childProduct.isSalable() && childProduct.getPrice() != null && childProduct.getPrice().getFinalPrice() != null) {
											createPromotionPriceIndex(cart, childProduct, promotion, coupon, childProduct.getId());
										}
									}
								} else if (product.isProgramme() && product.getValidProgrammeProducts() != null) {
									for (Product childProduct : product.getValidProgrammeProducts()) {
										if (childProduct.isSalable() && childProduct.getPrice() != null && childProduct.getPrice().getFinalPrice() != null) {
											createPromotionPriceIndex(cart, childProduct, promotion, coupon, childProduct.getId());
										}
									}
								}
							}
						}
					}
				}
			}
			List<CouponCode> couponCodes = couponService.getAutoCoupons();
			for (CouponCode couponCode : couponCodes) {

				Coupon coupon = couponCode.getCoupon();
				if (coupon == null || coupon.getEnabled() == null || coupon.getEnabled().getVal() == null || !coupon.getEnabled().getVal())
					continue;


			//	if(coupon.getShowPriceInProduct() == null || !coupon.getShowPriceInProduct().getBoolean())
			//		continue;

				Date date = new Date();
				Date fromDate = coupon.getFromDate();
				Date toDate = coupon.getToDate();

				if (fromDate != null && fromDate.after(date) || toDate != null && toDate.before(date))
					continue;

				cart.setCouponCode(couponCode);

				System.out.println("PromotionPriceIndexer:: Coupon  index...");

				System.out.println(coupon.getName().str());

				List<Id> productIds = products.allIdsForContext();

				long start = System.currentTimeMillis();
				long start1000 = System.currentTimeMillis();

				if (productIds != null && !productIds.isEmpty()) {

					int totalCount = 0;


					for (Id id : productIds) {
						try {

							Product product = products.findById(Product.class, id);

							if (!product.isProgramme() && !product.isVariantMaster() && product != null) {
								createPromotionPriceIndex(cart, product, null, coupon, product.getId());
							} else if (product.isVariantMaster()) {
								Price lowestPrice = product.getPrice().getLowestFinalPriceFor();
								if(lowestPrice != null) {
									Product lovestPriceProduct = productService.getProduct(lowestPrice.getProductId());
									createPromotionPriceIndex(cart, lovestPriceProduct, null, coupon, product.getId());

									for (Product childProduct : product.getVariants()) {
										if (childProduct.isSalable() && childProduct.getPrice() != null && childProduct.getPrice().getFinalPrice() != null) {
											createPromotionPriceIndex(cart, childProduct, null, coupon, childProduct.getId());
										}
									}
								}
							} else if (product.isProgramme() && product.getValidProgrammeProducts() != null) {
								for (Product childProduct : product.getValidProgrammeProducts()) {
									if (childProduct.isSalable() && childProduct.getPrice() != null && childProduct.getPrice().getFinalPrice() != null) {
										createPromotionPriceIndex(cart, childProduct, null, coupon, childProduct.getId());
									}
								}
							}

							if ((totalCount % 1000) == 0) {
								System.out.println("Total processed: " + totalCount + ", 1000 products took: " + (System.currentTimeMillis() - start1000) + "ms.");
								start1000 = System.currentTimeMillis();

							}

							totalCount++;

						} catch (Exception e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					}
				}

			}
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

	void createPromotionPriceIndex(Cart cart, Product product, CouponPromotion promotion, Coupon coupon, Id productId){
		try {
			if(!product.isValidForSelling())
				return;

			cart.getCartItems().clear();
			cart.addProduct(product);

			if(!couponService.isCouponApplicableToCart(cart.getCouponCode(), ((CouponData) cart).toCartAttributeCollection(), true))
				return;

			CalculationResult cartTotals = cart.getTotals();
			if(cartTotals.getDouble(ResultKey.GROSS_DISCOUNT_TOTAL) != null && cartTotals.getDouble(ResultKey.GROSS_DISCOUNT_TOTAL) > 0) {
				final Double price = cartTotals.getDouble(ResultKey.GROSS_GRAND_TOTAL);// - cartTotals.getDouble(ResultKey.GROSS_DISCOUNT_TOTAL);

				if(cartTotals.getDouble(ResultKey.GROSS_DISCOUNT_TOTAL) == 0)
					return;
				// clear everything after

				ProductPromotionPriceIndex ppiExisting = promotionPriceIndexes.byProduct(productId);

				if (ppiExisting == null || ppiExisting.getPrice() > price) {

					if (ppiExisting != null) {
						promotionPriceIndexes.remove(ppiExisting);
					}

					// update price index
					ProductPromotionPriceIndex ppi = app.model(ProductPromotionPriceIndex.class);
					ppi.setId(app.nextId());
					ppi.setProductId(productId);
					ppi.setPrice(price);
					if(promotion != null)
						ppi.setPromotionId(promotion.getId());
					ppi.setDateFrom(coupon.getFromDate());  // TODO
					ppi.setDateTo(coupon.getToDate());
					promotionService.createPromotionPriceIndex(ppi);
				}
			}
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			cart.getCartItems().clear();
		}
	}
}
