package com.geecommerce.customer.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.authentication.Passwords;
import com.geecommerce.core.config.MerchantConfig;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.core.util.Requests;
import com.geecommerce.core.web.BaseController;
import com.geecommerce.customer.configuration.Key;
import com.geecommerce.customer.form.AccountForm;
import com.geecommerce.customer.model.Account;
import com.geecommerce.customer.model.Address;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.geecommerce.mailer.service.MailerService;
import com.geemvc.Bindings;
import com.geemvc.HttpMethod;
import com.geemvc.RequestContext;
import com.geemvc.Results;
import com.geemvc.annotation.Controller;
import com.geemvc.annotation.Request;
import com.geemvc.bind.param.annotation.Param;
import com.geemvc.i18n.message.CompositeMessageResolver;
import com.geemvc.intercept.OnView;
import com.geemvc.intercept.annotation.PreView;
import com.geemvc.validation.Errors;
import com.geemvc.validation.annotation.Required;
import com.geemvc.view.bean.Result;
import com.google.inject.Inject;

@Controller
@Request("/customer/account")
public class AccountController extends BaseController {
    @Inject
    protected App app;

    protected final CustomerService customerService;
    protected final MailerService mailerService;
    protected final CompositeMessageResolver messageResolver;

    protected static final String LOGGED_IN_MESSAGE = "customer:logged-in";

    protected static final Logger LOG = LogManager.getLogger(AccountController.class);

    public static final String CUSTOMER_REGISTRATION_COMPLETE = "customer:registration:complete";
    public static final String CUSTOMER_FORGOTTEN_PASSWORD_TOKEN = "customer:forgotten-password:token";
    public static final String CUSTOMER_FORGOTTEN_PASSWORD_SAVE = "customer:forgotten-password:save";

    @Inject
    public AccountController(CustomerService customerService, MailerService mailerService, CompositeMessageResolver messageResolver) {
        this.customerService = customerService;
        this.mailerService = mailerService;
        this.messageResolver = messageResolver;
    }
    //
    // @ValidationMethod(on = "createAccount", when = ValidationState.ALWAYS)
    // public void fixUsernameError(ValidationErrors errors) {
    // if (getUseEmail()) {
    // errors.remove("username");
    // }
    // }

    @Request("/new")
    public Result newAccount() {
        if (isCustomerLoggedIn())
            return redirect("/customer/account/overview/");

        return view("customer/account/new_form");
    }

    @Request("/login")
    public Result login() {
        if (isCustomerLoggedIn())
            return redirect("/customer/account/overview/");

        return view("customer/account/login_form")
            .bind("customerLoggedIn", isCustomerLoggedIn())
            .bind("loggedInCustomer", getLoggedInCustomer());
    }

