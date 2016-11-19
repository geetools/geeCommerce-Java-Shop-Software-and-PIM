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
import com.geecommerce.search.model.Synonym;
import com.google.inject.Inject;

@Path("/v1/synonyms")
public class SynonymResource extends AbstractResource {

    private final RestService service;

    @Inject
    public SynonymResource(RestService service) {
	this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getSynonyms(@FilterParam Filter filter) {
	return ok(service.get(Synonym.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Synonym getSynonym(@PathParam("id") Id id) {
	return checked(service.get(Synonym.class, id));
    }

    @PUT
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void updateSynonym(@PathParam("id") Id id, Update update) {
	if (id != null) {
	    Synonym s = checked(service.get(Synonym.class, id));
	    s.set(update.getFields());

	    service.update(s);
	}
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createSynonym(Update update) {
	Synonym s = app.getModel(Synonym.class);
	s.set(update.getFields());
	s = service.create(s);

	return created(s);
    }

    @DELETE
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void removeSynonym(@PathParam("id") Id id) {
	if (id != null) {
	    Synonym s = checked(service.get(Synonym.class, id));

	    if (s != null) {
		service.remove(s);
	    }
	}
    }
}
