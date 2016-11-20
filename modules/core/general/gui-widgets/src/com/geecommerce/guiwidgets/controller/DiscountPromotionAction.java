package com.geecommerce.guiwidgets.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.web.BaseActionBean;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.repository.CouponCodes;
import com.geecommerce.coupon.service.CouponService;
import com.geecommerce.customer.model.Customer;
import com.geecommerce.customer.service.CustomerService;
import com.geecommerce.guiwidgets.form.UserForm;
import com.geecommerce.guiwidgets.model.ActionGift;
import com.geecommerce.guiwidgets.model.DiscountPromotion;
import com.geecommerce.guiwidgets.model.DiscountPromotionSubscription;
import com.geecommerce.guiwidgets.repository.DiscountPromotionSubscriptions;
import com.geecommerce.guiwidgets.repository.DiscountPromotions;
import com.geecommerce.guiwidgets.service.DiscountPromotionService;
import com.geecommerce.mailer.service.MailerService;
import com.geecommerce.news.subscription.service.NewsSubscriberService;
import com.google.inject.Inject;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/discount-promotion/{$event}/{id}")
public class DiscountPromotionAction extends BaseActionBean {

    private final NewsSubscriberService newsSubscriberService;
    private final CouponService couponService;
    private final CustomerService customerService;
    private final CouponCodes couponCodes;
    private final DiscountPromotions discountPromotions;
    private final DiscountPromotionService discountPromotionService;
    private final DiscountPromotionSubscriptions discountPromotionSubscriptions;
    private final MailerService mailerService;

    private final String email = null;
    private String message = null;
    private String errorMessage = null;

    private String promotion = null;
    private String template = null;

    private DiscountPromotion discountPromotion;

    private final String SESSION_KEY_USER_FORM = "session_key_user_form";
    private final String COOKIE_DISPLAYED_COUNT = "dp_dc_";
    private final String COOKIE_DISPLAYED_LAST_TIME = "dp_d_lt_";
    private final String COOKIE_RECEIVED_CODE = "dp_rc_";

    private final String SHOW_PROMO = "show_promo";

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private UserForm form = null;

    @Inject
    public DiscountPromotionAction(NewsSubscriberService newsSubscriberService, CouponService couponService,
        CouponCodes couponCodes, DiscountPromotions discountPromotions, MailerService mailerService,
        CustomerService customerService, DiscountPromotionSubscriptions discountPromotionSubscriptions,
        DiscountPromotionService discountPromotionService) {
        this.newsSubscriberService = newsSubscriberService;
        this.couponService = couponService;
        this.couponCodes = couponCodes;
        this.discountPromotions = discountPromotions;
        this.mailerService = mailerService;
        this.customerService = customerService;
        this.discountPromotionSubscriptions = discountPromotionSubscriptions;
        this.discountPromotionService = discountPromotionService;
    }

    private Integer getShowedTimes(String str) {
        try {
            Integer val = Integer.parseInt(str);
            return val;
        } catch (Exception ex) {
            return null;
        }
    }

    private Date getShowedLastTime(String str) {
        try {
            Date val = simpleDateFormat.parse(str);
            return val;
        } catch (Exception ex) {
            return null;
        }
    }

