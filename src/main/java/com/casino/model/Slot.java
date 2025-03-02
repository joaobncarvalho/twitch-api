package com.casino.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class Slot extends PanacheMongoEntity {
    @BsonProperty("name")
    public String name;

    @BsonProperty("provider")
    public String provider;

    @BsonProperty("rtp")
    public double rtp;

    @BsonProperty("maxWin")
    public double maxWin;

    @BsonProperty("imageBase64")
    public String imageBase64;

    @Override
    public String toString() {
        return "Slot{" +
                "name='" + name + '\'' +
                ", provider='" + provider + '\'' +
                ", rtp=" + rtp +
                ", maxWin=" + maxWin +
                ", imageBase64='" + (imageBase64 != null ? "EXISTS" : "NULL") + '\'' +
                '}';
    }
}


