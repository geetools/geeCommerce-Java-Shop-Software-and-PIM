package com.geecommerce.guiwidgets.rest.v1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.Str;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.core.util.Strings;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.repository.CouponCodes;
import com.geecommerce.guiwidgets.model.ActionGift;
import com.geecommerce.guiwidgets.model.DiscountPromotion;
import com.geecommerce.guiwidgets.model.DiscountPromotionSubscription;
import com.geecommerce.guiwidgets.repository.DiscountPromotionSubscriptions;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import au.com.bytecode.opencsv.CSVWriter;

@Path("/v1/discount-promotions")
public class DiscountPromotionResource extends AbstractResource {
    private final RestService service;
    private final MediaAssetService mediaAssetService;
    private final DiscountPromotionSubscriptions discountPromotionSubscriptions;
    private final CouponCodes couponCodes;
    SimpleDateFormat csvDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    public DiscountPromotionResource(RestService service, MediaAssetService mediaAssetService, DiscountPromotionSubscriptions discountPromotionSubscriptions, CouponCodes couponCodes) {
        this.service = service;
        this.mediaAssetService = mediaAssetService;
        this.discountPromotionSubscriptions = discountPromotionSubscriptions;
        this.couponCodes = couponCodes;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getDiscountPromotions(@FilterParam Filter filter) {
        return ok(service.get(DiscountPromotion.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DiscountPromotion getDiscountPromotion(@PathParam("id") Id id) {
        return checked(service.get(DiscountPromotion.class, id));
    }

    @DELETE
    @Path("{id}")
    public void removeDiscountPromotion(@PathParam("id") Id id) {
        DiscountPromotion discountPromotion = checked(service.get(DiscountPromotion.class, id));

        System.out.println("--- Removing discount promotion: " + discountPromotion);
        service.remove(discountPromotion);

    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createDiscountPromotion(Update update) {
        DiscountPromotion p = app.getModel(DiscountPromotion.class);
        try {
            p.set(update.getFields());
            setDiscountPromotionKey(p);
            p = service.create(p);
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }

        return created(p);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DiscountPromotion updateDiscountPromotion(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            DiscountPromotion p = checked(service.get(DiscountPromotion.class, id));
            p.set(update.getFields());
            setDiscountPromotionKey(p);

            service.update(p);
        }
        return checked(service.get(DiscountPromotion.class, id));
    }

    private void setDiscountPromotionKey(DiscountPromotion productPromotion) {
        String defaultLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_DEFAULT_EDIT_LANGUAGE);
        if (productPromotion.getKey() == null || productPromotion.getKey().isEmpty()) {
            if (productPromotion.getLabel() != null) {
                String name = productPromotion.getLabel().getClosestValue(defaultLanguage);
                if (name != null && !name.isEmpty()) {
                    productPromotion.setKey(Strings.slugify(name));
                }
            }
        }
    }

    @GET
    @Path("{id}/export/ordered")
    public Response getEmailExportOrdered(@PathParam("id") Id id, @Context HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"ordered-emails.csv\"");
        PrintWriter printWriter = response.getWriter();
        CSVWriter writer = new CSVWriter(printWriter, ';');
        writer.writeNext(new String[] { "E-mail", "From", "To", "Used", "Code" });

        DiscountPromotion p = checked(service.get(DiscountPromotion.class, id));
        if (p != null) {
            Coupon c = p.getCoupon();
            if (c != null) {
                List<CouponCode> couponCodes = c.getCodes();
                if (couponCodes != null) {
                    for (CouponCode couponCode : couponCodes) {
                        if (couponCode.getEmail() != null) {
                            String email = couponCode.getEmail();
                            if (couponCode.getCouponUsages() != null && couponCode.getCouponUsages().size() > 0) {
                                Date fromDate = DateTimes.maxOfDates(c.getFromDate(), couponCode.getFromDate());
                                Date toDate = DateTimes.minOfDates(c.getToDate(), couponCode.getToDate());
                                String used = couponCode.getCouponUsages() != null && couponCode.getCouponUsages().size() > 0
                                    ? (couponCode.getCouponUsages().get(0).getUsageDate() != null ? csvDate.format(couponCode.getCouponUsages().get(0)
                                        .getUsageDate()) : "---")
                                    : "";
                                writer.writeNext(new String[] { email, fromDate == null ? "" : csvDate.format(fromDate), toDate == null ? "" : csvDate.format(toDate), used, couponCode.getCode() });
                            }
                        }
                    }
                }
            }
        }

        writer.flush();
        writer.close();

        return Response.ok().build();
    }

    @GET
    @Path("{id}/export/expired")
    public Response getEmailExportExpired(@PathParam("id") Id id, @Context HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"expired-emails.csv\"");
        PrintWriter printWriter = response.getWriter();
        CSVWriter writer = new CSVWriter(printWriter, ';');
        writer.writeNext(new String[] { "E-mail", "From", "To", "Used", "Code" });
        // rom/to dates, used and coupon code
        DiscountPromotion p = checked(service.get(DiscountPromotion.class, id));
        if (p != null) {
            Coupon c = p.getCoupon();
            if (c != null) {
                List<CouponCode> couponCodes = c.getCodes();
                if (couponCodes != null) {
                    for (CouponCode couponCode : couponCodes) {
                        if (couponCode.getEmail() != null) {
                            String email = couponCode.getEmail();
                            if (couponCode.getCouponUsages() == null || couponCode.getCouponUsages().size() == 0) {
                                Date now = new Date();
                                Date fromDate = DateTimes.maxOfDates(c.getFromDate(), couponCode.getFromDate());
                                Date toDate = DateTimes.minOfDates(c.getToDate(), couponCode.getToDate());
                                if (toDate != null && toDate.before(now)) {
                                    writer.writeNext(new String[] { email, fromDate == null ? "" : csvDate.format(fromDate), csvDate.format(toDate), "", couponCode.getCode() });
                                }
                            }
                        }
                    }
                }
            }
        }

        writer.flush();
        writer.close();

        return Response.ok().build();
    }

    @GET
    @Path("{id}/export/all")
    public Response getEmailExportAll(@PathParam("id") Id id, @Context HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"all.csv\"");
        PrintWriter printWriter = response.getWriter();
        CSVWriter writer = new CSVWriter(printWriter, ';');
        writer.writeNext(new String[] { "E-mail", "From", "To", "Used", "Code" });

        DiscountPromotion p = checked(service.get(DiscountPromotion.class, id));
        if (p != null) {
            Coupon c = p.getCoupon();
            if (c != null) {
                List<CouponCode> couponCodes = this.couponCodes.thatBelongTo(c);
                if (couponCodes != null) {
                    for (CouponCode couponCode : couponCodes) {
                        String code = couponCode.getCode();
                        String email = couponCode.getEmail() == null ? Str.EMPTY : couponCode.getEmail();
                        String used = couponCode.getCouponUsages() != null && couponCode.getCouponUsages().size() > 0
                            ? (couponCode.getCouponUsages().get(0).getUsageDate() != null ? csvDate.format(couponCode.getCouponUsages().get(0).getUsageDate())
                                : "---")
                            : "";
                        String exported = couponCode.getExportedDate() != null ? csvDate.format(couponCode.getExportedDate()) : "";
                        Date fromDate = DateTimes.maxOfDates(c.getFromDate(), couponCode.getFromDate());
                        Date toDate = DateTimes.minOfDates(c.getToDate(), couponCode.getToDate());

                        writer.writeNext(new String[] { email, fromDate == null ? "" : csvDate.format(fromDate), toDate == null ? "" : csvDate.format(toDate), used, code });
                    }
                }
            }
        }

        writer.flush();
        writer.close();

        return Response.ok().build();
    }

    private String getGift(Id id, DiscountPromotion discountPromotion) {
        if (discountPromotion.getGifts() == null)
            return "";

        Optional<ActionGift> actionGift = discountPromotion.getGifts().stream().filter(item -> item.getId().equals(id)).findFirst();
        if (actionGift.isPresent()) {
            ActionGift gift = actionGift.get();
            return gift.getName().str();
        }

        return "";
    }

    @GET
    @Path("{id}/export/gifts")
    public Response getEmailExportGifts(@PathParam("id") Id id, @Context HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"gifts.csv\"");
        OutputStream os = response.getOutputStream();
        os.write(239); // 0xEF
        os.write(187); // 0xBB
        os.write(191); // 0xBF
        OutputStreamWriter printWriter = new OutputStreamWriter(os, "UTF-8");
        CSVWriter writer = new CSVWriter(printWriter, ';');
        writer.writeNext(new String[] { "Code", "E-mail", "Forename", "Surname", "Address", "ZIP", "City", "Gift-ID", "Gift" });

        DiscountPromotion p = checked(service.get(DiscountPromotion.class, id));
        List<DiscountPromotionSubscription> dpSubscriptions = discountPromotionSubscriptions.subscribedOnPromotion(id);

        if (p != null) {
            Coupon c = p.getCoupon();
            if (c != null) {
                if (dpSubscriptions != null) {
                    for (DiscountPromotionSubscription dpSubscription : dpSubscriptions) {
                        String code = dpSubscription.getCouponCode();
                        String email = dpSubscription.getEmail();
                        String firstName = dpSubscription.getForm() == null ? "" : (String) dpSubscription.getForm().get("firstName");
                        String lastName = dpSubscription.getForm() == null ? "" : (String) dpSubscription.getForm().get("lastName");
                        String address = dpSubscription.getForm() == null ? "" : (String) dpSubscription.getForm().get("address");
                        String zip = dpSubscription.getForm() == null ? "" : (String) dpSubscription.getForm().get("zip");
                        String city = dpSubscription.getForm() == null ? "" : (String) dpSubscription.getForm().get("city");
                        String giftId = dpSubscription.getGiftId().toString();
                        String gift = getGift(dpSubscription.getGiftId(), p);

                        writer.writeNext(new String[] { code, email, firstName, lastName, address, zip, city, giftId, gift });
                    }

                }
            }
        }

        writer.flush();
        writer.close();

        return Response.ok().build();
    }

