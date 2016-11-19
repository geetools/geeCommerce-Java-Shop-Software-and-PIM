package com.geecommerce.guiwidgets.service;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.DiscountPromotion;
import com.geecommerce.guiwidgets.model.ProductPromotion;

import java.util.List;

public interface DiscountPromotionService extends Service {

    public List<DiscountPromotion> getDiscountPromotionByKey(String key);

    public DiscountPromotion getDiscountPromotion(Id id);

}