    @Request(value = "/process-login", method = HttpMethod.POST)
    public Result processLogin(@Valid AccountForm accountForm) {

        Account account = customerService.getAccountFor(accountForm.getUsername());

        if (account != null) {
            try {
                if (Passwords.authenticate(accountForm.getPassword(), account.getPassword(), getSalt(account.getSalt()))) {
                    Customer customer = customerService.getCustomer(account.getCustomerId());

                    if (customer != null) {
                        account.setLastLoggedIn(DateTimes.newDate());
                        customerService.updateAccount(account);

                        // Prevent session hijacking.
                        renewSession();

                        setLoggedInCustomer(customer);
                        app.publish(LOGGED_IN_MESSAGE, "customer", customer);

                    } else {
                        return view("customer/account/login_form");
                    }
                } else {
                    return view("customer/account/login_form");
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                LOG.throwing(e);
                return view("customer/account/login_form");
            }
        } else {
            return view("customer/account/login_form");
        }

        if (accountForm.getPostLoginRedirect() != null && Requests.isRelativeURL(accountForm.getPostLoginRedirect())) {
            return redirect(accountForm.getPostLoginRedirect());
        } else {
            return redirect("/customer/account/overview/");
        }
    }

    @Request(value = "/add", method = HttpMethod.POST)
    public Result createAccount(@Valid AccountForm accountForm, Bindings bindings, Errors errors, RequestContext requestContext) {

        if (bindings.hasErrors())
            return Results.view("customer/account/new_form")
                .bind(bindings.typedValues());

        Customer savedCustomer = null;
        Account savedAccount = null;
        Address savedInvoiceAddress = null;
        Address savedShippingAddress = null;

        try {
            if (getUseEmail())
                accountForm.setUsername(accountForm.getEmail());

            if (!customerService.accountExists(accountForm.getUsername())) {
                Customer customer = app.getModel(Customer.class);
                customer.setId(app.nextId());
                customer.setCustomerNumber(app.nextIncrementId("customer_number"));
                customer.setForename(accountForm.getForename());
                customer.setSurname(accountForm.getSurname());
                customer.setEmail(accountForm.getEmail());
                customer.setSalutation(accountForm.getSalutation());
                customer.setDegree(accountForm.getTitle());
                customer.setPhone(accountForm.getPhone());
                customer.setPhoneCode(accountForm.getPhoneCode());
                customer.setCompany(accountForm.getInvoiceAddrFirm());
                customer.setCompanyTaxIdNumber(accountForm.getInvoiceAddrUst());
                customer.setNewsletterEnabled(accountForm.isEmailNotification());

                savedCustomer = customerService.createCustomer(customer);

                if (savedCustomer != null && savedCustomer.getId() != null) {

                    Address invoiceAddress = app.getModel(Address.class);
                    invoiceAddress.belongsTo(customer).setCompany(accountForm.getInvoiceAddrFirm())
                        .setAddressLines(accountForm.getInvoiceAddrStreet())
                        .setHouseNumber(accountForm.getInvoiceAddrHouseNum())
                        .setZip(accountForm.getInvoiceAddrZipCode())
                        .setCity(accountForm.getInvoiceAddrCity())
                        .markAsDefaultInvoiceAddress();
                    savedInvoiceAddress = customerService.createAddress(invoiceAddress);

                    Address shippingAddress = app.getModel(Address.class);
                    shippingAddress.belongsTo(customer).setAddressLines(accountForm.getShippingAddrStreet())
                        .setHouseNumber(accountForm.getShippingAddrHouseNum())
                        .setZip(accountForm.getShippingAddrZipCode())
                        .setCity(accountForm.getShippingAddrCity())
                        .markAsDefaultDeliveryAddress();
                    savedShippingAddress = customerService.createAddress(shippingAddress);

                    byte[] randomSalt = Passwords.getRandomSalt();

                    Account account = app.getModel(Account.class);
                    account.belongsTo(savedCustomer).setUsername(accountForm.getUsername())
                        .setPassword(encryptPassword(accountForm.getPassword1(), randomSalt))
                        .setSalt(randomSalt).enableAccount();

                    savedAccount = customerService.createAccount(account);

                    setLoggedInCustomer(savedCustomer);

                    // app.publish(CUSTOMER_REGISTRATION_COMPLETE, "customerId",
                    // customer.getId());
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Username '" + accountForm.getUsername() + "' already exists.");
                }

                errors.add("account.error.usernameExist");
                return view("customer/account/new_form");
            }
        } catch (Throwable t) {
            t.printStackTrace();

            LOG.error("An error occured when trying to create a new account: savedCustomer=" + savedCustomer + ", savedAccount=" + savedAccount);

            LOG.throwing(t);

            errors.add("account.error.create");

            if (savedCustomer != null && savedCustomer.getId() != null) {
                try {
                    // If something goes wrong, we attempt to remove the
                    // customer and account again, to keep data clean.
                    customerService.removeCustomer(savedCustomer);

                    customerService.removeAccountFor(savedCustomer);

                    customerService.removeAddress(savedInvoiceAddress);

                    customerService.removeAddress(savedShippingAddress);
                } catch (Throwable t2) {
                    LOG.throwing(t2);
                }
            }

            return view("customer/account/new_form");
        }

        return view("customer/account/success");
    }

    @Request("/edit")
    public Result edit() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        AccountForm form = new AccountForm();
        populateForm(form);
        return view("customer/account/edit_form")
            .bind("accountForm", form);
    }

