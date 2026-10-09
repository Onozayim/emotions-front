package com.emotions.emotions.helpers;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/** Model attributes shared by every page (used by the layout and fragments). */
@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAdvice {

    @ModelAttribute("emotions")
    public List<Emotion> emotions() {
        return Emotion.ALL;
    }

    @ModelAttribute("emotionsByKey")
    public Map<String, Emotion> emotionsByKey() {
        return Emotion.BY_KEY;
    }

    /** Request path without the context path, used to highlight the active navbar link. */
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }
}
