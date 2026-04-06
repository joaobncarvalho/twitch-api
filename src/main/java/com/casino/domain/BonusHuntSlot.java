package com.casino.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@AllArgsConstructor
public class BonusHuntSlot {
    @BsonProperty("name")
    @JsonProperty("name")
    public String slotName;

    @BsonProperty("bet")
    @JsonProperty("bet") // O React envia 'betSize', o Mongo grava 'bet'
    public Double betSize;

    @BsonProperty("win")
    @JsonProperty("win")
    public Double winAmount = 0.0;

    @BsonProperty("imageUrl")
    public String imageUrl;

    // Campos de bónus que pediste
    @BsonProperty("superMode")
    @JsonProperty("superMode")
    public boolean superMode = false;

    @BsonProperty("extraScatters")
    @JsonProperty("extraScatters")
    public boolean extraScatters = false;

    @BsonProperty("collected")
    public boolean collected = false;

    public BonusHuntSlot() {}
}