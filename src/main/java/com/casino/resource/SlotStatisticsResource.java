package com.casino.resource;

import com.casino.domain.BonusHunt;
import com.casino.domain.SinglePlayerGame; // v2.0
import com.casino.domain.BonusHuntSlot;      // v2.0
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import java.util.Comparator;
import java.util.List;

@Path("/slot-stats") // Alterado para evitar conflito JAX-RS com /slots
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SlotStatisticsResource {

    @GET
    @Path("/{slotName}/statistics")
    public Response getSlotStatistics(@PathParam("slotName") String slotName) {
        // Performance: Filtramos na DB, não em Java
        List<SinglePlayerGame> spGames = SinglePlayerGame.list("slotName", slotName);
        List<BonusHunt> bonusHunts = BonusHunt.find("slots.slotName", slotName).list();

        double totalMultipliers = 0;
        double totalBets = 0;
        int totalBonuses = 0;
        double bestMultiplier = 0;
        double bestWin = 0;
        double lastWin = 0;

        for (BonusHunt hunt : bonusHunts) {
            for (BonusHuntSlot s : hunt.slots) {
                if (s.slotName.equalsIgnoreCase(slotName)) {
                    double mult = (s.winAmount > 0 && s.betSize > 0) ? (s.winAmount / s.betSize) : 0;
                    totalMultipliers += mult;
                    totalBets++;
                    totalBonuses++;
                    bestMultiplier = Math.max(bestMultiplier, mult);
                    bestWin = Math.max(bestWin, s.winAmount);
                    lastWin = s.winAmount;
                }
            }
        }

        for (SinglePlayerGame game : spGames) {
            double mult = game.getMultiplier();
            totalMultipliers += mult;
            totalBets++;
            totalBonuses++;
            bestMultiplier = Math.max(bestMultiplier, mult);
            bestWin = Math.max(bestWin, game.win);
            lastWin = game.win;
        }

        double avgMult = (totalBets > 0) ? (totalMultipliers / totalBets) : 0;
        return Response.ok(new SlotStats(slotName, avgMult, bestMultiplier, totalBonuses, bestWin, lastWin)).build();
    }

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
    @Path("/{slotName}/latest-win")
    public Response getLatestWin(@PathParam("slotName") String slotName) {
        SinglePlayerGame latestSP = SinglePlayerGame.find("slotName", Sort.descending("createdAt"), slotName).firstResult();

        // Otimização: Query direta em vez de loop manual
        BonusHunt latestHunt = BonusHunt.find("slots.slotName", Sort.descending("createdAt"), slotName).firstResult();
        BonusHuntSlot latestBHS = null;

        if (latestHunt != null) {
            latestBHS = latestHunt.slots.stream()
                    .filter(s -> s.slotName.equalsIgnoreCase(slotName))
                    .max(Comparator.comparing(s -> latestHunt.createdAt)) // Aproximação pela data da hunt
                    .orElse(null);
        }

        if (latestSP == null && latestBHS == null) return Response.status(404).build();

        // Retorna o mais recente entre os dois
        return Response.ok(latestSP != null ? latestSP : latestBHS).build();
    }
}