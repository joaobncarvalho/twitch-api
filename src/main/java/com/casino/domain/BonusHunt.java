package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "bonus_hunts")
public class BonusHunt extends PanacheMongoEntity {

    @NotNull(message = "A caça aos bónus deve pertencer a uma sessão")
    public ObjectId sessionId;

    @NotBlank(message = "O nome da Bonus Hunt é obrigatório")
    public String name;

    public double startAmount;

    // Na v2.0, usamos instâncias de uma classe interna ou dedicada para os slots da hunt
    public List<BonusHuntSlot> slots = new ArrayList<>();

    public Instant createdAt = Instant.now();

    // Substituímos o boolean 'ativo' por um Enum para melhor controlo de estado
    public HuntStatus status = HuntStatus.OPEN;

    public enum HuntStatus {
        OPEN,       // A adicionar slots
        COLLECTING, // A abrir os bónus em live
        FINISHED    // Hunt concluída
    }

    // --- Lógica de Domínio (Business Logic) ---

    /**
     * O Break Even Inicial é puramente calculado: Dinheiro gasto / número de slots.
     * Não deve ser uma coluna na DB para evitar dessincronização.
     */
    public double getInitialBreakEven() {
        if (slots.isEmpty()) return 0;
        return startAmount / slots.size();
    }

    /**
     * Calcula quanto falta recuperar para sair em lucro (Real-time).
     */
    public double getRemainingToBreakEven() {
        double totalWon = slots.stream().mapToDouble(s -> s.winAmount).sum();
        return Math.max(0, startAmount - totalWon);
    }

    public double getTotalWon() {
        return slots.stream().mapToDouble(s -> s.winAmount).sum();
    }
}