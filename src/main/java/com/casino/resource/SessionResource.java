package com.casino.resource;

import com.casino.domain.StreamSession;
import com.casino.service.GlobalStatsService;
import com.casino.service.SessionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessionResource {

    @Inject
    SessionService sessionService;

    @Inject
    GlobalStatsService globalStatsService;

    @POST
    public Response create(StreamSession sessionRequest) {
        StreamSession session = sessionService.startNewSession(
                sessionRequest.title,
                sessionRequest.initialBalance
        );
        return Response.status(Response.Status.CREATED).entity(session).build();
    }

    @GET
    @Path("/active")
    public Response getActive() {
        return sessionService.getActiveSession()
                .map(session -> Response.ok(session).build())
                .orElse(Response.status(Response.Status.NO_CONTENT).build());
    }

    @PUT
    @Path("/{id}/terminate")
    public Response terminate(@PathParam("id") String id, StreamSession update) {
        return Response.ok(sessionService.terminateSession(id, update.currentBalance)).build();
    }

    @GET
    @Path("/active/stats")
    public Response getLiveStats() {
        return Response.ok(globalStatsService.getCurrentSessionStats()).build();
    }
}