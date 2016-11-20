package com.geecommerce.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.Scope;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.ProductIds;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.customer.CustomerConstant;
import com.geecommerce.customer.configuration.Key;
import com.geecommerce.customer.model.Account;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.CustomerGroup;
import com.geecommerce.customer.model.Payment;
import com.geecommerce.customer.model.ViewedProduct;
import com.geecommerce.customer.repository.Accounts;
import com.geecommerce.customer.repository.Addresses;
import com.geecommerce.customer.repository.Customers;
import com.geecommerce.customer.repository.Payments;
import com.geecommerce.customer.repository.ViewedProducts;
import com.google.inject.Inject;

@Service
public class DefaultCustomerService implements CustomerService {
    @Inject
    protected App app;

    protected final Customers customers;
    protected final Accounts accounts;
    protected final Addresses addresses;
    protected final ViewedProducts viewedProducts;
    protected final Payments payments;

    private static final String ACCOUNT_SCOPE_KEY = "customer/account/scope";

    @Inject
    public DefaultCustomerService(Customers customers, Accounts accounts, Addresses addresses,
        ViewedProducts viewedProducts, Payments payments) {
        this.customers = customers;
        this.accounts = accounts;
        this.addresses = addresses;
        this.viewedProducts = viewedProducts;
        this.payments = payments;
    }

    /* Customer */

    @Override
    public Customer createCustomer(Customer customer) {
        if (customer == null)
            throw new NullPointerException("Customer cannot be null");

        ApplicationContext appCtx = app.context();

        // Add merchant if it has not yet been set.
        if (!customer.isIn(appCtx.getMerchant())) {
            customer.addTo(appCtx.getMerchant());
        }

        // Add store if it has not yet been set.
        if (!customer.isIn(appCtx.getStore())) {
            customer.addTo(appCtx.getStore());
        }

        // Add request context if it has not yet been set.
        if (!customer.isIn(appCtx.getRequestContext())) {
            customer.addTo(appCtx.getRequestContext());
        }

        List<Id> customerGroupIds = customer.getCustomerGroupIds();

        // Add default customer-group if it has not yet been set.
        if (customerGroupIds == null || customerGroupIds.isEmpty()) {
            Long customerGroupId = app.cpLong_(CustomerConstant.CUSTOMER_GROUP_DEFAULT_NEW_CUSTOMER_CONFIG_KEY);

            if (customerGroupId == null) {
                throw new IllegalStateException(
                    "Unable to create customer because no default customer-group has been configured. Please add a value for the key '"
                        + CustomerConstant.CUSTOMER_GROUP_DEFAULT_NEW_CUSTOMER_CONFIG_KEY
                        + "' to the configuration collection.");
            }

            CustomerGroup customerGroup = getCustomerGroup(Id.valueOf(customerGroupId));

            if (customerGroup == null) {
                throw new IllegalStateException("Unable to create customer because customer-group '" + customerGroupId
                    + "' could not be found. Please check your configuration property '"
                    + CustomerConstant.CUSTOMER_GROUP_DEFAULT_NEW_CUSTOMER_CONFIG_KEY + "'.");
            }

            customer.addCustomerGroup(customerGroup);
        }

        return customers.add(customer);
    }

    @Override
    public void updateCustomer(Customer customer) {
        if (customer == null || customer.getId() == null)
            return;

        customers.update(customer);
    }

    @Override
    public void removeCustomer(Customer customer) {
        if (customer == null || customer.getId() == null)
            return;

        customers.remove(customer);
    }

    @Override
    public Customer getCustomer(Id customerId) {
        if (customerId == null)
            return null;

        return customers.findById(Customer.class, customerId);
    }

    @Override
    public Customer getCustomer(String email) {
        return customers.withEmail(email);
    }

    @Override
    public CustomerGroup getCustomerGroup(Id customerGroupId) {
        if (customerGroupId == null)
            return null;

        return customers.findById(CustomerGroup.class, customerGroupId);
    }

