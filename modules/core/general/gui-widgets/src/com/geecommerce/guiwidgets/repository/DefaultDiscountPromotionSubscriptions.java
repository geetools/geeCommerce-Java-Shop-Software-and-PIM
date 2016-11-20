package com.geecommerce.guiwidgets.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.DiscountPromotionSubscription;

@Repository
public class DefaultDiscountPromotionSubscriptions extends AbstractRepository
    implements DiscountPromotionSubscriptions {
    @Override
    public List<DiscountPromotionSubscription> subscribedOnPromotion(Id discountPromotionId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(DiscountPromotionSubscription.Col.DISCOUNT_PROMOTION_ID, discountPromotionId);
        return find(DiscountPromotionSubscription.class, filter);
    }
}
