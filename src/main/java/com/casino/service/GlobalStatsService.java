package com.casino.service;

import com.casino.domain.BonusHunt;
import com.casino.domain.SinglePlayerGame;
import com.casino.domain.StreamSession;
import com.casino.dto.LiveStreamStatsDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

@ApplicationScoped
public class GlobalStatsService {

    @Inject
    SessionService sessionService;

    public LiveStreamStatsDTO getCurrentSessionStats() {
        StreamSession session = sessionService.getActiveSession()
                .orElseThrow(() -> new jakarta.ws.rs.NotFoundException("Nenhuma sessão ativa"));

        ObjectId sId = (ObjectId) session.id;

        // 1. Somar Single Player
        double spWagered = SinglePlayerGame.find("sessionId", sId).stream()
                .mapToDouble(g -> ((SinglePlayerGame) g).bet).sum();
        double spWon = SinglePlayerGame.find("sessionId", sId).stream()
                .mapToDouble(g -> ((SinglePlayerGame) g).win).sum();

        // 2. Somar Bonus Hunts
        double bhWagered = BonusHunt.find("sessionId", sId).stream()
                .mapToDouble(h -> ((BonusHunt) h).startAmount).sum();
        double bhWon = BonusHunt.find("sessionId", sId).stream()
                .mapToDouble(h -> ((BonusHunt) h).getTotalWon()).sum();

        double totalWagered = spWagered + bhWagered;
        double totalWon = spWon + bhWon;
        double netPnl = totalWon - totalWagered;
        double luckFactor = totalWagered > 0 ? (totalWon / totalWagered) * 100 : 0;

        return new LiveStreamStatsDTO(
                session.title,
                session.initialBalance,
                session.currentBalance,
                totalWagered,
                totalWon,
                netPnl,
                luckFactor
        );
    }
}