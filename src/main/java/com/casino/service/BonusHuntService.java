package com.casino.service;

import com.casino.domain.BonusHunt;
import com.casino.domain.BonusHuntSlot;
import com.casino.domain.StreamSession;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

import java.util.Optional;

@ApplicationScoped
public class BonusHuntService {

    @Inject
    SessionService sessionService;

    // --- MÉTODOS ADICIONADOS PARA RESOLVER O ERRO DE COMPILAÇÃO ---

    /**
     * Busca uma hunt por ID e encapsula em Optional para o Resource.
     */
    public Optional<BonusHunt> findById(String id) {
        return Optional.ofNullable(BonusHunt.findById(new ObjectId(id)));
    }

    /**
     * Busca a Hunt mais recente (baseada no ID do MongoDB que é cronológico).
     */
    public Optional<BonusHunt> findLatest() {
        // CORRETO: findAll() com Sort explícito
        return BonusHunt.findAll(Sort.descending("id")).firstResultOptional();
    }

    /**
     * Remove uma hunt do sistema.
     */
    public boolean deleteHunt(String id) {
        return BonusHunt.deleteById(new ObjectId(id));
    }

    // --- MÉTODOS EXISTENTES REFATORADOS ---

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

    public BonusHunt addSlotToHunt(String huntId, BonusHuntSlot slot) {
        BonusHunt hunt = findById(huntId)
                .orElseThrow(() -> new NotFoundException("Hunt não encontrada"));

        if (hunt.status != BonusHunt.HuntStatus.OPEN) {
            throw new BadRequestException("A Hunt já não permite adicionar slots.");
        }

        hunt.slots.add(slot);
        hunt.update();
        return hunt;
    }

    public BonusHunt updateSlotBet(String huntId, String slotName, double newBet) {
        BonusHunt hunt = findById(huntId).orElseThrow(() -> new NotFoundException("Hunt não encontrada"));

        if (hunt.status != BonusHunt.HuntStatus.OPEN) {
            throw new BadRequestException("Não é possível alterar apostas após o início da coleta.");
        }

        hunt.slots.stream()
                .filter(s -> s.slotName.equalsIgnoreCase(slotName))
                .findFirst()
                .ifPresent(s -> s.betSize = newBet);

        hunt.update();
        return hunt;
    }

    public BonusHunt collectSlotWin(String huntId, String slotName, double winAmount) {
        BonusHunt hunt = findById(huntId)
                .orElseThrow(() -> new NotFoundException("Hunt não encontrada"));

        hunt.slots.stream()
                // CORREÇÃO: Usa 'name' em vez de 'slotName'
                .filter(s -> s.slotName.equalsIgnoreCase(slotName))
                .findFirst()
                .ifPresent(s -> {
                    // CORREÇÃO: Usa 'win' em vez de 'winAmount'
                    s.winAmount = winAmount;
                    s.collected = true;
                    // Atualiza o saldo da sessão ativa
                    sessionService.updateSessionBalance(winAmount);
                });

        hunt.update();
        return hunt;
    }

    /**
     * Tranca a lista de slots e avança a Hunt para a fase de abertura (coleta de prémios).
     */
    public BonusHunt startOpeningPhase(String huntId) {
        BonusHunt hunt = findById(huntId)
                .orElseThrow(() -> new NotFoundException("Hunt não encontrada"));

        if (hunt.status != BonusHunt.HuntStatus.OPEN) {
            throw new BadRequestException("A Hunt já não está na fase de configuração.");
        }

        // Muda o estado para a fase de coleta
        hunt.status = BonusHunt.HuntStatus.COLLECTING;
        hunt.update();

        return hunt;
    }

    /**
     * Finaliza a Bonus Hunt oficialmente.
     */
    public BonusHunt finishHunt(String huntId) {
        BonusHunt hunt = findById(huntId)
                .orElseThrow(() -> new NotFoundException("Hunt não encontrada"));

        // Verifica se todos os bónus foram abertos antes de deixar finalizar
        boolean allCollected = hunt.slots.stream().allMatch(s -> s.collected);
        if (!allCollected) {
            throw new BadRequestException("Ainda tens bónus por abrir! Coleta todos os prémios primeiro.");
        }

        hunt.status = BonusHunt.HuntStatus.FINISHED;
        hunt.update();

        return hunt;
    }
}