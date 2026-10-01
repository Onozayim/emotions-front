package com.emotions.emotions.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateEmailDto {
    @NotBlank(message = "The primary emotion is required")
    private String primaryEmotion;

    @NotBlank(message = "The secondary emotion is required")
    private String secondaryEmotion;

    @NotBlank(message = "The compound emotion is required")
    private String compoundEmotion;
}
