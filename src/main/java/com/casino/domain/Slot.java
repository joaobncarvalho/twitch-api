package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "Slot")
@JsonIgnoreProperties(ignoreUnknown = true) // <-- Adicionei isto para ignorar lixo antigo no Mongo
public class Slot extends PanacheMongoEntity {

    @NotBlank(message = "O nome da slot é obrigatório")
    public String name;

    @NotBlank(message = "O provider é obrigatório")
    public String provider;

    // A MAGIA DO TECHLEAD AQUI 👇
    // Usar "Number" faz com que o Java aceite tanto "96" como "96.5"
    public Number rtp;

    // A mesma regra aplica-se ao maxWin (que também tem both Integers e Doubles)
    public Number maxWin;

    public String imageUrl;

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}