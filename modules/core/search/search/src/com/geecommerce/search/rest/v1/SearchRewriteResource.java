package com.geecommerce.search.rest.v1;

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
import com.geecommerce.search.model.SearchRewrite;
import com.google.inject.Inject;

@Path("/v1/search-rewrites")
public class SearchRewriteResource extends AbstractResource {

    private final RestService service;

    @Inject
    public SearchRewriteResource(RestService service) {
	this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getSearchRewrites(@FilterParam Filter filter) {
	return ok(service.get(SearchRewrite.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchRewrite getSearchRewrite(@PathParam("id") Id id) {
	return checked(service.get(SearchRewrite.class, id));
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateSearchRewrite(@PathParam("id") Id id, Update update) {
	if (id != null) {
	    SearchRewrite s = checked(service.get(SearchRewrite.class, id));
	    s.set(update.getFields());
	    if (s.getKeywords() != null)
		s.getKeywords().stream().forEach(k -> k.toLowerCase().trim());

	    service.update(s);
	}
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createSearchRewrite(Update update) {
	SearchRewrite s = app.getModel(SearchRewrite.class);
	s.set(update.getFields());
	if (s.getKeywords() != null)
	    s.getKeywords().stream().forEach(k -> k.toLowerCase().trim());

	s = service.create(s);

	return created(s);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeSearchRewrite(@PathParam("id") Id id) {
	if (id != null) {
	    SearchRewrite s = checked(service.get(SearchRewrite.class, id));

	    if (s != null) {
		service.remove(s);
	    }
	}
    }
}
