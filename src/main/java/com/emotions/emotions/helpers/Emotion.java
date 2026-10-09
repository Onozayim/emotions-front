package com.emotions.emotions.helpers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Single source of truth for the emotions shown in the UI (filters, badges,
 * charts and the compound-emotion editor). Exposed to every template as
 * ${emotions} and ${emotionsByKey} by {@link GlobalModelAdvice}.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Emotion {
    private final String key;
    private final String label;
    /**
     * Primary emotions use the first five slots of a colorblind-safe categorical
     * palette, in this fixed order (the order is what keeps adjacent colors
     * distinguishable - don't reorder). Compound emotions blend the colors of
     * their two components.
     */
    private final String color;
    /** Text color (white or near-black) with the best contrast on {@link #color}. */
    private final String textColor;
    /** The two primary emotions that combine into this one; empty for primary emotions. */
    private final List<String> components;

    private static final Emotion JOY = primary("joy", "Joy", "#2a78d6");
    private static final Emotion SADNESS = primary("sadness", "Sadness", "#eb6834");
    private static final Emotion ANGER = primary("anger", "Anger", "#1baf7a");
    private static final Emotion FEAR = primary("fear", "Fear", "#eda100");
    private static final Emotion DISGUST = primary("disgust", "Disgust", "#e87ba4");

    public static final List<Emotion> ALL = List.of(
            JOY, SADNESS, ANGER, FEAR, DISGUST,
            compound("surprise", "Surprise", JOY, FEAR),
            compound("nostalgia", "Nostalgia", JOY, SADNESS),
            compound("intrigue", "Intrigue", JOY, DISGUST),
            compound("justice", "Justice", JOY, ANGER),
            compound("contempt", "Contempt", SADNESS, DISGUST),
            compound("anxiety", "Anxiety", SADNESS, FEAR),
            compound("betrayal", "Betrayal", SADNESS, ANGER),
            compound("repulsion", "Repulsion", DISGUST, FEAR),
            compound("aversion", "Aversion", DISGUST, ANGER),
            compound("hate", "Hate", FEAR, ANGER));

    public static final Map<String, Emotion> BY_KEY = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(Emotion::getKey, Function.identity()));

    public boolean isCompound() {
        return !components.isEmpty();
    }

    private static Emotion primary(String key, String label, String color) {
        return new Emotion(key, label, color, Colors.readableTextOn(color), List.of());
    }

    private static Emotion compound(String key, String label, Emotion first, Emotion second) {
        String color = Colors.mix(first.color, second.color);
        return new Emotion(key, label, color, Colors.readableTextOn(color), List.of(first.key, second.key));
    }

    /** Color math for the palette above. */
    private static final class Colors {
        private static final String WHITE = "#ffffff";
        private static final String INK = "#0b0b0b";

        /** Average of two colors in OKLab, so blends keep the brightness of their parents. */
        static String mix(String first, String second) {
            double[] a = toOklab(first);
            double[] b = toOklab(second);
            return fromOklab(new double[] { (a[0] + b[0]) / 2, (a[1] + b[1]) / 2, (a[2] + b[2]) / 2 });
        }

        /** White or near-black, whichever has the higher WCAG contrast against the background. */
        static String readableTextOn(String background) {
            double luminance = luminance(background);
            double withWhite = 1.05 / (luminance + 0.05);
            double withInk = (luminance + 0.05) / (luminance(INK) + 0.05);
            return withWhite >= withInk ? WHITE : INK;
        }

        private static double luminance(String hex) {
            double[] rgb = linearRgb(hex);
            return 0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2];
        }

        private static double[] linearRgb(String hex) {
            double[] rgb = new double[3];
            for (int i = 0; i < 3; i++) {
                double c = Integer.parseInt(hex.substring(1 + i * 2, 3 + i * 2), 16) / 255.0;
                rgb[i] = c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
            }
            return rgb;
        }

        private static double[] toOklab(String hex) {
            double[] rgb = linearRgb(hex);
            double l = Math.cbrt(0.4122214708 * rgb[0] + 0.5363325363 * rgb[1] + 0.0514459929 * rgb[2]);
            double m = Math.cbrt(0.2119034982 * rgb[0] + 0.6806995451 * rgb[1] + 0.1073969566 * rgb[2]);
            double s = Math.cbrt(0.0883024619 * rgb[0] + 0.2817188376 * rgb[1] + 0.6299787005 * rgb[2]);
            return new double[] {
                    0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s,
                    1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s,
                    0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s };
        }

        private static String fromOklab(double[] lab) {
            double l = Math.pow(lab[0] + 0.3963377774 * lab[1] + 0.2158037573 * lab[2], 3);
            double m = Math.pow(lab[0] - 0.1055613458 * lab[1] - 0.0638541728 * lab[2], 3);
            double s = Math.pow(lab[0] - 0.0894841775 * lab[1] - 1.2914855480 * lab[2], 3);
            double[] rgb = {
                    4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
                    -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
                    -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s };

            StringBuilder hex = new StringBuilder("#");
            for (double c : rgb) {
                double srgb = c <= 0.0031308 ? 12.92 * c : 1.055 * Math.pow(c, 1 / 2.4) - 0.055;
                hex.append(String.format("%02x", Math.round(Math.min(1, Math.max(0, srgb)) * 255)));
            }
            return hex.toString();
        }
    }
}
