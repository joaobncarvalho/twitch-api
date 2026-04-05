package com.casino.domain;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.types.ObjectId;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SessionActivity extends PanacheMongoEntity {
    public ObjectId sessionId;
    public String slotName;
    public double bet;
    public double win;
    public Instant createdAt = Instant.now();
}