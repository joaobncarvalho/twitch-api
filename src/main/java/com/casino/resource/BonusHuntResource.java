package com.casino.resource;

import com.casino.domain.BonusHunt;
import com.casino.domain.BonusHuntSlot;
import com.casino.service.BonusHuntService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import java.util.List;

@Path("/bonus-hunt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BonusHuntResource {

    public static class WinPayload {
        public double winAmount;
    }

    public static class BetPayload {
        public double bet;
    }

    @Inject
    BonusHuntService huntService;

    // 1. ROTA ESTÁTICA: Deve vir ANTES das rotas com {id}
    @GET
    @Path("/latest")
    public Response getLatest() {
        return huntService.findLatest()
                .map(hunt -> Response.ok(hunt).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // 2. BUSCA POR ID: Protegida por Regex (apenas 24 caracteres hexadecimais)
    @GET
    @Path("/{id:[a-f0-9]{24}}")
    public Response getById(@PathParam("id") String id) {
        return huntService.findById(id)
                .map(hunt -> Response.ok(hunt).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(@Valid BonusHunt request) {
        BonusHunt hunt = huntService.createHunt(request.name, request.startAmount);
        return Response.status(Response.Status.CREATED).entity(hunt).build();
    }

    // 3. ATUALIZAÇÕES: Também protegidas por Regex
    @PUT
    @Path("/{id:[a-f0-9]{24}}/slots")
    public Response addSlot(@PathParam("id") String id, @Valid BonusHuntSlot slot) {
        return Response.ok(huntService.addSlotToHunt(id, slot)).build();
    }

    @PATCH
    @Path("/{id:[a-f0-9]{24}}/slots/{slotName}/collect")
    public Response collectWin(@PathParam("id") String id,
                               @PathParam("slotName") String slotName,
                               @QueryParam("amount") double amount) {
        return Response.ok(huntService.collectSlotWin(id, slotName, amount)).build();
    }

    @DELETE
    @Path("/{id:[a-f0-9]{24}}")
    public Response delete(@PathParam("id") String id) {
        boolean deleted = huntService.deleteHunt(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public List<BonusHunt> listAll() {
        return BonusHunt.listAll();
    }

    @PUT
    @Path("/{id}/start-collection")
    public Response startCollection(@PathParam("id") String id) {
        return Response.ok(huntService.startOpeningPhase(id)).build();
    }

    /**
     * Rota para recolher o prémio (Fase de Coleta)
     */
    @PUT
    @Path("/{id}/slots/{slotName}/collect")
    public Response collectWin(@PathParam("id") String id, @PathParam("slotName") String slotName, WinPayload payload) {
        // Chama o serviço que já tinhas criado
        return Response.ok(huntService.collectSlotWin(id, slotName, payload.winAmount)).build();
    }

    /**
     * Rota para atualizar a aposta da slot na tabela (Fase OPEN)
     */
    @PATCH
    @Path("/{id}/slots/{slotName}/bet")
    public Response updateBet(@PathParam("id") String id, @PathParam("slotName") String slotName, BetPayload payload) {
        // Chama o serviço que já tinhas criado
        return Response.ok(huntService.updateSlotBet(id, slotName, payload.bet)).build();
    }

    @PUT
    @Path("/{id}/finish")
    public Response finish(@PathParam("id") String id) {
        return Response.ok(huntService.finishHunt(id)).build();
    }
}