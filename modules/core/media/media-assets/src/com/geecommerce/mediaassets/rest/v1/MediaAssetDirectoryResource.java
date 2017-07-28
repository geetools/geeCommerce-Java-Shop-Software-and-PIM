package com.geecommerce.mediaassets.rest.v1;

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
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAssetDirectory;
import com.geecommerce.mediaassets.repository.MediaAssetDirectories;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.swagger.annotations.Api;

@Api
@Singleton
@Path("/v1/media-asset-directories")
public class MediaAssetDirectoryResource extends AbstractResource {
    private final RestService service;
    private final MediaAssetDirectories mediaAssetDirectories;

    @Inject
    public MediaAssetDirectoryResource(RestService service, MediaAssetDirectories mediaAssetDirectories) {
        this.service = service;
        this.mediaAssetDirectories = mediaAssetDirectories;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMediaAssetDirectories(@FilterParam Filter filter) {
        return ok(service.get(MediaAssetDirectory.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("root")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRootMediaAssetDirectory() {
        return ok(mediaAssetDirectories.havingParent(null));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MediaAssetDirectory getMediaAssetDirectory(@PathParam("id") Id id) {
        return checked(service.get(MediaAssetDirectory.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createMediaAssetDirectory(@ModelParam MediaAssetDirectory mediaAssetDirectory) {
        /* setAttributeCode(attribute); */
        return created(service.create(mediaAssetDirectory));
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeMediaAssetDirectory(@PathParam("id") Id id) {
        if (id != null) {
            MediaAssetDirectory directory = service.get(MediaAssetDirectory.class, id);

            if (directory != null) {
                service.remove(directory);
            }
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MediaAssetDirectory updateMediaAssetDirectory(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            MediaAssetDirectory directory = service.get(MediaAssetDirectory.class, id);

            directory.set(update.getFields());
            directory.putAttributes(update.getAttributes());
            directory.setOptionAttributes(update.getOptions());

            service.update(directory);
        }
        return checked(service.get(MediaAssetDirectory.class, id));
    }

}
