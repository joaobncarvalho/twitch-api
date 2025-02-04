package com.casino.resource;

import com.casino.model.BonusHunt;
import com.casino.model.Slot;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/slots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SlotResource {

    @POST
    @Path("/slots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSlot(Slot slot) {
        slot.persist();
        return Response.status(Response.Status.CREATED).entity(slot).build();
    }

    @GET
    @Path("/slots")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Slot> getSlots(@QueryParam("query") String query) {
        return Slot.list("name like ?1", "%" + query + "%");
    }

    @PUT
    @Path("/bonus-hunts/{id}/slots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSlotToBonusHunt(@PathParam("id") String id, Slot slot) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        hunt.slots.add(slot);
        hunt.persist();
        return Response.ok(hunt).build();
    }


    @GET
    public List<Slot> list() {
        return Slot.listAll();
    }

    @GET
    @Path("/{id}")
    public Slot get(@PathParam("id") String id) {
        return Slot.findById(new org.bson.types.ObjectId(id));
    }
}
