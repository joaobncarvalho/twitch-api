package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "single_player_games")
public class SinglePlayerGame extends PanacheMongoEntity {

    @NotBlank(message = "O nome da slot é obrigatório")
    public String slotName;

    @Positive(message = "A aposta deve ser positiva")
    public double bet;

    public double win;

    public Instant createdAt = Instant.now();

    @NotBlank(message = "O jogo deve pertencer a uma sessão")
    public ObjectId sessionId;

    // Lógica de Domínio: Multiplicador
    public double getMultiplier() {
        return (win > 0 && bet > 0) ? (win / bet) : 0;
    }
}