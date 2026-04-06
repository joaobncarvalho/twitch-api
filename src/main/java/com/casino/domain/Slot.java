package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import org.bson.codecs.pojo.annotations.BsonProperty; // <-- Import do BSON
import com.fasterxml.jackson.annotation.JsonProperty; // <-- Import do JSON (React)
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "Slot")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Slot extends PanacheMongoEntity {

    @NotBlank(message = "O nome da slot é obrigatório")
    @BsonProperty("name")
    @JsonProperty("name")
    public String name;

    @NotBlank(message = "O provider é obrigatório")
    @BsonProperty("provider")
    @JsonProperty("provider")
    public String provider;

    // A Blindagem Perfeita: Obriga a mapear para 'rtp' na BD e no React
    @BsonProperty("rtp")
    @JsonProperty("rtp")
    public Double rtp; // Limpinho como Double!

    @BsonProperty("maxWin")
    @JsonProperty("maxWin")
    public Double maxWin;

    @BsonProperty("imageUrl")
    @JsonProperty("imageUrl")
    public String imageUrl;

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}