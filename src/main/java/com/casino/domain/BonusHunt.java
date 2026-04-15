package com.casino.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "BonusHunt")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BonusHunt extends PanacheMongoEntity {

    @NotNull(message = "A caça aos bónus deve pertencer a uma sessão")
    @BsonProperty("session_id")
    @JsonProperty("sessionId")
    public double getInitialBreakEven() {

    public ObjectId sessionId;

    @NotBlank(message = "O nome da Bonus Hunt é obrigatório")
    public String name;

    @BsonProperty("start_amount")
    @JsonProperty("startAmount")
    public double startAmount;

    @BsonProperty("break_even_inicial")
    @JsonProperty("initialBreakEven")
    public double initialBreakEven;

    @BsonProperty("totalWon")
    @JsonProperty("totalWon")
    public double totalWon;

    public List<BonusHuntSlot> slots = new ArrayList<>();

    @BsonProperty("ativo")
    public boolean active = true;

    public Instant createdAt = Instant.now();

    public HuntStatus status = HuntStatus.OPEN;

    public enum HuntStatus {
        OPEN, COLLECTING, FINISHED
    }

    // --- Helpers para o Frontend ---

    @JsonProperty("id") // Garante que o React veja 'id' e não um objeto complexo
    public String getHexId() {
        return id != null ? id.toHexString() : null;
    }

    @JsonProperty("initialBreakEven")
    public double getInitialBreakEven() {
        if (slots == null || slots.isEmpty()) return 0;
        return startAmount / slots.size();
    }
}