    @Request(value = "/process-edit", method = HttpMethod.POST)
    public Result processEdit(@Valid AccountForm accountForm, Bindings bindings, Errors errors) {
        Customer loggedInCustomer = null;
        Account account = null;

        if (bindings.hasErrors())
            return Results.view("customer/account/edit_form")
                .bind(bindings.typedValues());

        if (accountForm.getPassword1() != null && !accountForm.getPassword1().equals(accountForm.getPassword2())) {
            errors.add("account.error.passwordNotEqual");
            return view("customer/account/edit_form");
        }

        try {
            loggedInCustomer = getLoggedInCustomer();
            account = customerService.getAccountFor(loggedInCustomer);

            loggedInCustomer.setForename(accountForm.getForename())
                .setSurname(accountForm.getSurname())
                .setSalutation(accountForm.getSalutation())
                .setDegree(accountForm.getTitle())
                .setPhone(accountForm.getPhone())
                .setPhoneCode(accountForm.getPhoneCode())
                .setCustomerNumber(accountForm.getCustomerNumber());

            loggedInCustomer.setCompany(accountForm.getInvoiceAddrFirm());
            loggedInCustomer.setCompanyTaxIdNumber(accountForm.getInvoiceAddrUst());
            loggedInCustomer.setNewsletterEnabled(accountForm.isEmailNotification());

            List<Address> addresses = customerService.getAddressesFor(loggedInCustomer);
            if (addresses != null && !addresses.isEmpty()) {
                for (Address address : addresses) {
                    if (address.isDefaultInvoiceAddress()) {
                        address.belongsTo(loggedInCustomer)
                            .setCompany(accountForm.getInvoiceAddrFirm())
                            .setAddressLines(accountForm.getInvoiceAddrStreet())
                            .setHouseNumber(accountForm.getInvoiceAddrHouseNum())
                            .setZip(accountForm.getInvoiceAddrZipCode())
                            .setCity(accountForm.getInvoiceAddrCity());
                        customerService.updateAddress(address);
                    } else if (address.isDefaultDeliveryAddress()) {
                        address.belongsTo(loggedInCustomer)
                            .setAddressLines(accountForm.getShippingAddrStreet())
                            .setHouseNumber(accountForm.getShippingAddrHouseNum())
                            .setZip(accountForm.getShippingAddrZipCode())
                            .setCity(accountForm.getShippingAddrCity());
                        customerService.updateAddress(address);
                    }
                }
            }

            if (getUseEmail())
                accountForm.setUsername(accountForm.getEmail());

            byte[] randomSalt = Passwords.getRandomSalt();

            account.belongsTo(loggedInCustomer).setUsername(accountForm.getUsername()).enableAccount();
            if (accountForm.getPassword1() != null && accountForm.getPassword1().equals(accountForm.getPassword2())) {
                if (StringUtils.isNotBlank(accountForm.getPassword1()) && StringUtils.isNotBlank(accountForm.getPassword2())) {
                    account.setPassword(encryptPassword(accountForm.getPassword1(), randomSalt));
                    account.setSalt(randomSalt);
                }
            }

            customerService.updateAccount(account);
            customerService.updateCustomer(loggedInCustomer);

        } catch (Throwable t) {
            t.printStackTrace();

            LOG.error("An error occured when trying to update an account: customer=" + loggedInCustomer + ", account=" + account);
            LOG.throwing(t);

            errors.add("account.error.updateError");

            return view("customer/account/edit");
        }

        return redirect("/customer/account/overview");
    }

    @Request("/logout")
    public Result logout() {
        sessionInvalidate();
        return redirect("/");
    }

    @Request("/overview")
    public Result overview() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        AccountForm form = new AccountForm();
        populateForm(form);

