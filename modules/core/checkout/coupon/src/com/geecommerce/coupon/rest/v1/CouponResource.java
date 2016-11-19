package com.geecommerce.coupon.rest.v1;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.FrontendInput;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.attribute.TargetObjectCode;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Strings;
import com.geecommerce.coupon.enums.CouponActionType;
import com.geecommerce.coupon.enums.CouponFilterAttributeType;
import com.geecommerce.coupon.enums.CouponFilterNodeType;
import com.geecommerce.coupon.helper.CouponHelper;
import com.geecommerce.coupon.model.Coupon;
import com.geecommerce.coupon.model.CouponAction;
import com.geecommerce.coupon.model.CouponCode;
import com.geecommerce.coupon.model.CouponCodeGeneration;
import com.geecommerce.coupon.model.CouponCodePattern;
import com.geecommerce.coupon.model.CouponFilterAttribute;
import com.geecommerce.coupon.model.CouponFilterOperator;
import com.geecommerce.coupon.model.LabelValue;
import com.geecommerce.coupon.repository.CouponCodes;
import com.geecommerce.coupon.repository.Coupons;
import com.google.inject.Inject;

@Path("/v1/coupons")
public class CouponResource extends AbstractResource {

    private final RestService service;
    private final CouponHelper couponHelper;
    private final AttributeService attributeService;
    private final CouponCodes couponCodes;

    private static final String COUPON_CODES_EXPORT_FILE_BASENAME = "coupon_codes";
    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String REPLACE_REGEX = "[^A-Za-z0-9 ]";

    private SimpleDateFormat exportDateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");

