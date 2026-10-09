package com.emotions.emotions.helpers;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Single source of truth for the emotions shown in the UI (filters, badges,
 * charts and the compound-emotion editor). Exposed to every template as
 * ${emotions} by {@link GlobalModelAdvice}.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Emotion {
    private final String key;
    private final String label;
    /**
     * Chart color for primary emotions: the first five slots of a colorblind-safe
     * categorical palette, in this fixed order (the order is what keeps adjacent
     * colors distinguishable - don't reorder or add slots). Compound emotions have
     * none: ten series are too many for color, so their chart highlights one at a time.
     */
    private final String color;
    /** The two primary emotions that combine into this one; empty for primary emotions. */
    private final List<String> components;

    public static final List<Emotion> ALL = List.of(
            primary("joy", "Joy", "#2a78d6"),
            primary("sadness", "Sadness", "#eb6834"),
            primary("anger", "Anger", "#1baf7a"),
            primary("fear", "Fear", "#eda100"),
            primary("disgust", "Disgust", "#e87ba4"),

            compound("surprise", "Surprise", "joy", "fear"),
            compound("nostalgia", "Nostalgia", "joy", "sadness"),
            compound("intrigue", "Intrigue", "joy", "disgust"),
            compound("justice", "Justice", "joy", "anger"),
            compound("contempt", "Contempt", "sadness", "disgust"),
            compound("anxiety", "Anxiety", "sadness", "fear"),
            compound("betrayal", "Betrayal", "sadness", "anger"),
            compound("repulsion", "Repulsion", "disgust", "fear"),
            compound("aversion", "Aversion", "disgust", "anger"),
            compound("hate", "Hate", "fear", "anger"));

    public boolean isCompound() {
        return !components.isEmpty();
    }

    private static Emotion primary(String key, String label, String color) {
        return new Emotion(key, label, color, List.of());
    }

    private static Emotion compound(String key, String label, String first, String second) {
        return new Emotion(key, label, null, List.of(first, second));
    }
}