    @HandlesEvent("promotion")
    public Resolution promotion() {
        String promotionKey = promotion;
        String promotionTemplate = template;

        boolean showPromoAnyway = false;
        Id showPromotionId = null;
        String showPromo = "";// request.getParameter(SHOW_PROMO)

        if (showPromo != null && !showPromo.isEmpty()) {
            if (showPromo.toLowerCase().equals("true")) {
                showPromoAnyway = true;
            } else {
                try {
                    showPromotionId = Id.parseId(showPromo);
                } catch (Exception ex) {
                    showPromotionId = null;
                }

            }
        }

        if (showPromotionId != null) {
            DiscountPromotion discountPromotion = discountPromotionService.getDiscountPromotion(showPromotionId);
            if (discountPromotion != null) {
                this.discountPromotion = discountPromotion;
            }
        } else {
            List<DiscountPromotion> discountPromotions = discountPromotionService
                .getDiscountPromotionByKey(promotionKey);

            if (discountPromotions != null && discountPromotions.size() != 0) {

                DiscountPromotion discountPromotion = null;

                Optional<DiscountPromotion> optional = discountPromotions.stream()
                    .filter(x -> (x.getShowFrom() == null || x.getShowFrom().before(new Date()))
                        && (x.getShowTo() == null || x.getShowTo().after(new Date())))
                    .findFirst();

                if (optional.isPresent())
                    discountPromotion = optional.get();

                if (discountPromotion == null)
                    return view("discount_promotion/empty");

                String receivedCodeStr = app.cookieGet(COOKIE_RECEIVED_CODE + discountPromotion.getId());
                String showedTimesStr = app.cookieGet(COOKIE_DISPLAYED_COUNT + discountPromotion.getId());
                String showedLastTimeStr = app.cookieGet(COOKIE_DISPLAYED_LAST_TIME + discountPromotion.getId());
                String promotionIdStr = discountPromotion.getId().toString();

                if (receivedCodeStr == null || showPromoAnyway) {
                    if (discountPromotion.getShowForAll() != null && discountPromotion.getShowForAll()
                        || couponService.couponCouldBeUsedCustomerWithGroups(discountPromotion.getCoupon())
                        || showPromoAnyway) {
                        Integer showedTimes = getShowedTimes(showedTimesStr);
                        Date showedLastTime = getShowedLastTime(showedLastTimeStr);

                        if (promotionIdStr == null || !promotionIdStr.equals(discountPromotion.getId().toString())) {
                            showedTimes = null;
                            showedLastTime = null;
                        }

                        if (showedTimes == null || discountPromotion.getShowTimes() == null
                            || showedTimes < discountPromotion.getShowTimes() || showPromoAnyway) {

                            Date now = new Date();
                            if (showedLastTime == null || discountPromotion.getRerunAfter() == null
                                || new Period(new DateTime(showedLastTime), new DateTime(now))
                                    .getHours() > discountPromotion.getRerunAfter()
                                || showPromoAnyway) {
                                this.discountPromotion = discountPromotion;

                                app.cookieSet(COOKIE_DISPLAYED_LAST_TIME + promotionIdStr, simpleDateFormat.format(now),
                                    (60 * 60 * 24 * 365 * 2));
                                if (showedTimes == null)
                                    showedTimes = 1;
                                else {
                                    showedTimes += 1;
                                }
                                app.cookieSet(COOKIE_DISPLAYED_COUNT + promotionIdStr, showedTimes,
                                    (60 * 60 * 24 * 365 * 2));
                            }
                        }
                    }
                }

            }
        }

        if (this.discountPromotion == null)
            return view("discount_promotion/empty");
        else
            return view("discount_promotion/" + promotionTemplate);
    }

    @HandlesEvent("special")
    public Resolution special() {

        discountPromotion = discountPromotions.findById(DiscountPromotion.class, getId());

        if (discountPromotion == null)
            return new ErrorResolution(404);

        Date nowDate = new Date();
        if (discountPromotion.getShowFrom() != null && nowDate.before(discountPromotion.getShowFrom())
            || discountPromotion.getShowTo() != null && nowDate.after(discountPromotion.getShowTo()))
            return new ErrorResolution(404);

        return view("discount_promotion/special_form");
    }

    @HandlesEvent("process-special")
    public Resolution processSpecial() {
        UserForm userForm = getForm();
        discountPromotion = discountPromotions.findById(DiscountPromotion.class, getId());

        if (discountPromotion == null) {
            errorMessage = app.message(
                "Zdá se, že Váš e-mail je již v naší databázi. Tato akce je pouze pro nově registrované, ale nezoufejte, e-mail s jinou speciální akcí pro Vás se právě připravuje.");
            return view("discount_promotion/special_result");
        }

        Date nowDate = new Date();
        if (discountPromotion.getShowFrom() != null && nowDate.before(discountPromotion.getShowFrom())
            || discountPromotion.getShowTo() != null && nowDate.after(discountPromotion.getShowTo())) {
            errorMessage = app.message(
                "Zdá se, že Váš e-mail je již v naší databázi. Tato akce je pouze pro nově registrované, ale nezoufejte, e-mail s jinou speciální akcí pro Vás se právě připravuje.");
            return view("discount_promotion/special_result");
        }

        String email = userForm.getEmail();
        newsSubscriberService.subscribe(userForm.getEmail(), "DSC_PROMO", getId());

        DiscountPromotion discountPromotion = discountPromotions.findById(DiscountPromotion.class, getId());
        if (!couponService.couponCouldBeUsedCustomerWithGroups(discountPromotion.getCoupon())) {
            Customer customer = customerService.getCustomer(userForm.getEmail());
            if (customer != null) {
                errorMessage = app.message(
                    "Zdá se, že Váš e-mail je již v naší databázi. Tato akce je pouze pro nově registrované, ale nezoufejte, e-mail s jinou speciální akcí pro Vás se právě připravuje.");
                return view("discount_promotion/special_result");
            }
        }

        Coupon coupon = discountPromotion.getCoupon();
        // check there no codes exists
        CouponCode couponCode = couponCodes.thatBelongTo(coupon, userForm.getEmail());
        if (couponCode != null) {
            errorMessage = app.message(
                "Zdá se, že Váš e-mail je již v naší databázi. Tato akce je pouze pro nově registrované, ale nezoufejte, e-mail s jinou speciální akcí pro Vás se právě připravuje.");
            return view("discount_promotion/special_result");
        }

        // create coupon code
        couponCode = couponService.generateCode(coupon, userForm.getEmail(), discountPromotion.getCouponDuration());

        // send email
        Date endDate;
        if (couponCode.getToDate() != null) {
            endDate = couponCode.getToDate();
        } else {
            if (coupon.getToDate() != null) {
                endDate = coupon.getToDate();
            } else {
                endDate = discountPromotion.getShowTo();
            }
        }

        app.cookieSet(COOKIE_RECEIVED_CODE + discountPromotion.getId(), true, (60 * 60 * 24 * 365 * 2));

        DiscountPromotionSubscription subscription = app.model(DiscountPromotionSubscription.class);
        subscription.setDiscountPromotionId(discountPromotion.getId());
        subscription.setEmail(userForm.getEmail());
        subscription.setCouponCode(couponCode.getCode());
        subscription.setForm(userForm.toMap());
        subscription.setGiftId(Id.parseId(userForm.getGift()));

        discountPromotionSubscriptions.add(subscription);
        ActionGift actionGift = discountPromotion.getGifts().stream()
            .filter(g -> g.getId().toString().equals(userForm.getGift())).findFirst().get();

        String gift = actionGift.getName().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("couponCode", couponCode.getCode());
        templateParams.put("validTo", dateFormat.format(endDate));
        templateParams.put("address", userForm.getAddress());
        templateParams.put("zip", userForm.getZip());
        templateParams.put("city", userForm.getCity());
        templateParams.put("gift", gift);

        mailerService.sendMail(discountPromotion.getEmailTemplateCode(), userForm.getEmail(), templateParams);

        message = app.message("Na Váš e-mail byla odeslána zpráva obsahující unikátní kód.");
        this.discountPromotion = discountPromotion;
        sessionRemove(SESSION_KEY_USER_FORM);

        return view("discount_promotion/special_result");
    }

