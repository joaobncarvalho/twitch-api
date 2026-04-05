package com.casino.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class BonusHuntSlot {
    public String slotName;
    public double betSize;
    public double winAmount;
    public boolean collected = false;
}