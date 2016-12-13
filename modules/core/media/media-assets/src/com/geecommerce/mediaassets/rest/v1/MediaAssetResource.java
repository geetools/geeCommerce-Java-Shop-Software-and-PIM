package com.geecommerce.mediaassets.rest.v1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.elasticsearch.api.search.SearchResult;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.elasticsearch.service.ElasticsearchService;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.model.MediaAssetDirectory;
import com.geecommerce.mediaassets.repository.MediaAssets;
import com.geecommerce.mediaassets.service.MediaAssetDirectoryService;
import com.geecommerce.mediaassets.service.MediaAssetService;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/v1/media-assets")
public class MediaAssetResource extends AbstractResource {
    private final RestService service;
    private final MediaAssetService mediaAssetService;
    private final MediaAssetDirectoryService mediaAssetDirectoryService;
    private final MediaAssets mediaAssets;
    private final ElasticsearchService elasticsearchService;
    private final ElasticsearchHelper elasticsearchHelper;
    private final AttributeService attributeService;

    @Inject
    public MediaAssetResource(RestService service, MediaAssetService mediaAssetService,
        MediaAssetDirectoryService mediaAssetDirectoryService, MediaAssets mediaAssets,
        ElasticsearchService elasticsearchService, ElasticsearchHelper elasticsearchHelper,
        AttributeService attributeService) {
        this.service = service;
        this.mediaAssetService = mediaAssetService;
        this.mediaAssetDirectoryService = mediaAssetDirectoryService;
        this.mediaAssets = mediaAssets;
        this.elasticsearchService = elasticsearchService;
        this.elasticsearchHelper = elasticsearchHelper;
        this.attributeService = attributeService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMediaAssets(@FilterParam Filter filter) {
        return ok(mediaAssets.find(MediaAsset.class, filter.getParams(), queryOptions(filter)));
        // service.get(MediaAsset.class, filter.getParams(),
        // queryOptions(filter)));
    }

    @GET
    @Path("{id}/list")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMediaAssets(@FilterParam Filter filter, @PathParam("id") Id directoryId) {
        filter.getParams().put("directory", directoryId);
        return ok(mediaAssets.find(MediaAsset.class, filter.getParams(), queryOptions(filter)));
        // service.get(MediaAsset.class, filter.getParams(),
        // queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MediaAsset getMediaAsset(@PathParam("id") Id id) {
        try {
            return checked(mediaAssetService.get(id));
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return null;
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeMediaAsset(@PathParam("id") Id id) {
        if (id != null) {
            MediaAsset ma = service.get(MediaAsset.class, id);
            if (ma != null) {
                service.remove(ma);
            }
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateMediaAsset(@PathParam("id") Id id, Update update) {
        MediaAsset mediaAsset = checked(mediaAssets.findById(MediaAsset.class, id));
        mediaAsset.set(update.getFields());
        mediaAsset.putAttributes(update.getAttributes());
        mediaAsset.setOptionAttributes(update.getOptions());
        mediaAssets.update(mediaAsset);
    }

    @POST
    @Path("{id}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response createMediaAsset(@FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart, @PathParam("id") Id id) {
        // Get product and image.
        FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
        MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());
        newMediaAsset.setDirectoryId(id);
        mediaAssets.update(newMediaAsset);

        return ok(newMediaAsset);
    }

    @POST
    @Path("/system")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response createSystemMediaAsset(@FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart, @QueryParam("path") String path) {
        FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
        MediaAssetDirectory directory = mediaAssetDirectoryService.createOrGetSystem(path);
        if (directory != null) {
            MediaAsset newMediaAsset = mediaAssetService.create(uploadedInputStream, fileDetails.getFileName());
            newMediaAsset.setDirectoryId(directory.getId());
            mediaAssets.update(newMediaAsset);
            return ok(newMediaAsset);
        }
        return notFound();
    }

    @POST
    @Path("/update-file/{id}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response updateContentMediaAsset(@FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataBodyPart formDataBodyPart, @PathParam("id") Id id) {
        // Get product and image.
        FormDataContentDisposition fileDetails = formDataBodyPart.getFormDataContentDisposition();
        MediaAsset newMediaAsset = mediaAssetService.update(id, uploadedInputStream, fileDetails.getFileName());
        return ok(newMediaAsset);
    }

    @GET
    @Path("/update-file-url/{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateContentFromUrl(@PathParam("id") Id id, @QueryParam("url") String url) throws IOException {
        BufferedImage image = ImageIO.read(new URL(url));
        // Get product and image.
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        MediaAsset newMediaAsset = mediaAssetService.update(id, is, url);
        return ok(newMediaAsset);
    }

    @GET
    @Path("search/{query}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response findProducts(@PathParam("query") String query, @FilterParam Filter filter) {

        SearchParams searchParams = new SearchParams();
        searchParams.setOffset(filter.getOffset() == null ? 0 : filter.getOffset().intValue());
        searchParams.setLimit(filter.getLimit() == null ? 25 : filter.getLimit());

        AttributeTargetObject maTargetObject = attributeService.getAttributeTargetObject(MediaAsset.class);

        if (maTargetObject == null)
            throw new IllegalStateException(
                "Missing attribute target object entry for the type '" + MediaAsset.class.getName() + "'");

        List<Id> attributeTargetObjectIds = new ArrayList<>();
        attributeTargetObjectIds.add(maTargetObject.getId());

        SearchResult searchResult = elasticsearchService.findItems(MediaAsset.class, query, searchParams,
            attributeTargetObjectIds);

        List<MediaAsset> mediaAssetList = null;

        if (searchResult != null && searchResult.getDocumentIds() != null && searchResult.getDocumentIds().size() > 0) {
            QueryOptions queryOptions = queryOptions(filter);

            mediaAssetList = mediaAssets.findByIds(MediaAsset.class,
                elasticsearchHelper.toIds(searchResult.getDocumentIds().toArray()), queryOptions);
        }

        return ok(mediaAssetList);
    }
}