    @HandlesEvent("view")
    public Resolution view() {

        discountPromotion = discountPromotions.findById(DiscountPromotion.class, getId());
        return view("discount_promotion/view");
    }

    @HandlesEvent("coupon-code")
    public Resolution getCoupon() {
        Map<String, String> resultMap = new HashMap<>();

        String email = getRequest().getParameter("email");
        newsSubscriberService.subscribe(email, "DSC_PROMO", getId());

        DiscountPromotion discountPromotion = discountPromotions.findById(DiscountPromotion.class, getId());
        if (!couponService.couponCouldBeUsedCustomerWithGroups(discountPromotion.getCoupon())) {
            Customer customer = customerService.getCustomer(email);
            if (customer != null) {
                resultMap.put("result", "error");
                resultMap.put("message", app.message(
                    "Zdá se, že Váš e-mail je již v naší databázi. Tato akce je pouze pro nově registrované, ale nezoufejte, e-mail s jinou speciální akcí pro Vás se právě připravuje."));
                return json(Json.toJson(resultMap));
            }
        }

        Coupon coupon = discountPromotion.getCoupon();
        // check there no codes exists
        CouponCode couponCode = couponCodes.thatBelongTo(coupon, email);
        if (couponCode != null) {
            resultMap.put("result", "error");
            resultMap.put("message", app.message(
                "Zdá se, že Váš e-mail je již v naší databázi. Tato akce je pouze pro nově registrované, ale nezoufejte, e-mail s jinou speciální akcí pro Vás se právě připravuje."));
            return json(Json.toJson(resultMap));
        }
        // create coupon code
        couponCode = couponService.generateCode(coupon, email, discountPromotion.getCouponDuration());

        // send email
        Date endDate;
        if (couponCode.getToDate() != null) {
            endDate = couponCode.getToDate();
        } else {
            if (coupon.getToDate() != null) {
                endDate = coupon.getToDate();
            } else {
                endDate = discountPromotion.getShowTo();
            }
        }

        // Create subscription
        DiscountPromotionSubscription subscription = app.model(DiscountPromotionSubscription.class);
        subscription.setDiscountPromotionId(discountPromotion.getId());
        subscription.setEmail(email);
        subscription.setCouponCode(couponCode.getCode());

        discountPromotionSubscriptions.add(subscription);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("code", couponCode.getCode());
        templateParams.put("description", discountPromotion.getDescriptionEmail().getVal());
        templateParams.put("endDate", dateFormat.format(endDate));
        mailerService.sendMail("discount_promotion_template", email, templateParams);

        app.cookieSet(COOKIE_RECEIVED_CODE + discountPromotion.getId(), true, (60 * 60 * 24 * 365 * 2));

        resultMap.put("result", "ok");
        return json(Json.toJson(resultMap));

    }

    public String getEmail() {
        return email;
    }

    public DiscountPromotion getDiscountPromotion() {
        return discountPromotion;
    }

    public UserForm getForm() {
        UserForm userForm = sessionGet(SESSION_KEY_USER_FORM);

        if (userForm == null) {
            userForm = new UserForm();
            sessionSet(SESSION_KEY_USER_FORM, userForm);

            this.form = userForm;
        } else {
            this.form = userForm;
        }

        return this.form;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}
