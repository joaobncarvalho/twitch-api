package com.casino.resource;

import com.casino.domain.SinglePlayerGame;
import com.casino.domain.StreamSession;
import com.casino.service.SessionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;


@Path("/single-player")
public class SinglePlayerResource {

    @Inject
    SessionService sessionService;

    @POST
    public Response register(SinglePlayerGame game) {
        StreamSession active = sessionService.getActiveSession()
                .orElseThrow(() -> new BadRequestException("Nenhuma sessão ativa"));

        game.sessionId = (ObjectId) active.id;
        game.persist();

        // Se ganhou, aumenta o saldo da stream; se apostou, diminui.
        sessionService.updateSessionBalance(game.win - game.bet);

        return Response.status(201).entity(game).build();
    }
}
