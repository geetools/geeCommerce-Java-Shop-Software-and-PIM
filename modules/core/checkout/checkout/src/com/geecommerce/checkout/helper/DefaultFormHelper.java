package com.geecommerce.checkout.helper;

import java.util.List;

import com.geecommerce.checkout.CheckoutConstant;
import com.geecommerce.checkout.form.AddressForm;
import com.geecommerce.checkout.form.CheckoutForm;
import com.geecommerce.checkout.model.Checkout;
import com.geecommerce.checkout.model.CheckoutAddress;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.google.inject.Inject;

@Helper
public class DefaultFormHelper implements FormHelper {
    @Inject
    protected App app;

    protected final CustomerService customerService;

    @Inject
    public DefaultFormHelper(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void fillAddresses(CheckoutForm form, Checkout checkout) {
        if (checkout.getInvoiceAddress() != null) {
            form.setInvoice(fromCheckoutAddress(checkout.getInvoiceAddress()));
            if (checkout.getDeliveryAddress() != null) {
                form.setCustomDelivery(true);
                form.setDelivery(fromCheckoutAddress(checkout.getDeliveryAddress()));
            }
        } else if (app.isCustomerLoggedIn()) {
            Customer customer = app.getLoggedInCustomer();

            form.setEmail(customer.getEmail());
            List<Address> addresses = customerService.getAddressesFor(customer);

            if (addresses != null && addresses.size() > 0) {
                for (Address address : addresses) {
                    if (address.isDefaultDeliveryAddress()) {
                        form.setDelivery(fromCustomerAddress(address));
                    }

                    if (address.isDefaultInvoiceAddress()) {
                        form.setInvoice(fromCustomerAddress(address));
                    }
                }
            }
            if (form.getDelivery() == null)
                form.setCustomDelivery(false);
            else
                form.setCustomDelivery(true);
        }
    }

    @Override
    public AddressForm fromCheckoutAddress(CheckoutAddress checkoutAddress) {
        AddressForm addressForm = new AddressForm();
        addressForm.setSalutation(checkoutAddress.getSalutation());
        addressForm.setFirstName(checkoutAddress.getFirstName());
        addressForm.setLastName(checkoutAddress.getLastName());
        addressForm.setAddress1(checkoutAddress.getAddress1());
        addressForm.setAddress2(checkoutAddress.getAddress2());
        addressForm.setHouseNumber(checkoutAddress.getHouseNumber());
        addressForm.setZip(checkoutAddress.getZip());
        addressForm.setCity(checkoutAddress.getCity());
        addressForm.setCountry(checkoutAddress.getCountry());
        addressForm.setPhone(checkoutAddress.getPhone());
        return addressForm;
    }

    @Override
    public AddressForm fromCustomerAddress(Address address) {
        AddressForm addressForm = new AddressForm();
        addressForm.setSalutation(address.getSalutation());
        addressForm.setFirstName(address.getForename());
        addressForm.setLastName(address.getSurname());
        List<String> lines = address.getAddressLines();
        if (lines != null && lines.size() >= 1)
            addressForm.setAddress1(lines.get(0));
        if (lines != null && lines.size() >= 2)
            addressForm.setAddress2(lines.get(1));
        addressForm.setHouseNumber(address.getHouseNumber());
        addressForm.setZip(address.getZip());
        addressForm.setCity(address.getCity());
        addressForm.setCountry(address.getCountry());
        addressForm.setPhone(address.getTelephone());
        return addressForm;
    }

    @Override
    public CheckoutForm getForm() {
        CheckoutForm sessForm = app.sessionGet(CheckoutConstant.SESSION_KEY_CHECKOUT_FORM);

        if (sessForm == null) {
            sessForm = new CheckoutForm();
            app.sessionSet(CheckoutConstant.SESSION_KEY_CHECKOUT_FORM, sessForm);
            return sessForm;
        } else {
            return sessForm;
        }
    }

    @Override
    public void setForm(CheckoutForm form) {
        if (form != null) {
            app.sessionSet(CheckoutConstant.SESSION_KEY_CHECKOUT_FORM, form);
        }
    }
}
