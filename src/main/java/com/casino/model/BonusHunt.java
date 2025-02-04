package com.casino.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

public class BonusHunt extends PanacheMongoEntity {
    @BsonProperty("name")
    public String name;

    @BsonProperty("start_amount")
    public double startAmount;

    @BsonProperty("break_even")
    public double breakEven;

    @BsonProperty("slots")
    public List<Slot> slots;  // 🔹 Adicione este campo
}
