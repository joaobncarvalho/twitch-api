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

    // Rota 1: Estatísticas (GET /sessions/active/stats)
    @GET
    @Path("/active/stats")
    public Response getLiveStats() {
        try {
            // Tenta ir buscar as stats. Se não houver sessão ativa,
            // o teu GlobalStatsService provavelmente lança uma NotFoundException.
            Object stats = globalStatsService.getCurrentSessionStats();
            return Response.ok(stats).build();
        } catch (NotFoundException e) {
            // 🛑 O TRUQUE SNIPER: Em vez de devolver erro 404 (vermelho no browser),
            // devolvemos 204 (verde/cinza, que significa "OK, mas não há dados").
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            // Se for um erro a sério da base de dados, aí sim, devolvemos 500
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Rota 2: Encerrar (PUT /sessions/active/end)
    @PUT
    @Path("/active/end")
    public Response endActiveSession(StreamSession update) {
        return sessionService.getActiveSession()
                .map(session -> {
                    StreamSession terminated = sessionService.terminateSession(session.id.toString(), update.currentBalance);
                    return Response.ok(terminated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // Rota 3: Buscar Ativa (GET /sessions/active)
    @GET
    @Path("/active")
    public Response getActive() {
        return sessionService.getActiveSession()
                .map(session -> Response.ok(session).build())
                .orElse(Response.status(Response.Status.NO_CONTENT).build());
    }

    // Rota 4: Criar (POST /sessions)
    @POST
    public Response create(StreamSession sessionRequest) {
        StreamSession session = sessionService.startNewSession(
                sessionRequest.title,
                sessionRequest.initialBalance
        );
        return Response.status(Response.Status.CREATED).entity(session).build();
    }

    // Rota 5: Terminar por ID
    // O regex [a-f0-9]{24} garante que o Java só entre aqui se o ID parecer um ID do MongoDB
    // Isso impede que ele tente tratar "/active" como um ID.
    @PUT
    @Path("/{id:[a-f0-9]{24}}/terminate")
    public Response terminate(@PathParam("id") String id, StreamSession update) {
        return Response.ok(sessionService.terminateSession(id, update.currentBalance)).build();
    }
}