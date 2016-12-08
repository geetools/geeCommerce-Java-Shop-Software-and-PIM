package com.geecommerce.core.batch.dataimport.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.geecommerce.core.batch.dataimport.helper.ImportHelper;
import com.geecommerce.core.batch.dataimport.model.ImportProfile;
import com.geecommerce.core.batch.dataimport.model.ImportToken;
import com.geecommerce.core.batch.dataimport.repository.ImportTokens;
import com.geecommerce.core.batch.service.ImportExportService;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/import")
public class ImportResource extends AbstractResource {
    protected final RestService service;
    protected final AttributeService attributeService;
    protected final ImportExportService importExportService;
    protected final ImportHelper importHelper;
    protected final ImportTokens importTokens;

    @Inject
    public ImportResource(RestService service, AttributeService attributeService, ImportExportService importExportService, ImportHelper importHelper, ImportTokens importTokens) {
        this.service = service;
        this.attributeService = attributeService;
        this.importExportService = importExportService;
        this.importHelper = importHelper;
        this.importTokens = importTokens;
    }

    @GET
    @Path("/profiles/{token:[a-z0-9\\-]+}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ImportProfile getImportProfile(@PathParam("token") String token) {
        return importExportService.getImportProfile(token);
    }

    @GET
    @Path("/profiles/{id:[0-9]+}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ImportProfile getImportProfile(@PathParam("id") Id id) {
        return importExportService.getImportProfile(id);
    }

    @POST
    @Path("/files/{obj_type}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public String newImportFile(@PathParam("obj_type") String targetObjCode, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
        String mimeType = MimeType.fromFilename(fileDetails.getFileName());

        System.out.println(fileDetails.getFileName() + " -> " + mimeType + " -> " + fileDetails.getName());

        ImportToken importToken = null;

        try {
            String uploadedFilePath = importHelper.saveFile(uploadedInputStream, fileDetails.getFileName());

            AttributeTargetObject targetObj = attributeService.getAttributeTargetObjectByCode(targetObjCode);

            importToken = importTokens.add(app.model(ImportToken.class)
                .setToken(UUID.randomUUID().toString())
                .setFilePath(uploadedFilePath)
                .setTargetObjectId(targetObj.getId()));

            if (importHelper.isZipFile(uploadedFilePath)) {
                final String token = importToken.getToken();

                importHelper.unpack(uploadedFilePath, false, () -> {
                    Set<String> headers = importHelper.fetchHeaders(uploadedFilePath);

                    // First attempt to create default profile automatically.
                    importExportService.newDefaultImportProfile(headers, targetObj, token);

                    System.out.println("GOT HEADERS ::::: " + headers);

                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_id2").setScript("((Product)
                    // obj).id2 = data['_id2']").setTargetObjectId(new Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_ean").setScript("((Product)
                    // obj).ean = data['_ean']").setTargetObjectId(new Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_merchant").setScript("").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_store").setScript("").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_request_context").setScript("").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_language").setScript("").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_type").setScript("((Product)
                    // obj).type = data['_type']").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_saleable").setScript("((Product)
                    // obj).saleable = data['_saleable']").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_visible").setScript("((Product)
                    // obj).visible = data['_visible']").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_visible_from").setScript("((Product)
                    // obj).visibleFrom =
                    // data['_visible_from']").setTargetObjectId(new Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_visible_to").setScript("((Product)
                    // obj).visibleTo =
                    // data['_visible_to']").setTargetObjectId(new Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_visible_in_product_list").setScript("((Product)
                    // obj).visibleInProductList =
                    // data['_visible_in_product_list']").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_parent_id").setScript("((Product)
                    // obj).parentId =
                    // data['_parent_id']").setTargetObjectId(new Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_variant_id").setScript("((Product)
                    // obj).addVariant(products.findById(Id.valueOf(data['_variant_id'])))").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_upsell_id").setScript("((Product)
                    // obj).addUpsellProduct(products.findById(Id.valueOf(data['_upsell_id'])))").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_crosssell_id").setScript("((Product)
                    // obj).addCrossSellProduct(products.findById(Id.valueOf(data['_crosssell_id'])))").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_bundle_product_id").setScript("((Product)
                    // obj).addBundleProduct(products.findById(Id.valueOf(data['_bundle_product_id'])))").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_programme_product_id").setScript("((Product)
                    // obj).addProgrammeProduct(products.findById(Id.valueOf(data['_programme_product_id'])))").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_include_in_feeds").setScript("").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_price").setScript("").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_qty").setScript("").setTargetObjectId(new
                    // Id(2)));
                    // importExportService.createImportFieldScript(app.model(ImportFieldScriptlet.class).setFieldName("_uri").setScript("").setTargetObjectId(new
                    // Id(2)));

                    return null;
                });
            } else {
                String token = importToken.getToken();
                
                Set<String> headers = importHelper.fetchHeaders(uploadedFilePath);

                // First attempt to create default profile automatically.
                ImportProfile importProfile = importExportService.newDefaultImportProfile(headers, targetObj, token);

                System.out.println("GOT HEADERS ::::: " + headers);
                
                importHelper.createImportPlan(uploadedFilePath, importProfile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return importToken == null ? "" : importToken.getToken();
    }

}
