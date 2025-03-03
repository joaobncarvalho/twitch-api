package com.casino.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

@Data
public class BonusHunt extends PanacheMongoEntity {
    public String name;

    @BsonProperty("start_amount")
    @JsonProperty("start_amount")
    public double startAmount = 0;  // 🔹 Defina um valor padrão para evitar problemas

    @BsonProperty("break_even_inicial")
    public double breakEvenInicial = 0;

    @BsonProperty("break_even_atual")
    public double breakEvenAtual = 0;

    @BsonProperty("slots")
    public List<SlotEntry> slots = new ArrayList<>();  // 🔹 Evita `null`




    // 🔹 Construtor Padrão Obrigatório para Quarkus
    public BonusHunt() {
    }
}
