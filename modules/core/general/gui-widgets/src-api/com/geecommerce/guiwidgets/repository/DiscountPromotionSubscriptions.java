package com.geecommerce.guiwidgets.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.DiscountPromotionSubscription;

public interface DiscountPromotionSubscriptions extends Repository {
    public List<DiscountPromotionSubscription> subscribedOnPromotion(Id discountPromotionId);
}
