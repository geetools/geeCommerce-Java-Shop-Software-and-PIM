package com.geecommerce.catalog.product.repository;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;

public interface Products extends Repository {
    public List<Product> thatBelongTo(Merchant merchant);

    public List<Product> thatBelongTo(Store store);

    public List<Product> enabledForContext();

    public Product havingId2(Id id2);

    public Product havingArticleNumber(String articleNumber);

    public List<Id> allIdsForContext();

    public List<Id> enabledIdsForContext();

    public List<Id> noneDeletedIdsForContext();

    public List<Product> havingProgrammeChildProduct(Product childProduct);

    public Map<String, Id> allArticleNumbers();

    public Map<Id, String> idArticleNumberMap();
}
