package com.geecommerce.antispam.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringUtils;

import com.geecommerce.antispam.configuration.Key;
import com.geecommerce.antispam.model.RequestCounter;
import com.geecommerce.antispam.repository.RequestCounters;
import com.geecommerce.antispam.service.RequestCounterService;
import com.geecommerce.core.App;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.utils.Dates;
import com.google.inject.Inject;

@Helper
public class DefaultAntiSpamHelper implements AntiSpamHelper {
    @Inject
    protected App app;

    protected final RequestCounters requestCounters;
    protected final RequestCounterService requestCounterService;

    protected static final String url = "https://www.google.com/recaptcha/api/siteverify";
    protected final static String USER_AGENT = "Mozilla/5.0";

    @Inject
    public DefaultAntiSpamHelper(RequestCounters requestCounters, RequestCounterService requestCounterService) {
        this.requestCounters = requestCounters;
        this.requestCounterService = requestCounterService;
    }

    @Override
    public boolean checkSecurity() {
        return checkHoneyPotField() && checkRecapture();
    }

    @Override
    public boolean checkHoneyPotField() {

        if (!app.cpBool_(Key.HONEYPOT, true))
            return true;

        String fillme = app.servletRequest().getParameter("fillme");

        if (StringUtils.isBlank(fillme))
            return true;

        return false;
    }

    @Override
    public boolean checkRecapture() {
        if (!app.cpBool_(Key.RECAPTURE, false))
            return true;

        String gRecaptchaResponse = app.servletRequest().getParameter("g-recaptcha-response");
        return verifyRecaptcha(gRecaptchaResponse);
    }

    @Override
    public boolean checkRequestCounter(String name) {
        String ip = app.getClientIpAddress();

        RequestCounter requestCounter = requestCounters.withNameAndIp(name, ip);

        if (requestCounter != null) {
            if (requestCounter.getBlocked()) {
                int banPeriod = app.cpInt_(Key.REQUEST_BAN_TIME, 60 * 60);
                long dateDiff = Dates.diff(requestCounter.getRequestTime(), new Date(), TimeUnit.SECONDS);
                if (dateDiff >= banPeriod) {
                    requestCounterService.resetCounter(requestCounter);
                    return true;
                } else {
                    return false;
                }
            } else {
                int period = app.cpInt_(Key.REQUEST_TIME, 60 * 60);
                int count = app.cpInt_(Key.REQUEST_COUNT, 5);
                long dateDiff = Dates.diff(requestCounter.getRequestTime(), new Date(), TimeUnit.SECONDS);
                if (dateDiff >= period) {
                    requestCounterService.resetCounter(requestCounter);
                    return true;
                } else {
                    if (requestCounter.getCount() < count) {
                        requestCounterService.increaseCounter(requestCounter);
                        return true;
                    } else {
                        requestCounterService.blockCounter(requestCounter);
                        return false;
                    }
                }
            }
        } else {
            requestCounterService.createCounter(name, ip);
            return true;
        }
    }

    private boolean verifyRecaptcha(String gRecaptchaResponse) {
        if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
            return false;
        }

        String secret = app.cpStr_(Key.RECAPTURE_SECURE_KEY);

        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String postParams = "secret=" + secret + "&response=" + gRecaptchaResponse;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse JSON response and return 'success' value
            Map<String, Object> jsonObject = Json.fromJson(response.toString(), HashMap.class);
            boolean result = (boolean) jsonObject.get("success");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
