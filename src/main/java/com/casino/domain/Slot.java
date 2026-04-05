package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "slots_catalog")
public class Slot extends PanacheMongoEntity {

    @NotBlank(message = "O nome da slot é obrigatório")
    public String name;

    @NotBlank(message = "O provider é obrigatório")
    public String provider;

    // URL da imagem no Cloudinary
    public String imageUrl;

    // Novo campo para v2.0: RTP (Return to Player) teórico para cálculos de Luck Factor
    public Double theoreticalRtp;

    // Método utilitário para garantir que nomes são guardados em formato padrão
    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}