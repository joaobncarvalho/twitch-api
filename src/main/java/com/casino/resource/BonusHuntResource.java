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

    @Inject
    BonusHuntService huntService;

    @POST
    public Response create(@Valid BonusHunt request) {
        // Delegamos 100% da criação ao serviço para garantir o vínculo com a sessão ativa
        BonusHunt hunt = huntService.createHunt(request.name, request.startAmount);
        return Response.status(Response.Status.CREATED).entity(hunt).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        // Mantemos o findById aqui por ser uma operação simples de leitura (Active Record)
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(hunt).build();
    }

    @PUT
    @Path("/{id}/slots")
    public Response addSlot(@PathParam("id") String id, @Valid BonusHuntSlot slot) {
        // O serviço agora valida se a Hunt está aberta antes de adicionar
        return Response.ok(huntService.addSlotToHunt(id, slot)).build();
    }

    @PATCH
    @Path("/{id}/slots/{slotName}/collect")
    public Response collectWin(@PathParam("id") String id,
                               @PathParam("slotName") String slotName,
                               @QueryParam("amount") double amount) {
        // Novo endpoint v2.0: Regista o prémio e atualiza o saldo da live automaticamente
        return Response.ok(huntService.collectSlotWin(id, slotName, amount)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        boolean deleted = BonusHunt.deleteById(new ObjectId(id));
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public List<BonusHunt> listAll() {
        // Útil para o histórico do dashboard
        return BonusHunt.listAll();
    }
}