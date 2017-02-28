package com.geecommerce.coupon.promotion.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.coupon.promotion.model.ProductPromotionPriceIndex;

public interface PromotionPriceIndexes extends Repository {
    ProductPromotionPriceIndex byProduct(Id productId);
}
