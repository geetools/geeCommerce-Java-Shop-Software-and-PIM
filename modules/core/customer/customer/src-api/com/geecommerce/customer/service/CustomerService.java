package com.geecommerce.customer.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;
import com.geecommerce.customer.model.Account;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.model.CustomerGroup;
import com.geecommerce.customer.model.Payment;

public interface CustomerService extends Service {
    /* Customer */

    Customer createCustomer(Customer customer);

    void updateCustomer(Customer customer);

    void removeCustomer(Customer customer);

    Customer getCustomer(Id customerId);

    Customer getCustomer(String email);

    CustomerGroup getCustomerGroup(Id customerGroupId);

    List<CustomerGroup> getCustomerGroups();

    List<Id> getCustomerGroupIds();

    /* Account */

    Account createAccount(Account account);

    void updateAccount(Account account);

    void removeAccount(Account account);

    void removeAccountFor(Customer customer);

    boolean accountExists(String username);

    Account getAccount(Id accountId);

    Account getAccountFor(String username);

    Account getAccountFor(Customer customer);

    /* Address */

    Address createAddress(Address address);

    void updateAddress(Address address);

    void removeAddress(Address address);

    Address getAddress(Id id);

    List<Address> getAddressesFor(Customer customer);

    Address getDefaultDeliveryAddress(Customer customer);

    Address getDefaultInvoiceAddress(Customer customer);

    void appendAddresses(Customer customer, List<Address> addresses);

    /* Payment */
    Payment createPayment(Payment payment);

    void updatePayment(Payment payment);

    void removePayment(Payment payment);

    Payment getPayment(Id id);

    List<Payment> getPaymentFor(Customer customer);

    Payment getDefaultPayment(Customer customer);

    List<Payment> getPaymentWithCode(String code, boolean defaultOnly);

    void addPayment(Customer customer, String paymentMethodCode, Map<String, String> paymentParams);

    /* Viewed Product */

    void rememberViewedProduct(Customer customer, Id productId);

    List<Id> getViewedProductIds(Customer customer);
}
