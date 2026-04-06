package com.casino.service;

import com.casino.domain.StreamSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import java.util.Optional;

@ApplicationScoped
public class SessionService {

    /**
     * Inicia uma nova sessão.
     * Se houver uma sessão ativa, encerra-a automaticamente antes de abrir a nova.
     */
    public StreamSession startNewSession(String title, double initialBalance) {
        // Regra de Ouro: Apenas uma sessão ativa por vez
        getActiveSession().ifPresent(session -> {
            session.active = false;
            session.update();
        });

        StreamSession newSession = new StreamSession();
        newSession.title = title;
        newSession.initialBalance = initialBalance;
        newSession.currentBalance = initialBalance;
        newSession.active = true;

        newSession.persist();
        return newSession;
    }

    /**
     * Procura a sessão que está atualmente em live.
     */
    public Optional<StreamSession> getActiveSession() {
        return StreamSession.find("ativo", true).firstResultOptional();
    }

    /**
     * Encerra a sessão atual e define o saldo final.
     */
    public StreamSession terminateSession(String id, double finalBalance) {
        StreamSession session = StreamSession.findById(new org.bson.types.ObjectId(id));
        if (session == null) {
            throw new NotFoundException("Sessão não encontrada");
        }

        session.endSession(finalBalance);
        session.update();
        return session;
    }

    /**
     * Atualiza o saldo da sessão em tempo real (chamado por outras atividades).
     */
    public void updateSessionBalance(double amountChange) {
        getActiveSession().ifPresent(session -> {
            session.currentBalance += amountChange;
            session.update();
        });
    }
}