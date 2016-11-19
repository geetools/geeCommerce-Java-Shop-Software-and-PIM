package com.geecommerce.optivobroadmail.mailer.configuration;

public class Configuration {
    public static final String BM_MAILSERVICE_HOST = "optivobroadmail/http-api/service_host";
    public static final String BM_MAILSERVICE_PROTOCOL = "optivobroadmail/http-api/service_protocol";
    public static final String BM_MAILSERVICE_AUTH_CODE = "optivobroadmail/http-api/service_auth_code";

    public static final String BM_NEWSLETTER_AUTH_CODE_1 = "optivobroadmail/http-api/newsletter_auth_code_1";
    public static final String BM_NEWSLETTER_AUTH_CODE_2 = "optivobroadmail/http-api/newsletter_auth_code_2";
    public static final String BM_NEWSLETTER_OPT_IN_ID = "optivobroadmail/http-api/newsletter_opt_in_id";
    public static final String BM_NEWSLETTER_RECIPIENT_LIST_ID = "optivobroadmail/http-api/newsletter_recipient_list_id";

    public static final String BM_MAILING_ID = "optivobroadmail/http-api/mailing_id";

    public static final String BM_SUCCESS_URL = "optivobroadmail/http-api/success_url";
    public static final String BM_FAILURE_URL = "optivobroadmail/http-api/failure_url";
    public static final String BM_URL = "optivobroadmail/http-api/forward_url";
    public static final String BM_ENCODING = "optivobroadmail/http-api/encoding";
    public static final String BM_VERBOSE = "optivobroadmail/http-api/verbose";

    public static final String ACCOUNT_CREATED_MAILING_ID = "optivobroadmail/http-api/mailings/account_created";
    public static final String PASSWORD_RESET_MAILING_ID = "optivobroadmail/http-api/mailings/password_reset";
    public static final String PASSWORD_RESET_CONFIRM_MAILING_ID = "optivobroadmail/http-api/mailings/password_reset_confirm";

    public static final String ORDER_REVOCATION_SMALL_PARCEL_DELIVERY = "optivobroadmail/http-api/mailings/order_revocation_smd";
    public static final String ORDER_REVOCATION_TWO_MAN_DELIVERY = "optivobroadmail/http-api/mailings/order_revocation_tmd";
    public static final String ORDER_REVOCATION_CLICK_AND_COLLECT = "optivobroadmail/http-api/mailings/order_revocation_cc";
    public static final String ORDER_CANCELLATION = "optivobroadmail/http-api/mailings/order_cancellation";
    public static final String ORDER_READY_TO_COLLECT = "optivobroadmail/http-api/mailings/order_ready_to_collect";

    public static final String ORDER_CONFIRMATION_PREPAYMENT = "optivobroadmail/http-api/mailings/order_confirmation_prepayment";
    public static final String ORDER_CONFIRMATION = "optivobroadmail/http-api/mailings/order_confirmation";

    public static final String ORDER_PREPAYMENT_REMINDER = "optivobroadmail/http-api/mailings/order_prepayment_reminder";

    public static final String ORDER_CONFIRMATION_PREPAYMENT_WITH_PICKUP = "optivobroadmail/http-api/mailings/order_confirmation_prepayment_pickup";
    public static final String ORDER_CONFIRMATION_WITH_PICKUP = "optivobroadmail/http-api/mailings/order_confirmation_pickup";

    public static String SEND_TO_ARCHIVE = "optivobroadmail/http-api/send_to_archive";
    public static String ARCHIVE_MAILBOX = "optivobroadmail/http-api/archive_mailbox";
}
