package com.casino.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "widget_settings")
public class WidgetState extends PanacheMongoEntity {

    // Identificador único (ex: "main_widget")
    public String widgetKey;

    public String currentSlotName;
    public String lastMessage;

    public static WidgetState findMainWidget() {
        return find("widgetKey", "main_widget").firstResult();
    }
}