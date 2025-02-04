package com.casino.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Slot extends PanacheMongoEntity {

    @BsonProperty("name")
    public String name;

    @BsonProperty("max_win")
    public double maxWin;

    @BsonProperty("rtp")
    public double rtp;

    @BsonProperty("volatility")
    public String volatility;

    @BsonProperty("provider")
    public String provider;

    // Novos atributos
    @BsonProperty("bonus_profit")
    public double bonusProfit;  // % esperado ao ativar bônus

    @BsonProperty("best_win")
    public double bestWin;  // Maior ganho registrado nesta slot

    @BsonProperty("played")
    public int played;  // Número de vezes jogadas
}
