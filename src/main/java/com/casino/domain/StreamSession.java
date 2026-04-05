package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "sessions")
public class StreamSession extends PanacheMongoEntity {

    @NotBlank(message = "A sessão precisa de um título (ex: Live 05/04)")
    public String title;

    public LocalDateTime startTime = LocalDateTime.now();
    public LocalDateTime endTime;

    public double initialBalance;

    // Saldo atualizado em tempo real conforme as jogadas entram
    public double currentBalance;

    public boolean active = true;

    // --- Lógica de Domínio ---

    public void endSession(double finalBalance) {
        this.endTime = LocalDateTime.now();
        this.currentBalance = finalBalance;
        this.active = false;
    }

    /**
     * Cálculo de Profit/Loss total da sessão.
     */
    public double getTotalPnl() {
        return currentBalance - initialBalance;
    }
}