        return view("customer/account/overview").bind("accountForm", form);
    }

    @Request("orders-overview")
    public Result ordersOverview(@Param("orderFilterDate") String orderFilterDate) {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        AccountForm form = new AccountForm();
        populateForm(form);

        return view("customer/account/orders_overview")
            .bind("accountForm", form)
            .bind("orderFilterDate", orderFilterDate);
    }

    @Request("order-details")
    public Result orderDetails() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        AccountForm form = new AccountForm();
        populateForm(form);

        return view("customer/account/order_details").bind("accountForm", form);
    }

    protected void populateForm(AccountForm accountForm) {
        if (accountForm == null)
            return;

        Account account = customerService.getAccountFor((Customer) getLoggedInCustomer());
        if (account != null) {
            Customer customer = customerService.getCustomer(account.getCustomerId());
            accountForm.setForename(customer.getForename());
            accountForm.setSurname(customer.getSurname());
            accountForm.setCustomerNumber(customer.getCustomerNumber());
            accountForm.setTitle(customer.getDegree());
            accountForm.setSalutation(customer.getSalutation());
            accountForm.setEmailNotification(customer.isNewsletterEnabled());
            accountForm.setPhone(customer.getPhone());
            accountForm.setPhoneCode(customer.getPhoneCode());

            if (getUseEmail()) {
                accountForm.setEmail(account.getUsername());
            } else {
                accountForm.setEmail(customer.getEmail());
            }

            List<Address> addressesFor = customerService.getAddressesFor(customer);
            if (addressesFor != null) {
                for (Address address : addressesFor) {
                    if (address.isDefaultInvoiceAddress()) {
                        List<String> addressLines = address.getAddressLines();
                        if (addressLines != null && addressLines.size() > 0) {
                            accountForm.setInvoiceAddrStreet(addressLines.get(0));
                        }

                        accountForm.setInvoiceAddrFirm(address.getCompany());
                        accountForm.setInvoiceAddrUst(customer.getCompanyTaxIdNumber());
                        accountForm.setInvoiceAddrHouseNum(address.getHouseNumber());
                        accountForm.setInvoiceAddrCity(address.getCity());
                        accountForm.setInvoiceAddrZipCode(address.getZip());

                    } else if (address.isDefaultDeliveryAddress()) {
                        List<String> addressLines = address.getAddressLines();
                        if (addressLines != null && addressLines.size() > 0) {
                            accountForm.setShippingAddrStreet(addressLines.get(0));
                        }
                        accountForm.setShippingAddrHouseNum(address.getHouseNumber());
                        accountForm.setShippingAddrZipCode(address.getZip());
                        accountForm.setShippingAddrCity(address.getCity());
                    }
                }
            }
        }
    }

    @Request("/forgot-password")
    public Result forgotPassword() {
        return view("customer/account/forgot_password_form");
    }

    @PreView(on = { "/forgot-password", "/forgot-password-confirm" }, onView = OnView.EXISTS)
    public void preView(Result view) {
        view.bind("useEmail", getUseEmail());
    }

    @Request(value = "forgot-password-confirm", method = HttpMethod.POST, onError = "view: customer/account/forgot_password_form")
    public Result forgotPasswordConfirm(@Param @Required String username, Errors errors) {
        // send confirmation link
        Account account = customerService.getAccountFor(username);

        if (account != null) {
            account.createForgotPasswordToken();
            customerService.updateAccount(account);

            account = customerService.getAccountFor(username);
            if (account != null && account.getCustomerId() != null && account.getForgotPasswordToken() != null && account.getForgotPasswordOn() != null) {
                Customer customer = customerService.getCustomer(account.getCustomerId());

                if (customer != null && customer.getEmail() != null) {
                    String tokenToSend = new StringBuilder()
                        .append(account.getId())
                        .append(":")
                        .append(account.getForgotPasswordOn().getTime())
                        .append(":").append(account.getForgotPasswordToken()).toString();

                    String link = new StringBuilder(getSecureBasePath()).append("/customer/account/forgot-password-reset/?fpToken=")
                        .append(tokenToSend).toString();

                    Map<String, Object> templateParams = new HashMap<>();
                    templateParams.put("link", link);
                    mailerService.sendMail("forgotten_password", customer.getEmail(), templateParams);

                    app.publish(CUSTOMER_FORGOTTEN_PASSWORD_TOKEN, "customerId", customer.getId(), "link", link);

                    // FlashScope fs = FlashScope.getCurrent(getRequest(),
                    // true);
                    // fs.put("forgotPasswordConfirm", true);
                }
            }

            return redirect("/customer/account/forgot-password").flash("forgotPasswordConfirm", true);

        } else {
            errors.add("account.error.emailNotExist");
            return forgotPassword();
        }
    }

    @Request(value = "/forgot-password-reset", method = HttpMethod.GET)
    public Result forgotPasswordReset(@Param("fpToken") String fpToken, Errors errors) {

        AccountForm accountForm = new AccountForm();

        if (fpToken == null || "".equals(fpToken.trim())) {
            errors.add("account.password.reset.tokenMissing");
        } else {
            String[] tokenParts = fpToken.split(":");

            if (tokenParts == null || tokenParts.length != 3) {
                errors.add("account.password.reset.tokenIncomplete");
            } else {
                Id accountId = Id.parseId(tokenParts[0]);
                Long tokenTime = Long.valueOf(tokenParts[1]);
                String md5 = tokenParts[2];

                // send confirmation link
                Account account = customerService.getAccount(accountId);

                if (account != null) {
                    if (account.isForgotPasswordTokenValid(md5, accountId, tokenTime)) {
                        accountForm.setFpTokenIsValid(true);
                        accountForm.setFpToken(fpToken);
                    } else {
                        errors.add("account.password.reset.tokenInvalid");
                    }
                } else {
                    errors.add("account.password.reset.accountNotFound");
                }
            }
        }

        return view("customer/account/forgot_password_reset_form").bind("accountForm", accountForm);
    }

    @Request(value = "/forgot-password-save", method = HttpMethod.POST)
    public Result forgotPasswordSave(AccountForm accountForm, Errors errors) {
        if (accountForm.getFpToken() == null || "".equals(accountForm.getFpToken().trim())) {
            errors.add("account.password.reset.tokenMissing");
        } else {
            String[] tokenParts = accountForm.getFpToken().split(":");

            if (tokenParts == null || tokenParts.length != 3) {
                errors.add("account.password.reset.tokenIncomplete");
            } else {
                Id accountId = Id.parseId(tokenParts[0]);
                Long tokenTime = Long.valueOf(tokenParts[1]);
                String md5 = tokenParts[2];

                // send confirmation link
                Account account = customerService.getAccount(accountId);

                if (account != null) {
                    if (account.isForgotPasswordTokenValid(md5, accountId, tokenTime)) {
                        byte[] randomSalt;

                        try {
                            randomSalt = Passwords.getRandomSalt();

                            account.setPassword(encryptPassword(accountForm.getPassword1(), randomSalt)).setSalt(randomSalt).removeForgotPasswordToken();

                            customerService.updateAccount(account);

                            Customer customer = customerService.getCustomer(account.getCustomerId());

                            app.publish(CUSTOMER_FORGOTTEN_PASSWORD_SAVE, "customerId", customer.getId());
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            e.printStackTrace();
                            errors.add("account.password.reset.error");
                        }
                    } else {
                        errors.add("account.password.reset.tokenInvalid");
                    }
                } else {
                    errors.add("account.password.reset.accountNotFound");
                }
            }
        }

        return view("customer/account/forgot_password_reset_success").bind("accountForm", accountForm);
    }

    @Request("/delete")
    public Result delete() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        return view("customer/account/delete_confirm");
    }

    @Request("/delete-confirmed")
    public Result deleteConfirmed() {
        if (!isCustomerLoggedIn())
            return redirect("/customer/account/login/");

        Account account = customerService.getAccountFor((Customer) getLoggedInCustomer());

        if (account != null && account.getId() != null) {
            account.disableAccount();

            customerService.updateAccount(account);

            sessionInvalidate();
        }

        return view("customer/account/delete_success");
    }

    @Request("/header-login-container")
    public Result headerLoginContainer() {
        return view("customer/account/header_login_container");
    }

    private byte[] encryptPassword(String password, byte[] randomSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null || randomSalt == null || randomSalt.length == 0)
            throw new NullPointerException("Password and/or random salt cannot be null");

        return Passwords.getEncryptedPassword(password, getSalt(randomSalt));
    }

    private byte[] getSalt(byte[] randomSalt) throws NoSuchAlgorithmException {
        if (randomSalt == null || randomSalt.length == 0)
            throw new NullPointerException("Random salt cannot be null");

        byte[] sugar = MerchantConfig.GET.val(MerchantConfig.FRONTEND_SECURITY_SUGAR).getBytes();

        if (sugar == null || sugar.length == 0)
            throw new NullPointerException("Merchant sugar cannot be null");

        return Passwords.merge(randomSalt, sugar);
    }

    private Boolean getUseEmail() {
        return app.cpBool_(Key.USE_EMAIL, false);
    }

}
