package com.geecommerce.core.system.widget.rest.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.widget.helper.WidgetHelper;
import com.geecommerce.core.system.widget.model.Widget;
import com.geecommerce.core.system.widget.model.WidgetGroup;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.api.WidgetController;
import com.google.inject.Inject;

@Path("/v1/widgets")
public class WidgetResource extends AbstractResource {
    private final RestService service;

    @Inject
    public WidgetResource(RestService service) {
        this.service = service;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getWidgets(@FilterParam Filter filter) {
        return ok(checked(service.get(Widget.class)));// WidgetHelper.locateWidgets()
    }

    @GET
    @Path("{widget}/options/{parameter}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getOptions(@PathParam("widget") String widgetCode, @PathParam("parameter") Id parameterId,
        @FilterParam Filter filter) {
        WidgetController widgetController = WidgetHelper.findWidgetByCode(widgetCode);
        return ok(widgetController.getParameterOptions(parameterId));
    }

    @GET
    @Path("/groups")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getWidgetGroups(@FilterParam Filter filter) {
        return ok(checked(service.get(WidgetGroup.class)));
    }
}
