package com.casino.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class SlotEntry {

    @BsonProperty("bonus_hunt_id")
    public ObjectId bonusHuntId;

    @BsonProperty("slot_id")
    @JsonProperty("slot_id")
    public ObjectId slotId;

    @BsonProperty("name")  // 🔹 Adiciona o nome no modelo
    public String name;

    @BsonProperty("bet")
    public double bet;

    @BsonProperty("win")
    public double win;

    @BsonProperty("extraScatters")
    public boolean extraScatters;

    @BsonProperty("superMode")
    public boolean superMode;

    @BsonProperty("imageUrl")
    public String imageUrl;
}

