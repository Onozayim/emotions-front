package com.emotions.emotions.entities;

public record EmailCountDtoSum (
    Long totalJoy,
    Long totalSadness,
    Long totalAnger,
    Long totalFear,
    Long totalLove,
    Long totalSurprise 
) {}