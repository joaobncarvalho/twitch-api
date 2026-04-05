package com.casino.service;

import com.casino.domain.BonusHunt;
import com.casino.domain.BonusHuntSlot;
import com.casino.domain.StreamSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BonusHuntService {

    @Inject
    SessionService sessionService;

    /**
     * Cria uma nova Hunt vinculada automaticamente à sessão ativa.
     */
    public BonusHunt createHunt(String name, double startAmount) {
        StreamSession activeSession = sessionService.getActiveSession()
                .orElseThrow(() -> new BadRequestException("Não é possível criar uma Hunt sem uma sessão ativa."));

        BonusHunt hunt = new BonusHunt();
        hunt.name = name;
        hunt.startAmount = startAmount;
        hunt.sessionId = (ObjectId) activeSession.id;
        hunt.status = BonusHunt.HuntStatus.OPEN;

        hunt.persist();
        return hunt;
    }

    /**
     * Adiciona um slot à hunt e valida se a hunt ainda permite edições.
     */
    public BonusHunt addSlotToHunt(String huntId, BonusHuntSlot slot) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(huntId));
        if (hunt == null) throw new NotFoundException("Hunt não encontrada");

        if (hunt.status != BonusHunt.HuntStatus.OPEN) {
            throw new BadRequestException("Não é possível adicionar slots a uma Hunt que já não está aberta.");
        }

        hunt.slots.add(slot);
        hunt.update();
        return hunt;
    }

    /**
     * Regista o prémio de um slot e atualiza o saldo da sessão em tempo real.
     */
    public BonusHunt collectSlotWin(String huntId, String slotName, double winAmount) {
        BonusHunt hunt = BonusHunt.findById(new ObjectId(huntId));

        hunt.slots.stream()
                .filter(s -> s.slotName.equalsIgnoreCase(slotName))
                .findFirst()
                .ifPresent(s -> {
                    s.winAmount = winAmount;
                    s.collected = true;
                    // Comunicação entre serviços: Atualiza o saldo da live!
                    sessionService.updateSessionBalance(winAmount);
                });

        hunt.update();
        return hunt;
    }
}