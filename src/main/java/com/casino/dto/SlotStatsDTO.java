package com.casino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transporte de estatísticas consolidadas de uma Slot específica.
 * Utilizado pelo StatisticsService para enviar dados limpos ao Frontend.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotStatsDTO {

    public String slotName;

    public double averageMultiplier;

    public double bestMultiplier;

    public double bestWin;

    // Adicionei este campo pois será essencial para o teu Dashboard 2.0
    // mostrar a relevância estatística (ex: "Baseado em 50 bónus")
    public int totalEntries;

    /**
     * Construtor auxiliar para bater exatamente com a tua chamada no StatisticsService
     */
    public SlotStatsDTO(String slotName, double averageMultiplier, double bestMultiplier, double bestWin) {
        this.slotName = slotName;
        this.averageMultiplier = averageMultiplier;
        this.bestMultiplier = bestMultiplier;
        this.bestWin = bestWin;
    }
}