    @PUT
    @Path("{id}/gifts/positions")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateGiftsPositions(@PathParam("id") Id id, HashMap<String, Integer> positionsMap) {
        if (id != null && positionsMap != null && positionsMap.size() > 0) {
            DiscountPromotion discountPromotion = checked(service.get(DiscountPromotion.class, id));

            Set<String> keys = positionsMap.keySet();

            for (String key : keys) {
                Id giftId = Id.valueOf(key);
                Integer pos = positionsMap.get(key);

                for (ActionGift gift : discountPromotion.getGifts()) {
                    if (gift.getId().equals(giftId)) {
                        gift.setPosition(pos);
                    }
                }
            }
            service.update(discountPromotion);
        }
    }

    @POST
    @Path("{id}/gifts")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response newGift(@PathParam("id") Id id, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        ActionGift gift = null;

        if (id != null) {
            // Get product and image.
            FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
            MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());

            gift = app.getModel(ActionGift.class);
            gift.setMediaAsset(newMediaAsset);
            gift.setPosition(99);
            gift.setId(app.nextId());

            saveGift(gift, id);
        } else {
            throwBadRequest("SlideShowId cannot be null in requestURI. Expecting: slide-shows/{id}/slides");
        }

        if (gift == null || gift.getId() == null) {
            throwInternalServerError();
        }

