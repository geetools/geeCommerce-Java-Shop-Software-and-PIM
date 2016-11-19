package com.geecommerce.core;

import java.util.Locale;

public class Constant {
    public static final String CORE_JAR_PREFIX = "geecommerce-core-";

    public static final String CORE_PACKAGE_PREFIX = "com.geecommerce.core";

    public static final String BOOTSTRAP_LOGPATH = "Bootstrap.Logpath";

    public static final String APPLICATION_KEY_PROJECTS_PATH = "Application.Projects.Path";

    public static final String APPLICATION_KEY_INJECT_MODULES = "Application.Inject.Modules";

    public static final String APPLICATION_MODE_LIVE = "live";

    public static final String APPLICATION_MODE_DEV = "dev";

    public static final String APPLICATION_MODE_TEST = "test";

    public static final String APPLICATION_MODE_UNIT_TEST = "unit-test";

    public static final String MERCHANT_CONFIG_NAME = "Merchant.properties";

    public static final String MERCHANT_TEST_CONFIG_NAME = "Merchant.test.properties";

    public static final Locale LOCALE_ANY = new Locale("*");

    public static final String PAYMENT_METHOD_FRONTEND_FORMS_BASE_PATH = "/WEB-INF/templates/pages/checkout/payment_methods";

    public static final String PAYMENT_METHOD_FRONTEND_FORMS_FILE_NAME = "form.ftl";

    public static final String LOGIN_METHOD_FRONTEND_FORMS_BASE_PATH = "/WEB-INF/jsp/pages/customer/account/login_methods";

    public static final String LOGIN_METHOD_FRONTEND_FORMS_FILE_NAME = "form.ftl";

    public static final String SESSION_KEY_LOGGED_IN_CUTOMER = "logged_in_customer";

    public static final String SESSION_KEY_CART = "cart";

    public static final String STRIPES_IGNORE_EVENT = "stripes.ignore.event";
}
