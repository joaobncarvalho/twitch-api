package com.casino.resource;

import com.casino.model.BonusHunt;
import com.casino.model.BonusHuntSlotEntry;
import com.casino.model.Slot;
import com.casino.model.SlotEntry;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/bonus-hunt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BonusHuntResource {

    @GET
    public List<BonusHunt> list() {
        return BonusHunt.listAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBonusHunt(BonusHunt bonusHunt) {
        System.out.println("🔍 JSON recebido: " + bonusHunt);

        if (bonusHunt.startAmount == 0) {
            System.out.println("⚠️ ALERTA: start_amount recebido como 0!");
        } else {
            System.out.println("✅ start_amount recebido corretamente: " + bonusHunt.startAmount);
        }

        bonusHunt.persist();
        return Response.status(Response.Status.CREATED).entity(bonusHunt).build();
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


    @GET
    @Path("/{id}/slots")
    public Response getBonusHuntSlots(@PathParam("id") String id) {
        List<BonusHuntSlotEntry> slots = BonusHuntSlotEntry.list("bonusHuntId", new ObjectId(id));
        return Response.ok(slots).build();
    }

    @PUT
    @Path("/{id}/slots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSlotToBonusHunt(@PathParam("id") String id, SlotEntry slotEntry) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bonus Hunt not found").build();
        }

        if (hunt.slots == null) {
            hunt.slots = new ArrayList<>();
        }

        // 🔹 Garantir que o nome está sendo persistido
        if (slotEntry.name == null || slotEntry.name.isEmpty()) {
            Slot slotInfo = Slot.findById(slotEntry.slotId);
            if (slotInfo != null) {
                slotEntry.name = slotInfo.name; // Obtém o nome correto da slot
            }
        }

        hunt.slots.add(slotEntry);
        hunt.update();

        return Response.ok(hunt).build();
    }


    @PUT
    @Path("/{id}/slots/{slotEntryId}")
    public Response updateSlotInBonusHunt(@PathParam("id") String huntId, @PathParam("slotEntryId") String slotEntryId, BonusHuntSlotEntry updatedEntry) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(huntId));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bonus Hunt not found").build();
        }

        Optional<SlotEntry> slotEntryOpt = hunt.slots.stream()
                .filter(slot -> slot.slotId.equals(new ObjectId(slotEntryId)))
                .findFirst();

        if (slotEntryOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Slot entry not found in this Bonus Hunt").build();
        }

        SlotEntry existingEntry = slotEntryOpt.get();
        existingEntry.bet = updatedEntry.bet;
        existingEntry.win = updatedEntry.win;
        existingEntry.extraScatters = updatedEntry.extraScatters;
        existingEntry.superMode = updatedEntry.superMode;

        // 🚀 Use `update()` no lugar de `persist()` para evitar duplicação
        hunt.update();
        return Response.ok(existingEntry).build();
    }




    @DELETE
    @Path("/{id}/slots/{slotEntryId}")
    public Response removeSlotFromBonusHunt(@PathParam("id") String id, @PathParam("slotEntryId") String slotEntryId) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bonus Hunt not found").build();
        }

        // Encontrar e remover a slot correta dentro da lista de slots
        boolean removed = hunt.slots.removeIf(slot -> slot.slotId.toString().equals(slotEntryId));
        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND).entity("Slot entry not found in this Bonus Hunt").build();
        }

        hunt.persistOrUpdate();
        return Response.noContent().build();
    }



}