    @Override
    public List<CustomerGroup> getCustomerGroups() {
        List<CustomerGroup> groups = new ArrayList<>();
        for (Id id : getCustomerGroupIds()) {
            CustomerGroup group = getCustomerGroup(id);
            if (group != null) {
                groups.add(group);
            }
        }

        if (groups.size() > 0)
            return groups;
        return null;
    }

    @Override
    public List<Id> getCustomerGroupIds() {

        List<Id> groups = new ArrayList<>();
        Customer customer = app.getLoggedInCustomer();
        if (customer != null) {
            groups = getCustomerGroupIds();
        } else {
            String idStr = app.cpStr_(Key.DEFAULT_CUSTOMER_GROUP);
            if (idStr != null) {
                Id id = Id.parseId(idStr);
                groups.add(id);
            }
        }
        if (groups != null && groups.size() > 0)
            return groups;
        return null;

    }

    /* Account */

    @Override
    public Account createAccount(Account account) {
        if (account == null)
            return null;

        ApplicationContext appCtx = app.context();

        // Add merchant if it has not yet been set.
        if (!account.isIn(appCtx.getMerchant())) {
            account.addTo(appCtx.getMerchant());
        }

        // Add store if it has not yet been set.
        if (!account.isIn(appCtx.getStore())) {
            account.addTo(appCtx.getStore());
        }

        // Add request context if it has not yet been set.
        if (!account.isIn(appCtx.getRequestContext())) {
            account.addTo(appCtx.getRequestContext());
        }

        return accounts.add(account);
    }

    @Override
    public void updateAccount(Account account) {
        if (account == null || account.getId() == null)
            return;

        accounts.update(account);
    }

    @Override
    public void removeAccount(Account account) {
        if (account == null || account.getId() == null)
            return;

        accounts.remove(account);
    }

    @Override
    public void removeAccountFor(Customer customer) {
        if (customer == null || customer.getId() == null)
            return;

        Account account = accounts.thatBelongsTo(customer);

        if (account != null && account.getId() != null) {
            accounts.remove(account);
        }
    }

    @Override
    public Account getAccount(Id accountId) {
        return accounts.findById(Account.class, accountId);
    }

    @Override
    public Account getAccountFor(Customer customer) {
        if (customer == null || customer.getId() == null)
            return null;

        return accounts.thatBelongsTo(customer);
    }

    @Override
    public Account getAccountFor(String username) {
        if (username == null)
            return null;

        ApplicationContext appCtx = app.context();

        Scope accountScope = app.cpEnum_(ACCOUNT_SCOPE_KEY, Scope.class, Scope.STORE);

        List<Account> customerAccounts = null;

        switch (accountScope) {
        case GLOBAL:
            customerAccounts = accounts.havingUsername(username);
            break;
        case MERCHANT:
            customerAccounts = accounts.havingUsername(username, appCtx.getMerchant());
            break;
        case STORE:
            customerAccounts = accounts.havingUsername(username, appCtx.getStore());
            break;
        case REQUEST_CONTEXT:
            customerAccounts = accounts.havingUsername(username, appCtx.getRequestContext());
            break;
        }

        if (customerAccounts != null && customerAccounts.size() > 1) {
            throw new IllegalStateException("There is more than one account for username '" + username + "' in scope '"
                + accountScope + "'! Therefore not returning any account.");
        }

        return customerAccounts == null || customerAccounts.isEmpty() ? null : customerAccounts.get(0);
    }

    @Override
    public boolean accountExists(String username) {
        return getAccountFor(username) == null ? false : true;
    }

    /* Address */

    @Override
    public Address createAddress(Address address) {
        if (address == null)
            return null;

        return addresses.add(address);
    }

    @Override
    public void updateAddress(Address address) {
        if (address == null || address.getId() == null)
            return;

        addresses.update(address);
    }

    @Override
    public void removeAddress(Address address) {
        if (address == null || address.getId() == null)
            return;

        addresses.remove(address);
    }

    @Override
    public Address getAddress(Id id) {
        if (id == null)
            return null;

        return addresses.findById(Address.class, id);
    }

