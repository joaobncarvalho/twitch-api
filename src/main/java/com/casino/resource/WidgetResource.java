package com.casino.resource;

import com.casino.domain.Slot;
import com.casino.domain.WidgetState;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/widget")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WidgetResource {

    @POST
    @Path("/slot")
    public Response updateSlot(String slotName) {
        WidgetState state = WidgetState.findMainWidget();
        if (state == null) {
            state = new WidgetState();
            state.widgetKey = "main_widget";
        }

        state.currentSlotName = slotName;
        state.persistOrUpdate();
        return Response.ok().build();
    }

    @GET
    @Path("/active-slot")
    public Response getActiveSlot() {
        WidgetState state = WidgetState.findMainWidget();
        if (state == null || state.currentSlotName == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Slot.find("name", state.currentSlotName).firstResultOptional()
                .map(slot -> Response.ok(slot).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}