    @Inject
    public CouponResource(RestService service, CouponHelper couponHelper, AttributeService attributeService, CouponCodes couponCodes, Coupons coupons) {
        this.service = service;
        this.couponHelper = couponHelper;
        this.attributeService = attributeService;
        this.couponCodes = couponCodes;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCoupons(@FilterParam Filter filter) {

        // filter.getParams().put(Coupon.Col.DELETED, false);
        return ok(service.get(Coupon.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Coupon getCoupon(@PathParam("id") Id id) {
        return checked(service.get(Coupon.class, id));
    }

    @DELETE
    @Path("{id}")
    public void removeCoupon(@PathParam("id") Id id) {
        Coupon coupon = checked(service.get(Coupon.class, id));
        coupon.setDeleted(true);

        service.update(coupon);
    }

    @PUT
    @Path("{id}/generate/{amount}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void generateCoupon(@PathParam("id") Id id, @PathParam("amount") Integer amount) {
        if (id != null && amount != null) {
            Coupon c = checked(service.get(Coupon.class, id));
            if (c.getCouponCodeGeneration().getAuto() != null && c.getCouponCodeGeneration().getAuto()) {
                CouponCodePattern pattern = service.get(CouponCodePattern.class, c.getCouponCodeGeneration().getPattern());
                List<String> codes = couponHelper.generateCodes(c.getCouponCodeGeneration().getPrefix(), c.getCouponCodeGeneration().getPostfix(), pattern, c.getCouponCodeGeneration().getLength(),
                    amount);
                for (String code : codes) {
                    CouponCode cc = app.getModel(CouponCode.class);
                    cc.belongsTo(c);
                    cc.setCode(code);
                    service.create(cc);
                }
            }
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateCoupon(@PathParam("id") Id id, List<Update> updates) {
        if (id != null) {
            Coupon c = checked(service.get(Coupon.class, id));
            CouponCodeGeneration g = c.getCouponCodeGeneration();
            Update coupon = updates.get(0);
            Update action = updates.get(1);

            // Update generator = null;
            // if(updates.size() == 3)
            // updates.get(2);

            String conditionJson = (String) coupon.getFields().get("condition");
            Map<String, Object> conditionMap = Json.fromJson(conditionJson, HashMap.class);
            coupon.getFields().remove("condition");
            coupon.getFields().put("condition", conditionMap);
            String filterJson = (String) action.getFields().get("filter");
            Map<String, Object> filterMap = Json.fromJson(filterJson, HashMap.class);
            action.getFields().remove("filter");
            action.getFields().put("filter", filterMap);
            String rangeDiscountAmountJson = (String) action.getFields().get("rangeDiscountAmount");
            if (rangeDiscountAmountJson != null && !rangeDiscountAmountJson.isEmpty()) {
                List<Object> rangeDiscountAmountMap = Json.fromJson(rangeDiscountAmountJson, ArrayList.class);
                action.getFields().remove("rangeDiscountAmount");
                action.getFields().put("rangeDiscountAmount", rangeDiscountAmountMap);
            }
            if (action.getFields().get("priceTypeId") != null && action.getFields().get("priceTypeId").equals("")) {
                action.getFields().remove("priceTypeId");
                c.getCouponAction().setPriceTypeId(null);
                // action.getFields().put("priceTypeId", null);
            }
            c.fromMap(coupon.getFields());
            c.setId(id);
            c.getCouponAction().set(action.getFields());
            couponHelper.fixCouponFilters(c);
            c.setCouponCodeGeneration(g);
            service.update(c);

            /*
             * if(generator != null){ CouponCodeGeneration generation =
             * app.getModel(CouponCodeGeneration.class);
             * generation.set(generator.getFields()); if(generation.getAuto() !=
             * null && generation.getAuto()){ CouponCodePattern pattern =
             * service.get(CouponCodePattern.class, g.getPattern());
             * List<String> codes = couponHelper.generateCodes(g.getPrefix(),
             * g.getPostfix(),
             * pattern, g.getLength(), generation.getQuantity()); for(String
             * code: codes){ CouponCode cc = app.getModel(CouponCode.class);
             * cc.belongsTo(c); cc.setCode(code); service.create(cc); } } }
             */
        }
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createCoupon(List<Update> updates) {
        Coupon c = app.getModel(Coupon.class);
        Update coupon = updates.get(0);
        Update action = updates.get(1);
        Update generator = updates.get(2);

        String conditionJson = (String) coupon.getFields().get("condition");
        Map<String, Object> conditionMap = Json.fromJson(conditionJson, HashMap.class);
        coupon.getFields().remove("condition");
        coupon.getFields().put("condition", conditionMap);

        String filterJson = (String) action.getFields().get("filter");
        Map<String, Object> filterMap = Json.fromJson(filterJson, HashMap.class);
        action.getFields().remove("filter");
        action.getFields().put("filter", filterMap);

        String rangeDiscountAmountJson = (String) action.getFields().get("rangeDiscountAmount");
        if (rangeDiscountAmountJson != null && !rangeDiscountAmountJson.isEmpty()) {
            List<Object> rangeDiscountAmountMap = Json.fromJson(rangeDiscountAmountJson, ArrayList.class);
            action.getFields().remove("rangeDiscountAmount");
            action.getFields().put("rangeDiscountAmount", rangeDiscountAmountMap);
        }

        if (action.getFields().get("priceTypeId") != null && action.getFields().get("priceTypeId").equals("")) {
            action.getFields().remove("priceTypeId");
            // c.getCouponAction().setPriceTypeId(null);
        }

        c.fromMap(coupon.getFields());
        c.setCouponAction(app.getModel(CouponAction.class));
        c.getCouponAction().fromMap(action.getFields());
        couponHelper.fixCouponFilters(c);
        c = service.create(c);
        if (c != null) {
            if (c.getAuto()) {
                CouponCode cc = app.getModel(CouponCode.class);
                cc.belongsTo(c);
                service.create(cc);
                // create just one coupon code
            } else {
                CouponCodeGeneration g = app.getModel(CouponCodeGeneration.class);
                g.set(generator.getFields());
                c.setCouponCodeGeneration(g);
                service.update(c);
                if (!g.getAuto()) {
                    CouponCode cc = app.getModel(CouponCode.class);
                    cc.belongsTo(c);
                    cc.setCode(g.getCode());
                    service.create(cc);
                } else {
                    CouponCodePattern pattern = service.get(CouponCodePattern.class, g.getPattern());
                    List<String> codes = couponHelper.generateCodes(g.getPrefix(), g.getPostfix(), pattern, g.getLength(), g.getQuantity());
                    for (String code : codes) {
                        CouponCode cc = app.getModel(CouponCode.class);
                        cc.belongsTo(c);
                        cc.setCode(code);
                        service.create(cc);
                    }
                }
            }
        }

        return created(c);
    }

    @GET
    @Path("{id}/codes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCodes(@PathParam("id") Id id, @FilterParam Filter filter) {
        return ok(checked(service.get(CouponCode.class, filter.append("coupon_id", id).getParams(), queryOptions(filter))));
    }

    @GET
    @Path("/couponCodePatterns")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCouponCodePatterns() {
        return ok(checked(service.get(CouponCodePattern.class, (Map<String, Object>) null)));
    }

    @GET
    @Path("/checkcode/{code}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getIsCodeUnique(@PathParam("code") String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        List<CouponCode> codes = service.get(CouponCode.class, map);
        if (codes.size() == 0) {
            return ok(checked(true));
        }

        return ok(checked(false));
    }

    @GET
    @Path("/productAttributeOptions")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProductAttributeOptions() {
        List<AttributeOption> attributeOptions = service.get(AttributeOption.class, (Map<String, Object>) null);
        return ok(attributeOptions);
    }

    @GET
    @Path("/productAttributes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getProductAttributes() {
        List<LabelValue> result = new ArrayList<>();
        List<Attribute> attributes = attributeService.getAttributesFor(TargetObjectCode.PRODUCT);
        for (Attribute attribute : attributes) {
            if (!attribute.isEnabled())
                break;

            LabelValue lb = new LabelValue();
            if (attribute.getFrontendLabel() != null) {
                lb.setLabel(attribute.getFrontendLabel().getVal());
            } else if (attribute.getBackendLabel() != null) {
                lb.setLabel(attribute.getBackendLabel().getVal());
            } else {
                lb.setLabel(attribute.getCode());
            }
            lb.setValue(attribute.getCode());
            lb.setHasOptions(attribute.isOptionAttribute());
            lb.setFrontendInput(attribute.getFrontendInput());
            lb.setId(attribute.getId());
            result.add(lb);
        }

        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponFilterAttribute.Column.TYPE, CouponFilterAttributeType.PRODUCT.toId());
        List<CouponFilterAttribute> dynAttributes = service.get(CouponFilterAttribute.class, filter);
        for (CouponFilterAttribute attribute : dynAttributes) {
            LabelValue lb = new LabelValue();
            lb.setLabel(attribute.getName().getVal());
            lb.setValue(attribute.getCode());
            result.add(lb);
            lb.setFrontendInput(FrontendInput.TEXT);
        }

        Collections.sort(result, new Comparator<LabelValue>() {
            @Override
            public int compare(LabelValue o1, LabelValue o2) {
                if (o1.getLabel() != null && o2.getLabel() != null)
                    return o1.getLabel().compareTo(o2.getLabel());

                if (o1.getLabel() != null)
                    return 1;

                if (o2.getLabel() != null)
                    return -1;
                return 0;
            }
        });

        return ok(result);
    }

    @GET
    @Path("/cartAttributes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCartAttributes() {
        List<LabelValue> result = new ArrayList<>();

        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponFilterAttribute.Column.TYPE, CouponFilterAttributeType.CART.toId());
        List<CouponFilterAttribute> dynAttributes = service.get(CouponFilterAttribute.class, filter);
        for (CouponFilterAttribute attribute : dynAttributes) {
            LabelValue lb = new LabelValue();
            lb.setLabel(attribute.getName().getVal());
            lb.setValue(attribute.getCode());
            lb.setFrontendInput(FrontendInput.TEXT);
            result.add(lb);
        }
        return ok(result);
    }

    @GET
    @Path("/cartItemAttributes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getCartItemAttributes() {
        List<LabelValue> result = new ArrayList<>();

        Map<String, Object> filter = new HashMap<>();
        filter.put(CouponFilterAttribute.Column.TYPE, CouponFilterAttributeType.CART_ITEM.toId());
        List<CouponFilterAttribute> dynAttributes = service.get(CouponFilterAttribute.class, filter);
        for (CouponFilterAttribute attribute : dynAttributes) {
            LabelValue lb = new LabelValue();
            lb.setLabel(attribute.getName().getVal());
            lb.setValue(attribute.getCode());
            lb.setFrontendInput(FrontendInput.TEXT);
            result.add(lb);
        }
        return ok(result);
    }

    @GET
    @Path("/couponActionTypes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response geCouponActionTypes() {
        ArrayList<CouponActionType> types = new ArrayList<CouponActionType>(Arrays.asList(CouponActionType.values()));
        List<LabelValue> result = new ArrayList<>();

        for (CouponActionType type : types) {
            LabelValue lb = new LabelValue();
            lb.setLabel(type.getLabel());
            lb.setValue(type);
            result.add(lb);
        }
        return ok(result);
    }

    @GET
    @Path("/couponFilterNodeTypes")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response geCouponFilterNodeTypes() {
        ArrayList<CouponFilterNodeType> types = new ArrayList<CouponFilterNodeType>(Arrays.asList(CouponFilterNodeType.values()));
        List<LabelValue> result = new ArrayList<>();

        for (CouponFilterNodeType type : types) {
            LabelValue lb = new LabelValue();
            lb.setLabel(type.getLabel());
            lb.setValue(type);
            result.add(lb);
        }
        return ok(result);
    }

    @GET
    @Path("/couponFilterOperators")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response geCouponFilterOperators() {
        List<LabelValue> result = new ArrayList<>();

        List<CouponFilterOperator> operators = service.get(CouponFilterOperator.class, (Map<String, Object>) null);
        for (CouponFilterOperator operator : operators) {
            LabelValue lb = new LabelValue();
            lb.setLabel(operator.getName().getVal());
            lb.setValue(operator.getOperator());
            result.add(lb);
        }
        return ok(result);
    }

    @GET
    @Path("{id}/export/codes")
    public Response getCouponsExport(@PathParam("id") Id id, @Context HttpServletResponse response, @QueryParam("mark") Boolean mark, @QueryParam("qty") Integer qty,
        @QueryParam("exportMarkedAndUsed") Boolean exportMarkedAndUsed) throws IOException {
        Coupon c = checked(service.get(Coupon.class, id));
        String couponName = Str.EMPTY;

        if (c.getName() != null && c.getName().str() != null) {

            couponName = Strings.transliterate(c.getName().str());
            couponName = couponName.replaceAll(REPLACE_REGEX, Str.EMPTY).replace(Str.DOUBLE_SPACE, Str.SPACE).replace(Char.SPACE, Char.UNDERSCORE).toLowerCase();
        }

        String baseName = new StringBuilder(COUPON_CODES_EXPORT_FILE_BASENAME).append(couponName == null ? Str.EMPTY : Char.UNDERSCORE).append(couponName).append(Char.UNDERSCORE)
            .append(exportDateFormat.format(new Date())).toString();

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + baseName + ".zip\"");

        ZipOutputStream zos = null;
        PrintWriter pw = null;
        Integer maxCount = qty == null ? Integer.MAX_VALUE : qty;
        Date exportDate = new Date();
        try {
            zos = new ZipOutputStream(response.getOutputStream());
            pw = new PrintWriter(zos);

            zos.putNextEntry(new ZipEntry(new StringBuilder(baseName).append(CSV_FILE_EXTENSION).toString()));

            pw.write("\"Code\";\"Email\";\"Used\";\"Exported\"");
            pw.write(Char.NEWLINE);
            pw.flush();

            List<CouponCode> couponCodeList = couponCodes.thatBelongTo(c);
            if (couponCodeList != null) {
                for (CouponCode couponCode : couponCodeList) {
                    if ((couponCode.getExportedDate() == null && couponCode.getEmail() == null && !isCouponUsed(couponCode)) || (exportMarkedAndUsed != null && exportMarkedAndUsed)) {

                        if (maxCount == 0)
                            break;

                        String code = couponCode.getCode();
                        String email = couponCode.getEmail() == null ? Str.EMPTY : couponCode.getEmail();
                        String used = couponCode.getCouponUsages() != null && couponCode.getCouponUsages().size() > 0 ? couponCode.getCouponUsages().get(0).getUsageDate().toString() : "";
                        String exported = couponCode.getExportedDate() != null ? couponCode.getExportedDate().toString() : "";

                        // Code
                        pw.write(Char.DOUBLE_QUOTE);
                        pw.write(code);
                        pw.write(Char.DOUBLE_QUOTE);
                        pw.write(Char.SEMI_COLON);
                        // Email
                        pw.write(Char.DOUBLE_QUOTE);
                        pw.write(email);
                        pw.write(Char.DOUBLE_QUOTE);
                        pw.write(Char.SEMI_COLON);
                        // Used
                        pw.write(used);
                        pw.write(Char.SEMI_COLON);
                        // Exported
                        pw.write(exported);

                        pw.write(Char.NEWLINE);
                        pw.flush();

                        if (mark != null && mark && (exportMarkedAndUsed == null || !exportMarkedAndUsed)) {
                            couponCode.setExportedDate(exportDate);
                            service.update(couponCode);
                        }
                        maxCount--;
                    }
                }
            }

            zos.closeEntry();
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        } finally {
            IOUtils.closeQuietly(pw);
            IOUtils.closeQuietly(zos);
        }

        return Response.ok().build();
    }

    private boolean isCouponUsed(CouponCode couponCode) {
        if (couponCode.getCouponUsages() != null && couponCode.getCouponUsages().size() > 0)
            return true;
        return false;
    }
}
