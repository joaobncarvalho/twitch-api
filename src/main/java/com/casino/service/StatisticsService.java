package com.casino.service;

import com.casino.domain.BonusHunt;
import com.casino.domain.SinglePlayerGame;
import com.casino.dto.SlotStatsDTO;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class StatisticsService {

    public SlotStatsDTO getSlotStatistics(String slotName) {
        // Na v2.0, usamos queries filtradas no MongoDB em vez de carregar tudo
        List<SinglePlayerGame> spGames = SinglePlayerGame.list("slotName", slotName);
        List<BonusHunt> hunts = BonusHunt.listAll(); // Temporário até otimizarmos a query interna de bónus

        double totalMultipliers = 0;
        int totalEntries = 0;
        double bestWin = 0;
        double bestMultiplier = 0;

        // Processa Single Player
        for (SinglePlayerGame game : spGames) {
            double mult = game.getMultiplier();
            totalMultipliers += mult;
            totalEntries++;
            bestWin = Math.max(bestWin, game.win);
            bestMultiplier = Math.max(bestMultiplier, mult);
        }

        // Processa Bonus Hunts (Refatorado para usar a nova estrutura v2.0)
        for (BonusHunt hunt : hunts) {
            hunt.slots.stream()
                    .filter(s -> s.slotName.equalsIgnoreCase(slotName))
                    .forEach(s -> {
                        double mult = (s.winAmount / s.betSize);
                        // Lógica de agregação aqui...
                    });
        }

        // Retorna um DTO (Data Transfer Object) limpo
        return new SlotStatsDTO(slotName, totalEntries > 0 ? totalMultipliers/totalEntries : 0, bestMultiplier, bestWin);
    }
}