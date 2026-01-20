package com.emotions.emotions.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paginator {
    Paginator(int totalPages) {
        this.totalPages = totalPages;
    }

    int totalPages;
    boolean prevDots = false;
    boolean nextDots = false;

    List<Integer> pageNumbers;
}
