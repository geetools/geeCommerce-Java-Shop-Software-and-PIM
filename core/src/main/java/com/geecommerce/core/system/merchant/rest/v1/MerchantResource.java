package com.geecommerce.core.system.merchant.rest.v1;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.jersey.inject.ModelParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.model.SearchIndex;
import com.geecommerce.core.system.repository.RequestContexts;
import com.geecommerce.core.system.repository.SearchIndexes;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v1/merchants")
public class MerchantResource extends AbstractResource {
    private final RestService service;
    private final RequestContexts requestContexts;
    private final SearchIndexes searchIndexes;

    @Inject
    public MerchantResource(RestService service, RequestContexts requestContexts, SearchIndexes searchIndexes) {
        this.service = service;
        this.requestContexts = requestContexts;
        this.searchIndexes = searchIndexes;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMerchants(@FilterParam Filter filter)
    {
        return ok(service.get(Merchant.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Merchant getMerchant(@PathParam("id") Id id)
    {
        return checked(service.get(Merchant.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createMerchant(@ModelParam Merchant merchant)
    {
        return created(service.create(merchant));
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateMerchant(@PathParam("id") Id id, Update update)
    {
        if (id != null && update != null)
        {
            Merchant mr = checked(service.get(Merchant.class, id));
            mr.set(update.getFields());
            service.update(mr);

            return ok(mr);
        }

        return notFound();
    }

    @DELETE
    @Path("{id}/views/{viewId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeView(@PathParam("id") Id id, @PathParam("viewId") Id viewId) {
        if (id != null && viewId != null) {
            // Get product and media-asset.
            Merchant merchant = checked(service.get(Merchant.class, id));

            View forRemove = merchant.getView(viewId);

            if(forRemove != null){
                merchant.getViews().remove(forRemove);
            }

            service.update(merchant);
        } else {
            throwBadRequest(
                    "merchantId and viewId cannot be null in requestURI. Expecting: merchants/{id}/views/{viewId}");
        }
    }


    @DELETE
    @Path("{id}/stores/{storeId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeStore(@PathParam("id") Id id, @PathParam("storeId") Id storeId) {
        if (id != null && storeId != null) {
            // Get product and media-asset.
            Merchant merchant = checked(service.get(Merchant.class, id));

            Store forRemove = merchant.getStore(storeId);

            if(forRemove != null){
                merchant.getStores().remove(forRemove);
            }

            service.update(merchant);
        } else {
            throwBadRequest(
                    "merchantId and storeId cannot be null in requestURI. Expecting: merchants/{id}/stores/{storeId}");
        }
    }

    @PUT
    @Path("{id}/views")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Merchant saveViews(@PathParam("id") Id id, List<Update> updates) {
        Merchant merchant = checked(service.get(Merchant.class, id));

        if (updates != null && updates.size() > 0) {
            for (Update update : updates) {
                if (update != null && update.getId() != null && update.getFields() != null
                        && update.getFields().size() > 0) {
                    View view = merchant.getView(update.getId());
                    view.set(update.getFields());
                } else if(update.getId() == null && update.getFields() != null
                        && update.getFields().size() > 0){
                    View view = app.model(View.class);
                    view.belongsTo(merchant);
                    view.set(update.getFields());
                    view.setId(app.nextId());
                    merchant.getViews().add(view);
                }
            }
            service.update(merchant);
        }
        return merchant;
    }

    @PUT
    @Path("{id}/stores")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Merchant saveStores(@PathParam("id") Id id, List<Update> updates) {
        Merchant merchant = checked(service.get(Merchant.class, id));

        if (updates != null && updates.size() > 0) {
            for (Update update : updates) {
                if (update != null && update.getId() != null && update.getFields() != null
                        && update.getFields().size() > 0) {
                    Store store = merchant.getStore(update.getId());
                    store.set(update.getFields());
                } else if(update.getId() == null && update.getFields() != null
                        && update.getFields().size() > 0){
                    Store store = app.model(Store.class);
                    store.belongsTo(merchant);
                    store.set(update.getFields());
                    store.setId(app.nextId());
                    merchant.getStores().add(store);
                }
            }
            service.update(merchant);
        }
        return merchant;
    }

    @PUT
    @Path("{id}/request-contexts")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response saveRequestContexts(@PathParam("id") Id id, List<Update> updates) {
        Merchant merchant = checked(service.get(Merchant.class, id));

        if (updates != null && updates.size() > 0) {
            for (Update update : updates) {
                if (update != null && update.getId() != null && update.getFields() != null
                        && update.getFields().size() > 0) {
                    RequestContext requestContext = service.get(RequestContext.class, update.getId());
                    requestContext.set(update.getFields());
                    service.update(requestContext);
                } else if(update.getId() == null && update.getFields() != null
                        && update.getFields().size() > 0){
                    RequestContext requestContext = app.model(RequestContext.class);
                    requestContext.set(update.getFields());
                    requestContext = service.create(requestContext);

                    SearchIndex searchIndex = app.model(SearchIndex.class);
                    searchIndex.setMerchantId(requestContext.getMerchantId());
                    searchIndex.setStoreId(requestContext.getStoreId());
                    searchIndex.setRequestContextId(requestContext.getId());
                    searchIndex.setEnabled(true);
                    service.create(searchIndex);
                }
            }
        }
        return ok(requestContexts.forMerchant(merchant));
    }

    @GET
    @Path("{id}/request-contexts")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRequestContexts(@PathParam("id") Id id, @FilterParam Filter filter)
    {
        Merchant merchant = checked(service.get(Merchant.class, id));
        return ok(requestContexts.forMerchant(merchant));
    }

    @DELETE
    @Path("{id}/request-contexts/{requestContextId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeRequestContext(@PathParam("id") Id id, @PathParam("requestContextId") Id requestContextId) {
        if (id != null && requestContextId != null) {
            // Get product and media-asset.
            RequestContext requestContext = requestContexts.findById(RequestContext.class, requestContextId);

            if(requestContext != null) {
                SearchIndex searchIndex = searchIndexes.forValues(requestContext.getMerchantId(), requestContext.getStoreId(), requestContext.getId());

                if(searchIndex != null){
                    searchIndexes.remove(searchIndex);
                }

                requestContexts.remove(requestContext);
            }

        } else {
            throwBadRequest(
                    "merchantId and requestContextId cannot be null in requestURI. Expecting: merchants/{id}/request-contexts/{requestContextId}");
        }
    }

}
