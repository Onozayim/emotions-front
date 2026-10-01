package com.emotions.emotions.impls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.springframework.stereotype.Service;

import com.emotions.emotions.entities.EmotionSummary;
import com.emotions.emotions.entities.EmotionSummarySnapshot;
import com.emotions.emotions.services.EmotionChartService;

@Service 
public class EmotionChartServiceImpl implements EmotionChartService {
    @Override
    public byte[] generateChart(EmotionSummarySnapshot snapshot) throws IOException {
        PieChart chart = new PieChartBuilder()
                .width(500)
                .height(400)
                .title("Your Email Emotions")
                .build();

        for (EmotionSummary emotion : snapshot.getEmotions()) {

            if (emotion.getCount() > 0) {

                chart.addSeries(
                        capitalize(emotion.getEmotion()),
                        emotion.getCount());
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BitmapEncoder.saveBitmap(
                chart,
                outputStream,
                BitmapEncoder.BitmapFormat.PNG);

        return outputStream.toByteArray();
    }

    private String capitalize(String value) {

        if (value == null || value.isBlank()) {
            return value;
        }

        return value.substring(0, 1).toUpperCase()
                + value.substring(1).toLowerCase();
    }
}
