package com.geecommerce.customer.repository;

import java.util.List;

import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.customer.model.Account;
import com.geecommerce.customer.model.Customer;

public interface Accounts extends Repository {
    public Account thatBelongsTo(Customer customer);

    public Account withExternalId(String externalIdKey, String externalIdValue);

    public List<Account> havingUsername(String username);

    public List<Account> havingUsername(String username, Merchant merchant);

    public List<Account> havingUsername(String username, Store store);

    public List<Account> havingUsername(String username, RequestContext reqCtx);
}
