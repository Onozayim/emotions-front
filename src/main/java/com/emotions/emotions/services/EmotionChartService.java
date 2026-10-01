package com.emotions.emotions.services;

import java.io.IOException;

import com.emotions.emotions.entities.EmotionSummarySnapshot;

public interface EmotionChartService {
    byte[] generateChart(EmotionSummarySnapshot snapshot) throws IOException;
}
