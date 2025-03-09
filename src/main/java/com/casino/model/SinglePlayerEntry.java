package com.casino.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;

public class SinglePlayerEntry extends PanacheMongoEntity {
    public String id;
    public String slotName;
    public double bet;
    public double win;
}
