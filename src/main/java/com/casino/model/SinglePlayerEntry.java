package com.casino.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.sql.Timestamp;
import java.time.Instant;

public class SinglePlayerEntry extends PanacheMongoEntity {
    public String id;
    public String slotName;
    public double bet;
    public double win;
    @BsonProperty("createdAt")
    public Instant createdAt;
}