    @Override
    public List<Address> getAddressesFor(Customer customer) {
        if (customer == null || customer.getId() == null)
            return null;

        return addresses.thatBelongTo(customer);
    }

    @Override
    public Address getDefaultDeliveryAddress(Customer customer) {
        List<Address> addresses = getAddressesFor(customer);

        if (addresses != null && addresses.size() > 0) {
            for (Address address : addresses) {
                if (address.isDefaultDeliveryAddress()) {
                    return address;
                }
            }
        }
        return null;
    }

    @Override
    public Address getDefaultInvoiceAddress(Customer customer) {
        List<Address> addresses = getAddressesFor(customer);

        if (addresses != null && addresses.size() > 0) {
            for (Address address : addresses) {
                if (address.isDefaultInvoiceAddress()) {
                    return address;
                }
            }
        }
        return null;
    }

    /**
     * Append addresses to customer if they are different from existent
     */

    @Override
    public void appendAddresses(Customer customer, List<Address> addressList) {
        if (customer == null || customer.getId() == null || addressList == null || addressList.size() == 0)
            return;

        List<Address> customerAddresses = addresses.thatBelongTo(customer);

        Address defaultInvoiceAddress = getDefaultInvoiceAddress(customer);
        Address defaultDeliveryAddress = getDefaultDeliveryAddress(customer);

        boolean addressListHasCustomDeliveryAddress = addressList.size() > 1;

        int counter = 0;
        for (Address address : addressList) {
            boolean hasEqual = false;

            for (Address customerAddress : customerAddresses) {
                hasEqual = hasEqual || addressesAreEqual(address, customerAddress);
            }

            if (!hasEqual) {
                // add address to customer
                address.belongsTo(customer);

                // First entry is invoice address
                if (defaultInvoiceAddress == null && counter == 0) {
                    address.markAsDefaultInvoiceAddress();
                }

                if (defaultDeliveryAddress == null && !addressListHasCustomDeliveryAddress && counter == 0) {
                    address.markAsDefaultDeliveryAddress();
                } else if (defaultDeliveryAddress == null && addressListHasCustomDeliveryAddress && counter == 1) {
                    address.markAsDefaultDeliveryAddress();
                }

                createAddress(address);
            }

            counter++;
        }
    }

    @Override
    public Payment createPayment(Payment payment) {
        if (payment == null)
            return null;

        return payments.add(payment);
    }

    @Override
    public void updatePayment(Payment payment) {
        if (payment == null || payment.getId() == null)
            return;

        payments.update(payment);
    }

    @Override
    public void removePayment(Payment payment) {
        if (payment == null || payment.getId() == null)
            return;

        payments.remove(payment);
    }

    @Override
    public Payment getPayment(Id id) {
        if (id == null)
            return null;

        return payments.findById(Payment.class, id);
    }

    @Override
    public List<Payment> getPaymentFor(Customer customer) {
        if (customer == null || customer.getId() == null)
            return null;

        return payments.thatBelongTo(customer);
    }

    @Override
    public Payment getDefaultPayment(Customer customer) {
        List<Payment> payments = getPaymentFor(customer);

        if (payments != null && payments.size() > 0) {
            for (Payment payment : payments) {
                if (payment.isDefaultPayment())
                    return payment;
            }
        }
        return null;
    }

