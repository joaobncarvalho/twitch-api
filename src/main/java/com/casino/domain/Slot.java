package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "Slot")
public class Slot extends PanacheMongoEntity {

    @NotBlank(message = "O nome da slot é obrigatório")
    public String name;
    @NotBlank(message = "O provider é obrigatório")
    public String provider;
    public double rtp;
    public String imageUrl;
    public int maxWin;

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}