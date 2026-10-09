package com.emotions.emotions.helpers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class EmotionModelAdvice {

    @ModelAttribute("emotions")
    public List<Emotion> emotions() {
        return Emotion.ALL;
    }
}
