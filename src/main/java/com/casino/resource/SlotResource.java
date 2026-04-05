package com.casino.resource;

import com.casino.domain.BonusHunt;
import com.casino.domain.Slot;
import com.casino.domain.BonusHuntSlot; // v2.0
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
    public Response createSlot(Slot slot) {
        if (slot.name == null || slot.provider == null || slot.theoreticalRtp <= 0) {
            return Response.status(400).entity("Campos obrigatórios: name, provider, theoreticalRtp").build();
        }
        if (slot.imageUrl == null || slot.imageUrl.isEmpty()) {
            return Response.status(400).entity("Imagem obrigatória").build();
        }
        slot.persist();
        return Response.ok(slot).build();
    }

    @GET
    @Path("/providers")
    public Response getProviders() {
        List<String> providers = Slot.<Slot>listAll().stream()
                .map(s -> s.provider)
                .filter(p -> p != null && !p.isBlank())
                .distinct().sorted().toList();
        return Response.ok(providers).build();
    }

    @GET
    @Path("/search") // Otimizado: evitamos /slots/slots
    public List<Slot> getSlots(@QueryParam("query") String query) {
        return Slot.find("name like ?1", "%" + query + "%").list();
    }

    @PUT
    @Path("/bonus-hunts/{id}/slots")
    public Response addSlotToBonusHunt(@PathParam("id") String id, BonusHuntSlot slot) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) return Response.status(404).build();

        hunt.slots.add(slot);
        hunt.update(); // Use update() para entidades existentes
        return Response.ok(hunt).build();
    }

    @GET
    public List<Slot> list() {
        return Slot.listAll();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        Slot slot = Slot.findById(new ObjectId(id));
        return slot != null ? Response.ok(slot).build() : Response.status(404).build();
    }
}