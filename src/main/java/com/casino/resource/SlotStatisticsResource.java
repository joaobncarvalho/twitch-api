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
                }
            }
        }

        for (SinglePlayerEntry game : singlePlayerGames) {
            double multiplier = (game.win > 0 && game.bet > 0) ? (game.win / game.bet) : 0;

            totalMultipliers += multiplier;
            totalBets++;
            totalBonuses++;

            if (multiplier > bestMultiplier) {
                bestMultiplier = multiplier;
            }
        }

        double averageMultiplier = (totalBets > 0) ? (totalMultipliers / totalBets) : 0;

        return Response.ok(new SlotStats(slotName, averageMultiplier, bestMultiplier, totalBonuses)).build();
    }


    // Classe auxiliar para serializar JSON
    public static class SlotStats {
        public String slotName;
        public double averageMultiplier;
        public double bestMultiplier;
        public int totalBonuses;

        public SlotStats(String slotName, double averageMultiplier, double bestMultiplier, int totalBonuses) {
            this.slotName = slotName;
            this.averageMultiplier = averageMultiplier;
            this.bestMultiplier = bestMultiplier;
            this.totalBonuses = totalBonuses;
        }
    }




}
