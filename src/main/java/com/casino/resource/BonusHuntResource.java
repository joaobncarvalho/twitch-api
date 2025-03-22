package com.casino.resource;

import com.casino.model.BonusHunt;
import com.casino.model.BonusHuntSlotEntry;
import com.casino.model.Slot;
import com.casino.model.SlotEntry;
import io.quarkus.panache.common.Sort;
import io.vertx.core.json.JsonObject;
import jakarta.json.Json;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.*;

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
        bonusHunt.breakEvenInicial = 0;  // Inicializa como 0
        bonusHunt.breakEvenAtual = 0;
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

        // 🔥 Usa um Set para garantir que a slot não é duplicada
        if (hunt.slots == null) {
            hunt.slots = new ArrayList<>();
        }

        // 🔥 Verifica se a slot já existe na Bonus Hunt para evitar operações desnecessárias
        if (hunt.slots.stream().anyMatch(s -> s.slotId.equals(slotEntry.slotId))) {
            return Response.ok(hunt).build(); // Já existe, retorna diretamente
        }

        hunt.slots.add(slotEntry);
        hunt.persistOrUpdate(); // 🔥 Atualiza tudo de uma vez

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
        existingEntry.createdAt = updatedEntry.createdAt != null ? updatedEntry.createdAt : new Date().toInstant(); // ✅ obrigatório

        // 🚀 Use `update()` no lugar de `persist()` para evitar duplicação
        hunt.update();
        return Response.ok(existingEntry).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteBonusHunt(@PathParam("id") String id) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Bonus Hunt não encontrada")
                    .build();
        }

        hunt.delete(); // 🔥 Remove do MongoDB
        return Response.noContent().build(); // Retorna 204 No Content
    }



    @DELETE
    @Path("/{id}/slots/{slotEntryId}")
    public Response removeSlotFromBonusHunt(@PathParam("id") String id, @PathParam("slotEntryId") String slotEntryId) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(id));
        if (hunt == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bonus Hunt not found").build();
        }

        // 🚀 Em vez de carregar todas as slots, apenas removemos a necessária
        boolean removed = hunt.slots.removeIf(slot -> slot.slotId.toString().equals(slotEntryId));

        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND).entity("Slot entry not found in this Bonus Hunt").build();
        }

        // 🔥 Atualiza o banco apenas se houve remoção
        hunt.persistOrUpdate();

        return Response.noContent().build();
    }

    @GET
    @Path("/latest")
    public Response getLatestBonusHunt() {
        BonusHunt latestHunt = BonusHunt.findAll(Sort.descending("_id")).firstResult();

        if (latestHunt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Nenhuma Bonus Hunt encontrada").build();
        }

        return Response.ok(latestHunt).build();
    }

    @GET
    @Path("/latest/best-slot")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBestSlotOfLatestBonusHunt() {
        BonusHunt latestHunt = BonusHunt.findAll(Sort.descending("_id")).firstResult();

        if (latestHunt == null || latestHunt.slots.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        SlotEntry bestSlot = latestHunt.slots.stream()
                .filter(slot -> slot.win > 0)
                .max(Comparator.comparingDouble(slot -> slot.win))
                .orElse(null);

        if (bestSlot == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(bestSlot).build();
    }



    @PUT
    @Path("/{id}/calculate-breakeven-inicial")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularBreakEvenInicial(@PathParam("id") String id) {
        BonusHunt bonusHunt = BonusHunt.findById(new ObjectId(id));

        if (bonusHunt == null || bonusHunt.slots.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Bonus Hunt não encontrada ou sem slots.").build();
        }

        double totalBet = bonusHunt.slots.stream().mapToDouble(slot -> slot.bet).sum();

        if (totalBet == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Não há apostas para calcular o BE.").build();
        }

        bonusHunt.breakEvenInicial = bonusHunt.startAmount / totalBet;
        bonusHunt.persistOrUpdate();

        return Response.ok(Map.of("breakEvenInicial", bonusHunt.breakEvenInicial)).build();
    }

    @PUT
    @Path("/{id}/calculate-breakeven-atual")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularBreakEvenAtual(@PathParam("id") String id, List<SlotEntry> slotsAtualizados) {
        BonusHunt bonusHunt = BonusHunt.findById(new ObjectId(id));

        if (bonusHunt == null || bonusHunt.slots.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Bonus Hunt não encontrada ou sem slots.")
                    .build();
        }

        // 🔄 Atualizar wins dos slots recebidos
        for (SlotEntry updatedSlot : slotsAtualizados) {
            bonusHunt.slots.stream()
                    .filter(slot -> slot.slotId.equals(updatedSlot.slotId))
                    .forEach(slot -> {
                        slot.win = updatedSlot.win;
                        slot.createdAt = updatedSlot.createdAt; // garante que atualiza o createdAt também
                    });
        }

        // 🔥 Corrige cálculo totalWins
        double totalWinsRegistradas = bonusHunt.slots.stream()
                .mapToDouble(slot -> slot.win)
                .sum();

        // 🔥 Slots restantes (slots sem win registrada)
        long slotsRestantes = bonusHunt.slots.stream()
                .filter(slot -> slot.win == 0)
                .count();

        double saldoRestanteARecuperar = bonusHunt.startAmount - totalWinsRegistradas;

        bonusHunt.breakEvenAtual = slotsRestantes > 0
                ? saldoRestanteARecuperar / slotsRestantes
                : 0;

        bonusHunt.persistOrUpdate();

        return Response.ok(Map.of("breakEvenAtual", bonusHunt.breakEvenAtual)).build();
    }


}
