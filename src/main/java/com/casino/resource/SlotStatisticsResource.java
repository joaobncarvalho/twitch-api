package com.casino.resource;

import com.casino.model.BonusHunt;
import com.casino.model.SinglePlayerEntry;
import com.casino.model.SlotEntry;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.OptionalDouble;

@Path("/slots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SlotStatisticsResource {

    @GET
    @Path("/{slotName}/statistics")
    public Response getSlotStatistics(@PathParam("slotName") String slotName) {
        List<BonusHunt> bonusHunts = BonusHunt.listAll();
        List<SinglePlayerEntry> singlePlayerGames = SinglePlayerEntry.list("slotName", slotName);

        double totalMultipliers = 0;
        double totalBets = 0;
        int totalBonuses = 0;
        double bestMultiplier = 0;
        double bestWin = 0;
        double lastWin = 0;

        // 🔄 Buscar estatísticas nas Bonus Hunts
        for (BonusHunt hunt : bonusHunts) {
            for (SlotEntry slot : hunt.slots) {
                if (slot.name.equalsIgnoreCase(slotName)) {
                    double multiplier = (slot.win > 0 && slot.bet > 0) ? (slot.win / slot.bet) : 0;

                    totalMultipliers += multiplier;
                    totalBets++;
                    totalBonuses++;

                    if (multiplier > bestMultiplier) {
                        bestMultiplier = multiplier;
                    }
                    if (slot.win > bestWin) {
                        bestWin = slot.win; // 🔥 Guarda o maior pagamento
                    }
                    lastWin = slot.win; // 🔄 Atualiza com a última win encontrada
                }
            }
        }

        // 🔵 Buscar estatísticas do Single Player
        for (SinglePlayerEntry game : singlePlayerGames) {
            double multiplier = (game.win > 0 && game.bet > 0) ? (game.win / game.bet) : 0;

            totalMultipliers += multiplier;
            totalBets++;
            totalBonuses++;

            if (multiplier > bestMultiplier) {
                bestMultiplier = multiplier;
            }
            if (game.win > bestWin) {
                bestWin = game.win; // 🔥 Guarda o maior pagamento
            }
            lastWin = game.win; // 🔄 Atualiza com a última win encontrada
        }

        // 📊 Cálculo da Média do Multiplicador
        double averageMultiplier = (totalBets > 0) ? (totalMultipliers / totalBets) : 0;

        return Response.ok(new SlotStats(slotName, averageMultiplier, bestMultiplier, totalBonuses, bestWin, lastWin)).build();
    }

    // ✅ Classe que retorna os dados atualizados
    public static class SlotStats {
        public String slotName;
        public double averageMultiplier;
        public double bestMultiplier;
        public int totalBonuses;
        public double bestWin;
        public double lastWin;

        public SlotStats(String slotName, double averageMultiplier, double bestMultiplier, int totalBonuses, double bestWin, double lastWin) {
            this.slotName = slotName;
            this.averageMultiplier = averageMultiplier;
            this.bestMultiplier = bestMultiplier;
            this.totalBonuses = totalBonuses;
            this.bestWin = bestWin;
            this.lastWin = lastWin;
        }
    }



    @GET
    @Path("/{slotName}/splatest-win")
    public Response getLatestSinglePlayerWin(@PathParam("slotName") String slotName) {
        SinglePlayerEntry latestWin = SinglePlayerEntry.find("slotName = ?1 ORDER BY _id DESC", slotName).firstResult();

        if (latestWin == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Nenhuma win encontrada para essa slot").build();
        }

        return Response.ok(latestWin).build();
    }



    @GET
    @Path("/{slotName}/bhlatest-win")
    public Response getLatestBonusHuntWin(@PathParam("slotName") String slotName) {
        SlotEntry latestWin = null;

        List<BonusHunt> bonusHunts = BonusHunt.listAll(); // 🔄 Busca todas as Bonus Hunts
        for (BonusHunt hunt : bonusHunts) {
            for (SlotEntry slot : hunt.slots) {
                if (slot.name.equalsIgnoreCase(slotName)) {
                    if (latestWin == null || new ObjectId(slot.slotId.toString()).compareTo(new ObjectId(latestWin.slotId.toString())) > 0) {
                        latestWin = slot; // 🔥 Pega o mais recente
                    }
                }
            }
        }

        if (latestWin == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Nenhuma win encontrada na Bonus Hunt para essa slot").build();
        }

        return Response.ok(latestWin).build();
    }



    @GET
    @Path("/{slotName}/latest-win")
    public Response getLatestWin(@PathParam("slotName") String slotName) {
        // Obter última Single Player win
        SinglePlayerEntry latestSinglePlayerWin = SinglePlayerEntry
                .find("slotName = ?1 ORDER BY _id DESC", slotName)
                .firstResult();

        // Obter última Bonus Hunt win baseada no _id da BonusHunt (mais recente)
        SlotEntry latestBonusHuntWin = null;
        BonusHunt latestHunt = BonusHunt.find("slots.name = ?1 ORDER BY _id DESC", slotName).firstResult();

        if (latestHunt != null) {
            for (SlotEntry slot : latestHunt.slots) {
                if (slot.name.equalsIgnoreCase(slotName)) {
                    latestBonusHuntWin = slot;
                    break; // Já encontraste a última, não precisas continuar
                }
            }
        }

        // Comparação final entre Single Player e Bonus Hunt
        Object latestWin;
        if (latestSinglePlayerWin != null && latestBonusHuntWin != null) {
            latestWin = new ObjectId(latestSinglePlayerWin.id).compareTo(new ObjectId(latestHunt.id.toString())) > 0
                    ? latestSinglePlayerWin
                    : latestBonusHuntWin;
        } else {
            latestWin = (latestSinglePlayerWin != null) ? latestSinglePlayerWin : latestBonusHuntWin;
        }

        if (latestWin == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Nenhuma win encontrada").build();
        }

        return Response.ok(latestWin).build();
    }



}
