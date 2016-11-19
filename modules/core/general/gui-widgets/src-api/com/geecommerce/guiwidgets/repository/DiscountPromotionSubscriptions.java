package com.geecommerce.guiwidgets.repository;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.DiscountPromotionSubscription;

import java.util.List;

public interface DiscountPromotionSubscriptions extends Repository {
    public List<DiscountPromotionSubscription> subscribedOnPromotion(Id discountPromotionId);
}
