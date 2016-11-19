package com.geecommerce.vacancy.rest.v1;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.repository.MediaAssets;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.geecommerce.vacancy.model.Vacancy;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/vacancies")
public class VacancyResource extends AbstractResource {
    private final RestService service;
    private final MediaAssetService mediaAssetService;
    private final MediaAssets mediaAssets;

    @Inject
    public VacancyResource(RestService service, MediaAssetService mediaAssetService, MediaAssets mediaAssets) {
        this.service = service;
        this.mediaAssetService = mediaAssetService;
        this.mediaAssets = mediaAssets;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getVacancies(@FilterParam Filter filter) {
        QueryOptions queryOptions = queryOptions(filter);
        return ok(service.get(Vacancy.class, filter.getParams(), queryOptions));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Vacancy getVacancy(@PathParam("id") Id id) {
        return checked(service.get(Vacancy.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createVacancy(Update update) {
        Vacancy model = app.getModel(Vacancy.class);
        model.set(update.getFields());
        model.setAttributes(update.getAttributes());
        model.setOptionAttributes(update.getOptions());

        model = service.create(model);

        return created(model);
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateVacancy(@PathParam("id") Id id, Update update) {
        if (id != null) {
            Vacancy v = checked(service.get(Vacancy.class, id));
            v.set(update.getFields());
            v.setAttributes(update.getAttributes());
            v.setOptionAttributes(update.getOptions());
            service.update(v);
        }
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeVacancy(@PathParam("id") Id id) {
        if (id != null) {
            Vacancy vacancy = checked(service.get(Vacancy.class, id));

            if (vacancy != null) {
                service.remove(vacancy);
            }
        }
    }

    @POST
    @Path("{id}/file")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response createFile(@PathParam("id") Id id, @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        // Get product and image.
        FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
        MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());

        // VacancyGroup vacancyGroup = service.get(VacancyGroup.class, id);
        // vacancyGroup.setImage(newMediaAsset);

        // service.update(vacancyGroup);
        return ok(newMediaAsset);
    }

    @POST
    @Path("file")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response createFile(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        // Get product and image.
        FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
        MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());

        return ok(newMediaAsset);
    }
}
