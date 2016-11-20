package com.geecommerce.optivobroadmail.mailer.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

public class TemplateParameter {
    private static final Map<String, String> parameters;

    static {
        parameters = new LinkedHashMap<String, String>() {
            {
                put(TemplateParameter.EMAIL, "Email");
                put(TemplateParameter.KDKONTO, "KdKonto");
                put(TemplateParameter.KDKONTO_NR, "KdKonto-Nr");
                put(TemplateParameter.ANREDE, "Anrede");
                put(TemplateParameter.NAME, "Name");
                put(TemplateParameter.VORNAME, "Vorname");
                put(TemplateParameter.ORDER_ID, "KV-Nr");
                put(TemplateParameter.ORDER_DATE, "Bestelldatum");
                put(TemplateParameter.PAYMENT_METHOD, "Bezahlart");

                put(TemplateParameter.INVOICE_VORENAME, "Rechn.Vorname");
                put(TemplateParameter.INVOICE_SURNAME, "Rechn.Name");
                put(TemplateParameter.INVOICE_STREET, "Rechn.Straße");
                put(TemplateParameter.INVOICE_HOUSENUMBER, "Rechn.HausNr");
                put(TemplateParameter.INVOICE_ADDITIONAL, "Rechn.Zusatz");
                put(TemplateParameter.INVOICE_ZIPCODE, "Rechn.PLZ");
                put(TemplateParameter.INVOICE_CITY, "Rechn.Ort");
                put(TemplateParameter.DELIVERY_VORENAME, "Lief.Vorname");
                put(TemplateParameter.DELIVERY_SURNAME, "Lief.Name");
                put(TemplateParameter.DELIVERY_STREET, "Lief.Straße");
                put(TemplateParameter.DELIVERY_HOUSENUMBER, "Lief.HausNr");
                put(TemplateParameter.DELIVERY_ADDITIONAL, "Lief.Zusatz");
                put(TemplateParameter.DELIVERY_ZIPCODE, "Lief.PLZ");
                put(TemplateParameter.DELIVERY_CITY, "Lief.Ort");
                put(TemplateParameter.DELIVERY_DATE, "Lief.Termin");

                put(TemplateParameter.KDKTO_LINK, "KdKto-Link");
                put(TemplateParameter.PW_LINK, "PwLink");
                put(TemplateParameter.ORDER_SUMMARY, "Artikeltabelle");
                put(TemplateParameter.ORDER_TOTAL, "Gesamtsumme");
                put(TemplateParameter.ORDER_VAT, "MwSt");
                put(TemplateParameter.FILIALE_ADDRESS, "Filialadresse");
                put(TemplateParameter.FILIALE_WORKTIME, "Filialöffnung");
                put(TemplateParameter.IMPRESSUM, "Impressum");
                put(TemplateParameter.FIRMA, "Firma");
                put(TemplateParameter.APPENDIX, "Anhang");

                put(TemplateParameter.KUNDEN_NR, "KDNr");
                put(TemplateParameter.FIRMEN_URL, "firmenURL");
                put(TemplateParameter.ENTERPRISE, "Firma");
                put(TemplateParameter.CONDITIONS, "Widerrufsbelehrung");
                put(TemplateParameter.SHIPPING_AMOUNT, "Versandkosten");

                put(TemplateParameter.OUTSTANDING_AMOUNT, "Saldo");

            }
        };
    }

    public static String getLabel(String parameterName) {
        return parameters.get(parameterName);
    }

    public static final String FIRMA = "company";
    public static final String ANREDE = "salutation";
    public static final String NAME = "lastname";
    public static final String VORNAME = "firstname";
    public static final String EMAIL = "email";
    public static final String KDKTO_LINK = "custtolink";
    public static final String IMPRESSUM = "impressum";
    public static final String PW_LINK = "pwlink";
    public static final String KDKONTO = "cusaccount"; // customer account
                                                       // emails address
    public static final String KDKONTO_NR = "cusaccnumber"; // customer account
                                                            // ID number
    public static final String ORDER_ID = "kvnumber"; // order ID
    public static final String KUNDEN_NR = "KDNr";
    public static final String FIRMEN_URL = "firmenURL";
    public static final String APPENDIX = "attachment";
    public static final String ENTERPRISE = "enterprise";
    public static final String FILIALE_ADDRESS = "branchaddress";
    public static final String FILIALE_WORKTIME = "branchopening";

    public static final String INVOICE_VORENAME = "billfirstname";
    public static final String INVOICE_SURNAME = "billlastname";
    public static final String INVOICE_STREET = "billstreet";
    public static final String INVOICE_HOUSENUMBER = "billhousenumber";
    public static final String INVOICE_ADDITIONAL = "billaddition";
    public static final String INVOICE_ZIPCODE = "billzipcode";
    public static final String INVOICE_CITY = "billcity";

    public static final String ORDER_DATE = "orderdate";
    public static final String PAYMENT_METHOD = "paymenttype";

    public static final String DELIVERY_VORENAME = "shippingfirstname";
    public static final String DELIVERY_SURNAME = "shippinglastname";
    public static final String DELIVERY_STREET = "shippingstreet";
    public static final String DELIVERY_HOUSENUMBER = "shippinghousenumber";
    public static final String DELIVERY_ADDITIONAL = "shippingaddition";
    public static final String DELIVERY_ZIPCODE = "shippingzipcode";
    public static final String DELIVERY_CITY = "shippingcity";
    public static final String DELIVERY_DATE = "shippingdate";

    public static final String ORDER_SUMMARY = "articletable";
    public static final String ORDER_TOTAL = "sumtotal";
    public static final String ORDER_VAT = "addedvaluetax";

    public static final String SHIPPING_AMOUNT = "Versandkosten";
    public static final String OUTSTANDING_AMOUNT = "Saldo";

    public static final String CONDITIONS = "Widerrufsbelehrung";

}
