package com.geecommerce.vacancy.rest.v1;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import com.geecommerce.vacancy.model.VacancyGroup;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/vacancy-groups")
public class VacancyGroupResource extends AbstractResource {
    private final RestService service;
    private final MediaAssetService mediaAssetService;
    private final MediaAssets mediaAssets;

    @Inject
    public VacancyGroupResource(RestService service, MediaAssetService mediaAssetService, MediaAssets mediaAssets) {
        this.service = service;
        this.mediaAssetService = mediaAssetService;
        this.mediaAssets = mediaAssets;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getVacancyGroups(@FilterParam Filter filter) {
        QueryOptions queryOptions = queryOptions(filter);
        return ok(service.get(VacancyGroup.class, filter.getParams(), queryOptions));
    }

    @GET
    @Path("{groupId}/vacancies")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getVacancies(@PathParam("groupId") Id groupId) {
        Filter filter = new Filter();
        filter.getParams().put("vacancyGroupId", groupId);
        List<Vacancy> vacancies = service.get(Vacancy.class, filter.getParams());
        return ok(vacancies);
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public VacancyGroup getVacancyGroup(@PathParam("id") Id id) {
        return checked(service.get(VacancyGroup.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createVacancyGroup(Update update) {
        VacancyGroup model = app.model(VacancyGroup.class);
        model.set(update.getFields());
        model = service.create(model);

        return created(model);
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateVacancyGroup(@PathParam("id") Id id, Update update) {
        if (id != null) {
            VacancyGroup vg = checked(service.get(VacancyGroup.class, id));
            vg.set(update.getFields());
            service.update(vg);
        }
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeVacancyGroup(@PathParam("id") Id id) {
        if (id != null) {
            VacancyGroup vacancyGroup = checked(service.get(VacancyGroup.class, id));

            if (vacancyGroup != null) {
                service.remove(vacancyGroup);
            }
        }
    }

    @PUT
    @Path("{id}/vacancies")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateVacancies(@PathParam("id") Id id, List<Update> updates) {
        VacancyGroup vacancyGroup = checked(service.get(VacancyGroup.class, id));

        if (updates == null || updates.size() == 0)
            return;

        for (Update update : updates) {
            if (update != null && update.getFields() != null && update.getFields().size() > 0) {
                if (update.getId() == null) {
                    Vacancy v = app.model(Vacancy.class);
                    v.set(update.getFields());
                    v.setVacancyGroupId(id);
                    service.create(v);

                } else {
                    for (Vacancy vacancy : vacancyGroup.getVacancyList()) {
                        if (vacancy.getId().equals(update.getId())) {
                            vacancy.set(update.getFields());
                            service.update(vacancy);
                        }
                    }
                }
            }
        }
    }

    @PUT
    @Path("{id}/vacancies/positions")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateVacanciesPositions(@PathParam("id") Id id, HashMap<String, Integer> positionsMap) {
        if (id == null || positionsMap == null || positionsMap.size() == 0)
            return;

        VacancyGroup vacancyGroup = checked(service.get(VacancyGroup.class, id));

        Set<String> keys = positionsMap.keySet();

        for (String key : keys) {
            Id vacancyId = Id.valueOf(key);
            Integer pos = positionsMap.get(key);

            for (Vacancy vacancy : vacancyGroup.getVacancyList()) {
                if (vacancy.getId().equals(vacancyId)) {
                    vacancy.setPosition(pos);
                    service.update(vacancy);
                }
            }
        }

    }

    @POST
    @Path("image")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response createImage(@PathParam("id") Id id, @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        // Get product and image.
        FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
        MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());

        // VacancyGroup vacancyGroup = service.get(VacancyGroup.class, id);
        // vacancyGroup.setImage(newMediaAsset);

        // service.update(vacancyGroup);
        return ok(newMediaAsset);
    }
}
