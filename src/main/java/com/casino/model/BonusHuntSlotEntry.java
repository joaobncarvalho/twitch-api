package com.casino.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;

public class BonusHuntSlotEntry extends PanacheMongoEntity {

    @BsonProperty("bonus_hunt_id")
    public ObjectId bonusHuntId;

    @BsonProperty("slot_id")
    public ObjectId slotId;

    @BsonProperty("bet")
    public double bet;

    @BsonProperty("win")
    public double win;

    @BsonProperty("extra_scatters")
    public boolean extraScatters;

    @BsonProperty("superMode")
    public boolean superMode;

    @BsonProperty("createdAt")
    public Instant createdAt;
}
