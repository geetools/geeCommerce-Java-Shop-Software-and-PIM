package com.geecommerce.guiwidgets.rest.v1;

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
import com.geecommerce.core.type.Id;
import com.geecommerce.guiwidgets.model.Slide;
import com.geecommerce.guiwidgets.model.WebSlideShow;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/slide-shows")
public class SlideShowResource extends AbstractResource {
    private final RestService service;
    private final MediaAssetService mediaAssetService;

    @Inject
    public SlideShowResource(RestService service, MediaAssetService mediaAssetService) {
        this.service = service;
        this.mediaAssetService = mediaAssetService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getSlideShows(@FilterParam Filter filter) {
        return ok(service.get(WebSlideShow.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public WebSlideShow getSlideShow(@PathParam("id") Id id) {
        try {
            return checked(service.get(WebSlideShow.class, id));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    @DELETE
    @Path("{id}")
    public void removeSlideshow(@PathParam("id") Id id) {
        WebSlideShow slideshow = checked(service.get(WebSlideShow.class, id));

        System.out.println("--- Removing slideshow: " + slideshow);
        service.remove(slideshow);

    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateSlideShow(@PathParam("id") Id id, Update update) {
        WebSlideShow slideShow = checked(service.get(WebSlideShow.class, id));
        slideShow.set(update.getFields());
        service.update(slideShow);

    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createSlideShow(Update update) {
        WebSlideShow slideShow = app.model(WebSlideShow.class);
        slideShow.set(update.getFields());
        slideShow = service.create(slideShow);
        return created(slideShow);
    }

    @PUT
    @Path("{id}/slides/positions")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateSlidesPositions(@PathParam("id") Id id, HashMap<String, Integer> positionsMap) {
        if (id != null && positionsMap != null && positionsMap.size() > 0) {
            WebSlideShow slideShow = checked(service.get(WebSlideShow.class, id));

            Set<String> keys = positionsMap.keySet();

            for (String key : keys) {
                Id slideId = Id.valueOf(key);
                Integer pos = positionsMap.get(key);

                for (Slide slide : slideShow.getSlides()) {
                    if (slide.getId().equals(slideId)) {
                        slide.setPosition(pos);
                    }
                }
            }
            service.update(slideShow);
        }
    }

    @POST
    @Path("{id}/slides")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response newSlide(@PathParam("id") Id id, @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        Slide slide = null;

        if (id != null) {
            // Get product and image.
            FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
            MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());

            slide = app.model(Slide.class);
            slide.setMediaAsset(newMediaAsset);
            slide.setPosition(99);
            slide.setId(app.nextId());

            saveSlide(slide, id);

        } else {
            throwBadRequest("SlideShowId cannot be null in requestURI. Expecting: slide-shows/{id}/slides");
        }

        if (slide == null || slide.getId() == null) {
            throwInternalServerError();
        }

        return ok(slide);
    }

    private void saveSlide(Slide slide, Id slideId) {
        int cnt = 0;
        while (true) {
            try {
                WebSlideShow slideShow = checked(service.get(WebSlideShow.class, slideId));
                slideShow.getSlides().add(slide);
                service.update(slideShow);
                return;
            } catch (Exception ex) {
                cnt++;
            }
        }
    }

    @DELETE
    @Path("{id}/slides/{slideId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeSlide(@PathParam("id") Id id, @PathParam("slideId") Id slideId) {
        if (id != null && slideId != null) {
            // Get product and media-asset.
            WebSlideShow slideShow = checked(service.get(WebSlideShow.class, id));

            Slide forRemove = null;
            for (Slide slide : slideShow.getSlides()) {
                if (slide.getId().equals(slideId)) {
                    forRemove = slide;
                    break;
                }
            }

            if (forRemove != null) {
                slideShow.getSlides().remove(forRemove);
                mediaAssetService.remove(forRemove.getMediaAsset());
            }

            service.update(slideShow);
        } else {
            throwBadRequest(
                "SlideShowId and slideId cannot be null in requestURI. Expecting: slide-shows/{id}/slides/{slideId}");
        }
    }

    @PUT
    @Path("{id}/slides")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateSlides(@PathParam("id") Id id, List<Update> updates) {
        WebSlideShow slideShow = checked(service.get(WebSlideShow.class, id));

        if (updates != null && updates.size() > 0) {
            for (Update update : updates) {
                if (update != null && update.getId() != null && update.getFields() != null
                    && update.getFields().size() > 0) {
                    for (Slide slide : slideShow.getSlides()) {
                        if (slide.getId().equals(update.getId())) {
                            slide.set(update.getFields());
                        }
                    }
                }
            }
            service.update(slideShow);
        }
    }

    @POST
    @Path("{id}/slides/{slideId}/files")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response newFile(@PathParam("id") Id id, @PathParam("slideId") Id slideId,
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart) {
        Slide slide = null;

        if (id != null && slideId != null) {
            // Get product and image.
            WebSlideShow slideShow = checked(service.get(WebSlideShow.class, id));

            slide = slideShow.getSlides().stream().filter(item -> item.getId().equals(slideId)).findFirst().get();

            FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();

            MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());
            slide.setLinkedMediaAsset(newMediaAsset);
            slide.setLink(null);
            service.update(slideShow);
        } else {
            throwBadRequest(
                "SlideShowId and slideId cannot be null in requestURI. Expecting: slide-shows/{id}/slides/{slideId}/files");
        }

        if (slide == null || slide.getId() == null) {
            throwInternalServerError();
        }

        return ok(slide);
    }
}
