package com.geecommerce.core.template.rest.v1;

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
import com.geecommerce.core.service.CopySupport;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.template.model.Template;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

import io.swagger.annotations.Api;

@Api
@Path("/v1/templates")
public class TemplateResource extends AbstractResource {
    private final RestService service;

    @Inject
    public TemplateResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getTemplates(@FilterParam Filter filter) {
        QueryOptions queryOptions = queryOptions(filter);

        if (storeHeaderExists())
            queryOptions = QueryOptions.builder(queryOptions).build();

        return ok(service.get(Template.class, filter.getParams(), queryOptions));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Template getTemplate(@PathParam("id") Id id) {
        return checked(service.get(Template.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createURLRewrite(@ModelParam Template template) {
        return created(service.create(template));
    }

    @DELETE
    @Path("{id}")
    public void removeProductPromotion(@PathParam("id") Id id) {
        Template tmp = checked(service.get(Template.class, id));
        service.remove(tmp);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateTemplate(@PathParam("id") Id id, Update update) {
        if (id != null && update != null) {
            Template tmp = checked(service.get(Template.class, id));

            if (update.isSaveAsNewCopy()) {
                Template copy = ((CopySupport<Template>) tmp).makeCopy();
                copy.set(update.getFields());
                copy.setMerchantIds(update.getMerchantIds());
                copy.setStoreIds(update.getStoreIds());
                copy.setRequestContextIds(update.getRequestContextIds());
                copy = service.create(copy);

                return ok(copy);
            } else {
                tmp.set(update.getFields());
                tmp.setMerchantIds(update.getMerchantIds());
                tmp.setStoreIds(update.getStoreIds());
                tmp.setRequestContextIds(update.getRequestContextIds());
                service.update(tmp);
            }

            return ok(tmp);
        }

        return notFound();
    }

}

/*
 if (update.isSaveAsNewCopy()) {
                Product copy = ((CopySupport<Product>) p).makeCopy();

                copy.set(update.getFields());
                copy.putAttributes(update.getAttributes());
                copy.setOptionAttributes(update.getOptions());
                copy.setXOptionAttributes(update.getXOptions());
                copy.setOptOuts(update.getOptOuts());
                copy.setMerchantIds(update.getMerchantIds());
                copy.setStoreIds(update.getStoreIds());
                copy.setRequestContextIds(update.getRequestContextIds());

                setStatuses(copy);

                copy = service.create(copy);

                updateRewriteURL(copy.getId(), update);
                copy.setURI(null);

                return copy.getId();
            } else {
                p.set(update.getFields());
                p.putAttributes(update.getAttributes());
                p.setOptionAttributes(update.getOptions());
                p.setXOptionAttributes(update.getXOptions());
                p.setOptOuts(update.getOptOuts());
                p.setMerchantIds(update.getMerchantIds());
                p.setStoreIds(update.getStoreIds());
                p.setRequestContextIds(update.getRequestContextIds());

                setStatuses(p);

                service.update(p);

                updateRewriteURL(p.getId(), update);
                p.setURI(null);

                return p.getId();
            }
* */
