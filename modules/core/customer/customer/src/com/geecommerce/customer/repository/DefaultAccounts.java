package com.geecommerce.customer.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.service.api.Dao;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.persistence.mongodb.MongoDao;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Account;
import com.geecommerce.customer.model.Customer;
import com.google.inject.Inject;
import com.mongodb.QueryOperators;

@Repository
public class DefaultAccounts extends AbstractRepository implements Accounts {
    private final MongoDao mongoDao;

    @Inject
    public DefaultAccounts(MongoDao mongoDao) {
        this.mongoDao = mongoDao;
    }

    @Override
    public Dao dao() {
        return this.mongoDao;
    }

    @Override
    public Account thatBelongsTo(Customer customer) {
        if (customer == null || customer.getId() == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(Account.Column.CUSTOMER_ID, customer.getId());
        filter.put(Account.Column.ENABLED, true);

        List<Account> accounts = find(Account.class, filter);

        if (accounts != null && accounts.size() > 1) {
            throw new IllegalStateException("There is more than one account for customer " + customer + "! Therefore not returning any account.");
        }

        return accounts == null || accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public Account withExternalId(String externalIdKey, String externalIdValue) {
        if (externalIdKey == null || externalIdValue == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(new StringBuilder(Account.Column.EXTERNAL_IDENTIFIERS).append(".").append(externalIdKey).toString(), externalIdValue);
        filter.put(Account.Column.ENABLED, true);

        List<Account> accounts = find(Account.class, filter);

        if (accounts != null && accounts.size() > 1) {
            throw new IllegalStateException("There is more than one account for filter '" + filter + "'! Therefore not returning any account.");
        }

        return accounts == null || accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public List<Account> havingUsername(String username) {
        if (username == null)
            return null;

        Map<String, Object> filter = new HashMap<>();
        filter.put(Account.Column.USERNAME, username);
        filter.put(Account.Column.ENABLED, true);

        return find(Account.class, filter);
    }

    @Override
    public List<Account> havingUsername(String username, Merchant merchant) {
        if (username == null || merchant == null)
            return null;

        Map<String, Object> in = new HashMap<>();
        in.put(QueryOperators.IN, new Id[] { merchant.getId() });

        Map<String, Object> filter = new HashMap<>();
        filter.put(Account.Column.USERNAME, username);
        filter.put(Account.Column.ENABLED, true);
        filter.put(GlobalColumn.MERCHANT_ID, in);

        return find(Account.class, filter);
    }

    @Override
    public List<Account> havingUsername(String username, Store store) {
        if (username == null || store == null)
            return null;

        Map<String, Object> in = new HashMap<>();
        in.put(QueryOperators.IN, new Id[] { store.getId() });

        Map<String, Object> filter = new HashMap<>();
        filter.put(Account.Column.USERNAME, username);
        filter.put(Account.Column.ENABLED, true);
        filter.put(GlobalColumn.STORE_ID, in);

        return find(Account.class, filter);
    }

    @Override
    public List<Account> havingUsername(String username, RequestContext reqCtx) {
        if (username == null || reqCtx == null)
            return null;

        Map<String, Object> in = new HashMap<>();
        in.put(QueryOperators.IN, new Id[] { reqCtx.getId() });

        Map<String, Object> filter = new HashMap<>();
        filter.put(Account.Column.USERNAME, username);
        filter.put(Account.Column.ENABLED, true);
        filter.put(GlobalColumn.REQUEST_CONTEXT_ID, in);

        return find(Account.class, filter);
    }
}
