package com.casino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiveStreamStatsDTO {
    public String sessionTitle;
    public double initialBalance;
    public double currentBalance;
    public double totalWagered;
    public double totalWon;
    public double netPnl;
    public double luckFactor; // (Total Won / Total Wagered) * 100
}