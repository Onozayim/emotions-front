package com.emotions.emotions.helpers;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Single source of truth for the emotions shown in the UI (filters, badges,
 * charts and the compound-emotion editor). Exposed to every template as
 * ${emotions} by {@link EmotionModelAdvice}.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Emotion {
    private final String key;
    private final String label;
    private final String color;
    private final String borderColor;
    /** The two primary emotions that combine into this one; empty for primary emotions. */
    private final List<String> components;

    public static final List<Emotion> ALL = List.of(
            primary("joy", "Joy", "#FFD54F", "#FFC107"),
            primary("sadness", "Sadness", "#64B5F6", "#1E88E5"),
            primary("anger", "Anger", "#EF5350", "#D32F2F"),
            primary("fear", "Fear", "#7E57C2", "#5E35B1"),
            primary("disgust", "Disgust", "#26A69A", "#00897B"),

            compound("surprise", "Surprise", "#FFD54F", "#FFC107", "joy", "fear"),
            compound("nostalgia", "Nostalgia", "#64B5F6", "#1E88E5", "joy", "sadness"),
            compound("intrigue", "Intrigue", "#EF5350", "#D32F2F", "joy", "disgust"),
            compound("justice", "Justice", "#7E57C2", "#5E35B1", "joy", "anger"),
            compound("contempt", "Contempt", "#26A69A", "#00897B", "sadness", "disgust"),
            compound("anxiety", "Anxiety", "#FFD54F", "#FFC107", "sadness", "fear"),
            compound("betrayal", "Betrayal", "#64B5F6", "#1E88E5", "sadness", "anger"),
            compound("repulsion", "Repulsion", "#EF5350", "#D32F2F", "disgust", "fear"),
            compound("aversion", "Aversion", "#7E57C2", "#5E35B1", "disgust", "anger"),
            compound("hate", "Hate", "#26A69A", "#00897B", "fear", "anger"));

    public boolean isCompound() {
        return !components.isEmpty();
    }

    private static Emotion primary(String key, String label, String color, String borderColor) {
        return new Emotion(key, label, color, borderColor, List.of());
    }

    private static Emotion compound(String key, String label, String color, String borderColor,
            String first, String second) {
        return new Emotion(key, label, color, borderColor, List.of(first, second));
    }
}