    @Override
    public List<Payment> getPaymentWithCode(String code, boolean defaultOnly) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(Payment.Column.PAYMENT_CODE, code);
        if (defaultOnly) {
            filter.put(Payment.Column.DEFAULT_PAYMENT, true);
        }
        return payments.find(Payment.class, filter, QueryOptions.builder().sortBy(GlobalColumn.ID).build());
    }

    @Override
    public void addPayment(Customer customer, String paymentMethodCode, Map<String, String> paymentParams) {
        List<Payment> payments = getPaymentWithCode(paymentMethodCode, true);
        if (payments != null && !payments.isEmpty()) {
            Payment payment = payments.get(0);
            payment.setDefaultPayment(true).setParameters(paymentParams);
            updatePayment(payment);
        } else {
            Payment payment = app.model(Payment.class);
            payment.setId(app.nextId()).setDefaultPayment(true).setPaymentCode(paymentMethodCode)
                .setParameters(paymentParams).belongsTo(customer);
            createPayment(payment);
        }
    }

    /* Viewed Product */
    @Override
    public void rememberViewedProduct(Customer customer, Id productId) {
        if (customer == null || productId == null)
            return;

        ViewedProduct vp = app.model(ViewedProduct.class).viewedBy(customer).viewedProduct(productId)
            .viewedOn(DateTimes.newDate());

        viewedProducts.add(vp);
    }

    @Override
    public List<Id> getViewedProductIds(Customer customer) {
        List<Id> productIds = new ArrayList<>();

        if (customer == null)
            return productIds;

        // TODO add limit to config table
        List<ViewedProduct> viewedProductsList = viewedProducts.thatBelongTo(customer, 10);

        return viewedProductsList == null || viewedProductsList.size() == 0 ? productIds
            : ProductIds.toIdList(viewedProductsList);
    }

    private boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null)
            return true;
        if (str1 == null || str2 == null)
            return false;
        if (str1.equals(str2))
            return true;
        return false;
    }

    private boolean equals(List<String> lst1, List<String> lst2) {
        if (lst1 == null && lst2 == null)
            return true;
        if (lst1 == null || lst2 == null)
            return true;
        if (lst1.size() != lst2.size())
            return false;

        for (int i = 0; i < lst1.size(); i++) {
            if (!equals(lst1.get(i), lst2.get(i)))
                return false;
        }

        return true;
    }

    private boolean addressesAreEqual(Address address1, Address address2) {

        if (Str.trimNormalizedEqualsIgnoreCase(address1.getForename(), address2.getForename())
            && Str.trimNormalizedEqualsIgnoreCase(address1.getSurname(), address2.getSurname())
            && Str.trimNormalizedEqualsIgnoreCase(address1.getHouseNumber(), address2.getHouseNumber())
            && Str.trimNormalizedEqualsIgnoreCase(formatPostcode(address1.getZip(), address1.getCountry()),
                formatPostcode(address2.getZip(), address2.getCountry()))
            && Str.trimNormalizedEqualsIgnoreCase(address1.getCity(), address2.getCity())
            && Str.trimNormalizedEqualsIgnoreCase(address1.getCountry(), address2.getCountry())) {
            List<String> addressLines1 = address1.getAddressLines();
            List<String> addressLines2 = address2.getAddressLines();

            boolean addressLinesAreEqual = true;

            for (String addrLine1 : addressLines1) {

                if (StringUtils.isBlank(addrLine1))
                    break;

                boolean foundLine = false;
                for (String addrLine2 : addressLines2) {
                    if (Str.trimNormalizedEqualsIgnoreCase(addrLine1, addrLine2)) {
                        foundLine = true;
                        break;
                    }
                }

                if (!foundLine) {
                    addressLinesAreEqual = false;
                    break;
                }
            }

            if (addressLinesAreEqual)
                return true;
        }

        return false;

        // if (equals(address1.getForename(), address2.getForename())
        // && equals(address1.getSurname(), address2.getSurname())
        // && equals(address1.getCity(), address2.getCity())
        // && equals(address1.getCountry(), address2.getCountry())
        // && equals(address1.getZip(), address2.getZip())
        // && equals(address1.getAddressLines(), address2.getAddressLines()))
        // return true;
        // return false;
    }

    // TODO: move to helper.
    private static final Pattern patternNumberOnly = Pattern.compile("^[0-9 ]+$");

    private String formatPostcode(String zip, String country) {
        if (country != null && "CZ".equals(country)) {
            Matcher m = patternNumberOnly.matcher(zip.trim());

            if (m.matches()) {
                String newZIP = zip.trim().replace(Str.SPACE, Str.EMPTY);

                if (newZIP.length() == 5) {
                    newZIP = newZIP.substring(0, 3) + " " + newZIP.substring(3);
                    return newZIP;
                }
            }
        }

        return zip;
    }
}