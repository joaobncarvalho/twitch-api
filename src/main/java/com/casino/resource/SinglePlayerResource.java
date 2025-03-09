package com.casino.resource;

import com.casino.model.SinglePlayerEntry;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/single-player")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SinglePlayerResource {

    @POST
    @Transactional
    public Response registerSinglePlayerGame(SinglePlayerEntry entry) {
        if (entry.slotName == null || entry.bet <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nome da slot e bet são obrigatórios").build();
        }

        entry.id = new ObjectId().toHexString(); // 🔥 Garante um ID único
        entry.persist();

        return Response.status(Response.Status.CREATED).entity(entry).build();
    }



    @GET
    public List<SinglePlayerEntry> listAll() {
        return SinglePlayerEntry.listAll();
    }
}
