package com.geecommerce.guiwidgets.service;

import java.util.List;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.DiscountPromotion;

public interface DiscountPromotionService extends Service {

    public List<DiscountPromotion> getDiscountPromotionByKey(String key);

    public DiscountPromotion getDiscountPromotion(Id id);

}
