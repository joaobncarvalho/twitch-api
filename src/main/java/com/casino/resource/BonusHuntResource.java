package com.casino.resource;

import com.casino.model.BonusHunt;
import com.casino.model.Slot;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Path("/bonus-hunt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BonusHuntResource {

    @POST
    public Response create(BonusHunt hunt) {
        if (hunt.name == null || hunt.name.isBlank() || hunt.startAmount <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nome e valor inicial são obrigatórios!").build();
        }
        hunt.persist();
        return Response.status(Response.Status.CREATED).entity(hunt).build();
    }


    @GET
    public List<BonusHunt> list() {
        return BonusHunt.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getBonusHunt(@PathParam("id") String id) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bonus Hunt not found").build();
        }
        return Response.ok(hunt).build();
    }


    @PUT
    @Path("/{id}/slots")
    public Response addSlot(@PathParam("id") String id, Slot slot) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            throw new WebApplicationException("Bonus Hunt not found", 404);
        }
        if (hunt.slots == null) {
            hunt.slots = new ArrayList<>();
        }
        hunt.slots.add(slot);
        hunt.update();  // 🔹 Atualiza no MongoDB
        return Response.ok(hunt).build();
    }


    @GET
    @Path("/{id}/slots")
    public List<Slot> getSlots(@PathParam("id") String id) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            throw new WebApplicationException("Bonus Hunt not found", 404);
        }
        return hunt.slots;
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") String id, BonusHunt updatedHunt) {
        BonusHunt existingHunt = BonusHunt.findById(new org.bson.types.ObjectId(id));
        if (existingHunt == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existingHunt.name = updatedHunt.name;
        existingHunt.startAmount = updatedHunt.startAmount;
        existingHunt.breakEven = updatedHunt.breakEven;
        existingHunt.update();
        return Response.ok(existingHunt).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        BonusHunt hunt = BonusHunt.findById(new org.bson.types.ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        hunt.delete();
        return Response.noContent().build();
    }

    @GET
    @Path("/statistics")
    public Response getStatistics() {
        long totalBonusHunts = BonusHunt.count();
        long totalSlots = Slot.count();

        return Response.ok("{" +
                "\"total_bonus_hunts\": " + totalBonusHunts + ", " +
                "\"total_slots\": " + totalSlots + "}" ).build();
    }

}