        return ok(gift);
    }

    private void saveGift(ActionGift gift, Id discountPromotionId) {
        int cnt = 0;
        while (true) {
            try {
                DiscountPromotion discountPromotion = checked(service.get(DiscountPromotion.class, discountPromotionId));
                discountPromotion.getGifts().add(gift);
                service.update(discountPromotion);
                return;
            } catch (Exception ex) {
                cnt++;
            }
        }
    }

    @DELETE
    @Path("{id}/gifts/{giftId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeGift(@PathParam("id") Id id, @PathParam("giftId") Id giftId) {
        if (id != null && giftId != null) {
            // Get product and media-asset.
            DiscountPromotion discountPromotion = checked(service.get(DiscountPromotion.class, id));

            ActionGift forRemove = null;
            for (ActionGift gift : discountPromotion.getGifts()) {
                if (gift.getId().equals(giftId)) {
                    forRemove = gift;
                    break;
                }
            }

            if (forRemove != null) {
                discountPromotion.getGifts().remove(forRemove);
                mediaAssetService.remove(forRemove.getMediaAsset());
            }

            service.update(discountPromotion);
        } else {
            throwBadRequest("SlideShowId and slideId cannot be null in requestURI. Expecting: slide-shows/{id}/slides/{slideId}");
        }
    }

    @PUT
    @Path("{id}/gifts")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateGifts(@PathParam("id") Id id, List<Update> updates) {
        DiscountPromotion discountPromotion = checked(service.get(DiscountPromotion.class, id));

        if (updates != null && updates.size() > 0) {
            for (Update update : updates) {
                if (update != null && update.getId() != null && update.getFields() != null && update.getFields().size() > 0) {
                    for (ActionGift gift : discountPromotion.getGifts()) {
                        if (gift.getId().equals(update.getId())) {
                            gift.set(update.getFields());
                        }
                    }
                }
            }
            service.update(discountPromotion);
        }
    }

}
